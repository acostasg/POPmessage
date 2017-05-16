package costas.albert.popmessage.task;

import android.app.ProgressDialog;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.TokenMapper;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;


public class ValidationTask extends AsyncHttpResponseHandler {

    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private ProgressDialog dialog;
    private LoginActivity mContext;
    private Session session;

    private ValidationTask(LoginActivity mContext) {
        this.mContext = mContext;
        this.session = new Session(this.mContext);
    }

    @Override
    public void onStart() {
        this.dialog = new ProgressDialog(mContext);
        this.dialog.setCancelable(false);
        this.dialog.setMessage("Validation session...");
        this.dialog.show();
    }

    public static void execute(LoginActivity loginActivity) {
        Token token = new Session(loginActivity).getToken();
        if (!token.isEmpty()) {
            RestClient.get(
                    ApiValues.TOKEN_VALIDATION_END_POINT,
                    new RequestParams(),
                    new ValidationTask(loginActivity),
                    token
            );
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Token token = TokenMapper.build(responseBody);
            if (!token.isEmpty()) {
                this.session.setToken(token);
                GetUserTask.execute(this.mContext, token);
            } else {
                session.resetToken();
            }
        } catch (java.io.IOException | org.json.JSONException exception) {
            session.resetToken();
        }
        this.dialog.hide();
        this.dialog.cancel();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext, this.dialog);
    }
}

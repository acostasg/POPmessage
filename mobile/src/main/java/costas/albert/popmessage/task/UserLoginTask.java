package costas.albert.popmessage.task;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.session.Session;
import cz.msebera.android.httpclient.Header;

public class UserLoginTask extends AsyncHttpResponseHandler {

    private LoginActivity mContext;
    private Session session;

    public UserLoginTask(LoginActivity mContext) {
        this.mContext = mContext;
        this.session = new Session(this.mContext);
    }

    public static void execute(LoginActivity mContext, String email, String password) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(ApiValues.USERNAME, email);
        requestParams.add(ApiValues.USERNAME, password);
        RestClient.get(
                ApiValues.TOKEN_VALIDATION_END_POINT,
                requestParams,
                new UserLoginTask(mContext)
        );
    }

    @Override
    public void onCancel() {
        super.onCancel();
        this.mContext.showProgress(false);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Token token = new Token(responseBody.toString());
        this.mContext.mPasswordView.setError(responseBody.toString());
        if (!token.isEmpty()) {
            this.session.setToken(token);
            this.mContext.finish();
            this.mContext.sendMessagesView();

        } else {
            session.resetToken();
        }
        this.mContext.showProgress(false);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        this.mContext.showProgress(false);
        this.mContext.mPasswordView.setError(this.mContext.getString(R.string.error_incorrect_password));
        this.mContext.mPasswordView.requestFocus();
    }
}
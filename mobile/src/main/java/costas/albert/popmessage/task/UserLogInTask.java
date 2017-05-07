package costas.albert.popmessage.task;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.TokenMapper;
import costas.albert.popmessage.session.Session;
import cz.msebera.android.httpclient.Header;

public class UserLogInTask extends AsyncHttpResponseHandler {

    private LoginActivity mContext;
    private Session session;

    private UserLogInTask(LoginActivity mContext) {
        this.mContext = mContext;
        this.session = new Session(this.mContext);
    }

    public static void execute(LoginActivity mContext, String email, String password) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(ApiValues.USERNAME, email);
        requestParams.add(ApiValues.PASSWORD, password);
        RestClient.get(
                ApiValues.LOGIN_END_POINT,
                requestParams,
                new UserLogInTask(mContext)
        );
    }

    @Override
    public void onCancel() {
        super.onCancel();
        this.mContext.showProgress(false);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Token token = TokenMapper.build(responseBody);
            if (!token.isEmpty()) {
                this.session.setToken(token);
                this.mContext.finish();
                this.mContext.sendMessagesView();

            } else {
                invalidCredentials();
            }
        } catch (java.io.IOException | org.json.JSONException exception) {
            popAlertConnection(this.mContext.getString(R.string.wrong_server_end)
                    + " [" + exception.getMessage() + ']');
        }

        this.mContext.showProgress(false);
    }

    private void invalidCredentials() {
        session.resetToken();
        this.mContext.mPasswordView.setError(this.mContext.getString(R.string.error_incorrect_password));
        this.mContext.mPasswordView.requestFocus();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        this.mContext.showProgress(false);
        String message;
        switch (statusCode) {
            case 401:
                invalidCredentials();
                return;
            case 404:
                message = this.mContext.getString(R.string.requested_not_found);
                break;
            case 500:
                message = this.mContext.getString(R.string.wrong_server_end);
                break;
            default:
                message = this.mContext.getString(R.string.unexpected_error);
        }
        popAlertConnection(message);
    }

    private void popAlertConnection(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(this.mContext.getString(R.string.alert));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, this.mContext.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }
}
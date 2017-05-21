package costas.albert.popmessage.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.util.Log;

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

    private static UserLogInTask instance;
    private LoginActivity mContext;
    private Session session;
    private AlertDialog alertDialog;

    private UserLogInTask() {
    }

    private static UserLogInTask getInstance(LoginActivity mContext) {
        if (instance == null)
            instance = new UserLogInTask();
        instance.setContext(mContext);
        return instance;
    }

    public static void execute(LoginActivity mContext, String email, String password) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(ApiValues.USERNAME, email);
        requestParams.add(ApiValues.PASSWORD, password);
        RestClient.get(
                ApiValues.LOGIN_END_POINT,
                requestParams,
                getInstance(mContext)
        );
    }

    private void setContext(LoginActivity mContext) {
        synchronized (this) {
            this.mContext = mContext;
            this.session = new Session(this.mContext);
            this.alertDialog = new AlertDialog.Builder(mContext).create();
        }
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
                GetUserTask.execute(this.mContext, token);
            } else {
                invalidCredentials();
            }
        } catch (java.io.IOException | org.json.JSONException exception) {
            Snackbar.make(
                    this.mContext.findViewById(android.R.id.content).getRootView(),
                    this.mContext.getString(R.string.wrong_server_end),
                    Snackbar.LENGTH_LONG
            ).show();
            Log.d(this.getClass().getSimpleName(), exception.getMessage());
        }
        this.mContext.sendMessagesView();
        this.mContext.showProgress(false);
        this.mContext.finish();
    }

    private void invalidCredentials() {
        session.resetSession();
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
        this.alertDialog.setTitle(this.mContext.getString(R.string.alert));
        this.alertDialog.setMessage(message);
        this.alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, this.mContext.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        this.alertDialog.show();
    }
}
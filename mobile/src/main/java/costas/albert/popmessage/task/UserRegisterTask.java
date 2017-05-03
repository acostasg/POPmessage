package costas.albert.popmessage.task;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.R;
import costas.albert.popmessage.RegisterActivity;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.User;
import costas.albert.popmessage.entity.mapper.UserMapper;
import costas.albert.popmessage.session.Session;
import cz.msebera.android.httpclient.Header;

public class UserRegisterTask extends AsyncHttpResponseHandler {

    private RegisterActivity mContext;
    private Session session;

    public UserRegisterTask(RegisterActivity mContext) {
        this.mContext = mContext;
        this.session = new Session(this.mContext);
    }

    public static void execute(
            RegisterActivity mContext,
            String email,
            String password,
            String name,
            String dateOfBirth,
            boolean acceptedPolicy
    ) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(ApiValues.USERNAME, email);
        requestParams.add(ApiValues.PASSWORD, password);
        requestParams.add(ApiValues.NAME, name);
        requestParams.add(ApiValues.DATE_OF_BIRTH, dateOfBirth);
        requestParams.add(ApiValues.PRIVACY_POLICY, String.valueOf(acceptedPolicy));
        RestClient.post(
                ApiValues.REGISTER_END_POINT,
                requestParams,
                new UserRegisterTask(mContext)
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
            User user = UserMapper.build(responseBody);
            if (user != null) {
                this.mContext.finish();
                this.mContext.sendLogin();
                this.session.setUser(user);
            } else {
                invalidCredentials();
            }
        } catch (java.io.IOException | org.json.JSONException exception) {
            popAlertConnection(this.mContext.getString(R.string.wrong_server_end) + " [" + exception.getMessage() + ']');
        }

        this.mContext.showProgress(false);
    }

    private void invalidCredentials() {
        this.mContext.mNameView.setError(this.mContext.getString(R.string.invalid_params));
        this.mContext.mNameView.requestFocus();
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
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }
}
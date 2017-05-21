package costas.albert.popmessage.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.util.Log;

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

    private static UserRegisterTask instance;
    private RegisterActivity mContext;
    private Session session;
    private AlertDialog alertDialog;

    private UserRegisterTask() {
    }

    private static UserRegisterTask getInstance(RegisterActivity mContext) {
        if (instance == null)
            instance = new UserRegisterTask();
        instance.setContext(mContext);
        return instance;
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
                getInstance(mContext)
        );
    }

    private void setContext(RegisterActivity mContext) {
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
            User user = UserMapper.build(responseBody);
            if (user != null) {
                this.mContext.finish();
                this.mContext.sendLogin();
                this.session.setUser(user);
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
            case 409:
                this.mContext.emailInUse();
                return;
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
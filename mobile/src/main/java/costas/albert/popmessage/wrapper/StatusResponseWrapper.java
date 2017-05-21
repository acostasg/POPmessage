package costas.albert.popmessage.wrapper;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import costas.albert.popmessage.R;

public class StatusResponseWrapper {

    public void onFailure(int statusCode, AppCompatActivity mContext) {
        String messageError;
        switch (statusCode) {
            case 401:
                messageError = mContext.getString(R.string.invalid_credential);
                break;
            case 404:
                messageError = mContext.getString(R.string.requested_not_found);
                break;
            case 500:
                messageError = mContext.getString(R.string.wrong_server_end);
                break;
            default:
                messageError = mContext.getString(R.string.unexpected_error);
        }
        Snackbar.make(
                mContext.findViewById(android.R.id.content).getRootView(),
                messageError,
                Snackbar.LENGTH_LONG
        ).show();
    }
}
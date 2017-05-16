package costas.albert.popmessage.wrapper;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import costas.albert.popmessage.R;

public class StatusResponseWrapper {

    public void onFailure(int statusCode, AppCompatActivity appCompatActivity, ProgressDialog progressDialog) {

        switch (statusCode) {
            case 401:
                progressDialog.setMessage(appCompatActivity.getString(R.string.invalid_credential));
                break;
            case 404:
                progressDialog.setMessage(appCompatActivity.getString(R.string.requested_not_found));
                break;
            case 500:
                progressDialog.setMessage(appCompatActivity.getString(R.string.wrong_server_end));
                break;
            default:
                progressDialog.setMessage(appCompatActivity.getString(R.string.unexpected_error));
        }
        progressDialog.setCancelable(true);
    }
}
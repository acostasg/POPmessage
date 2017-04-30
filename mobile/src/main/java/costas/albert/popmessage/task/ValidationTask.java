package costas.albert.popmessage.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.CheckedTextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.session.Session;
import cz.msebera.android.httpclient.Header;


public class ValidationTask extends AsyncHttpResponseHandler {

    private ProgressDialog dialog;
    private Context mContext;

    public ValidationTask(Context mContext) {
        this.mContext = mContext;
    }

    public void onStart() {
        this.dialog = new ProgressDialog(mContext);
        this.dialog.setCancelable(false);
        this.dialog.setMessage("Validation session...");
        this.dialog.show();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        this.dialog.hide();

        Session session = new Session(this.mContext);

        Token token = new Token(responseBody.toString());
        if (!token.isEmpty()) {
            session.setToken(token.hash());
        } else {
            session.resetToken();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        this.dialog.hide();

        if (statusCode == 404) {
            this.dialog.setMessage("Requested resource not found");
        } else if (statusCode == 500) {
            this.dialog.setMessage("Something went wrong at server end");
        } else {
            this.dialog.setMessage("Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]");
        }
    }
}

package costas.albert.popmessage.task;

import android.content.Intent;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.MessagesActivity;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.session.Session;
import cz.msebera.android.httpclient.Header;

public class UserLogOutTask extends AsyncHttpResponseHandler {

    private MessagesActivity mContext;
    private Session session;

    public UserLogOutTask(MessagesActivity mContext) {
        this.mContext = mContext;
        this.session = new Session(this.mContext);
    }

    public static void execute(MessagesActivity mContext) {
        RequestParams requestParams = new RequestParams();
        RestClient.get(
                ApiValues.LOGOUT_END_POINT,
                requestParams,
                new UserLogOutTask(mContext),
                new Session(mContext).getToken()
        );
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        logOut();
    }

    private void logOut() {
        this.session.resetToken();

        Intent intent = new Intent(this.mContext, LoginActivity.class);
        this.mContext.startActivity(intent);
        this.mContext.finish();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        logOut();
    }

}
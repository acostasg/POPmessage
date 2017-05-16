package costas.albert.popmessage.task;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.session.Session;
import cz.msebera.android.httpclient.Header;

public class UserLogOutTask extends AsyncHttpResponseHandler {

    private AppCompatActivity mContext;
    private Session session;

    private UserLogOutTask(AppCompatActivity mContext) {
        this.mContext = mContext;
        this.session = new Session(this.mContext);
    }

    public static void execute(AppCompatActivity mContext) {
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
        this.session.resetSession();

        Intent intent = new Intent(this.mContext, LoginActivity.class);
        this.mContext.startActivity(intent);
        this.mContext.finish();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        logOut();
    }

}
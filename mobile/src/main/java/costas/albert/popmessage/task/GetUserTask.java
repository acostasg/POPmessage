package costas.albert.popmessage.task;

import android.support.design.widget.Snackbar;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.User;
import costas.albert.popmessage.entity.mapper.UserMapper;
import costas.albert.popmessage.session.Session;
import cz.msebera.android.httpclient.Header;

class GetUserTask extends AsyncHttpResponseHandler {

    private static GetUserTask instance;
    private LoginActivity mContext;
    private Session session;

    private GetUserTask() {
    }

    private static GetUserTask getInstance(LoginActivity mContext) {
        if (instance == null)
            instance = new GetUserTask();
        instance.setContext(mContext);
        return instance;
    }

    public static void execute(LoginActivity mContext, Token token) {
        RestClient.get(
                ApiValues.GET_USER,
                new RequestParams(),
                getInstance(mContext),
                token
        );
    }

    private void setContext(LoginActivity mContext) {
        synchronized (this) {
            this.mContext = mContext;
            this.session = new Session(this.mContext);
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
            if (null != user) {
                this.session.setUser(user);
            }
        } catch (java.io.IOException | org.json.JSONException exception) {
            Snackbar.make(
                    this.mContext.findViewById(android.R.id.content).getRootView(),
                    this.mContext.getString(R.string.wrong_server_end),
                    Snackbar.LENGTH_LONG
            ).show();
            Log.d(this.getClass().getSimpleName(), exception.getMessage());
        }
        finishTask();
    }

    private void finishTask() {
        this.mContext.finish();
        this.mContext.sendMessagesView();
        this.mContext.showProgress(false);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        finishTask();
    }
}
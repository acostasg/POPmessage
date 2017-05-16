package costas.albert.popmessage.task;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.User;
import costas.albert.popmessage.entity.mapper.UserMapper;
import costas.albert.popmessage.session.Session;
import cz.msebera.android.httpclient.Header;

public class GetUserTask extends AsyncHttpResponseHandler {

    private LoginActivity mContext;
    private Session session;

    private GetUserTask(LoginActivity mContext) {
        this.mContext = mContext;
        this.session = new Session(this.mContext);
    }

    public static void execute(LoginActivity mContext, Token token) {
        RestClient.get(
                ApiValues.GET_USER,
                new RequestParams(),
                new GetUserTask(mContext),
                token
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
            if (null != user) {
                this.session.setUser(user);
            }
        } catch (java.io.IOException | org.json.JSONException exception) {
            return;
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
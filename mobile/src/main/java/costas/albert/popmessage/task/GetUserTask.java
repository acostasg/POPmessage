package costas.albert.popmessage.task;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.User;
import costas.albert.popmessage.entity.mapper.UserMapper;
import costas.albert.popmessage.services.PrintMessageService;
import costas.albert.popmessage.session.Session;
import cz.msebera.android.httpclient.Header;

class GetUserTask extends AsyncHttpResponseHandler {

    private static GetUserTask instance;
    private final PrintMessageService printMessageService = new PrintMessageService();
    private AppCompatActivity mContext;
    private Session session;

    private GetUserTask() {
    }

    private static GetUserTask getInstance(AppCompatActivity mContext) {
        if (instance == null)
            instance = new GetUserTask();
        instance.setContext(mContext);
        return instance;
    }

    public static void execute(AppCompatActivity mContext, Token token) {
        RestClient.get(
                ApiValues.GET_USER,
                new RequestParams(),
                getInstance(mContext),
                token
        );
    }

    private void setContext(AppCompatActivity mContext) {
        synchronized (this) {
            this.mContext = mContext;
            this.session = new Session(this.mContext);
        }
    }

    @Override
    public void onCancel() {
        super.onCancel();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            User user = UserMapper.build(responseBody);
            if (null != user) {
                this.session.setUser(user);
            }
        } catch (java.io.IOException | org.json.JSONException exception) {
            setMessageError();
            Log.d(this.getClass().getSimpleName(), exception.getMessage());
        }
    }

    private void setMessageError() {
        this.printMessageService.printBarMessage(
                this.mContext.getString(R.string.wrong_server_end),
                this.mContext
        );
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        setMessageError();
        Log.d(this.getClass().getSimpleName(), error.getMessage());
    }
}
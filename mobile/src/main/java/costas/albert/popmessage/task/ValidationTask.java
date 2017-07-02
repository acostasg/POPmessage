package costas.albert.popmessage.task;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.R;
import costas.albert.popmessage.SplashActivity;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.TokenMapper;
import costas.albert.popmessage.services.PrintMessageService;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;


public class ValidationTask extends AsyncHttpResponseHandler {

    private static ValidationTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private final PrintMessageService printMessageService = new PrintMessageService();
    private SplashActivity mContext;
    private Session session;

    private ValidationTask() {
    }

    private static ValidationTask getInstance(SplashActivity mContext) {
        if (instance == null)
            instance = new ValidationTask();
        instance.setContext(mContext);
        return instance;
    }

    public static void execute(SplashActivity splashActivity) {
        Token token = new Session(splashActivity).getToken();
        if (!token.isEmpty()) {
            RestClient.get(
                    ApiValues.TOKEN_VALIDATION_END_POINT,
                    new RequestParams(),
                    getInstance(splashActivity),
                    token
            );
        } else {
            splashActivity.sendLoginView();
        }
    }

    private void setContext(SplashActivity mContext) {
        synchronized (this) {
            this.mContext = mContext;
            this.session = new Session(this.mContext);
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Token token = TokenMapper.build(responseBody);
            if (!token.isEmpty()) {
                this.session.setToken(token);
                GetUserTask.execute(this.mContext, token);
            } else {
                session.resetSession();
            }
        } catch (java.io.IOException | org.json.JSONException exception) {
            session.resetSession();
            this.printMessageService.printBarMessage(
                    this.mContext.getString(R.string.expired_session),
                    this.mContext
            );
            Log.d(this.getClass().getSimpleName(), exception.getMessage());
        }
        this.mContext.sendMessagesView();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext);
        this.mContext.sendLoginView();
    }
}

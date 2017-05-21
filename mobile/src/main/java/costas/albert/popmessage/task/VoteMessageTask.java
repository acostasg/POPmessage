package costas.albert.popmessage.task;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.MessagesActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.MessageMapper;
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;


public class VoteMessageTask extends AsyncHttpResponseHandler {

    private static VoteMessageTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private ProgressDialog dialog;
    private MessagesActivity mContext;

    private VoteMessageTask() {
    }

    private static VoteMessageTask getInstance(MessagesActivity mContext) {
        if (instance == null)
            instance = new VoteMessageTask();
        instance.setContext(mContext);
        return instance;
    }

    public static void executeLike(
            MessagesActivity mContext,
            Message message,
            Token token
    ) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(ApiValues.MESSAGE, String.valueOf(message.Id()));
        RestClient.post(
                ApiValues.POST_MESSAGE_LIKE,
                requestParams,
                getInstance(mContext),
                token
        );
    }

    public static void executeDislike(
            MessagesActivity mContext,
            Message message,
            Token token
    ) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(ApiValues.MESSAGE, String.valueOf(message.Id()));
        RestClient.post(
                ApiValues.POST_MESSAGE_DISLIKE,
                requestParams,
                getInstance(mContext),
                token
        );
    }

    private void setContext(MessagesActivity mContext) {
        synchronized (this) {
            this.mContext = mContext;
            this.dialog = new ProgressDialog(mContext);
        }
    }

    @Override
    public void onStart() {
        this.dialog.setCancelable(false);
        this.dialog.setMessage(this.mContext.getString(R.string.sending_vote));
        this.dialog.show();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Message message = MessageMapper.build(responseBody);
            this.dialog.hide();
            this.dialog.cancel();
            this.mContext.sendMessagesView(message);
        } catch (Exception exception) {
            Snackbar.make(
                    this.mContext.findViewById(android.R.id.content).getRootView(),
                    this.mContext.getString(R.string.wrong_server_end),
                    Snackbar.LENGTH_LONG
            ).show();
            Log.d(this.getClass().getSimpleName(), exception.getMessage());
        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext);
    }
}

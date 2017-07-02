package costas.albert.popmessage.task;

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
import costas.albert.popmessage.services.PrintMessageService;
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;


public class VoteMessageTask extends AsyncHttpResponseHandler {

    private static VoteMessageTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private final PrintMessageService printMessageService = new PrintMessageService();
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
        }
    }

    @Override
    public void onStart() {
        this.printMessageService.printMessage(
                this.mContext.getString(R.string.sending_vote),
                this.mContext
        );
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Message message = MessageMapper.build(responseBody);
            this.mContext.sendMessagesView(message);
        } catch (Exception exception) {
            this.printMessageService.printBarMessage(
                    this.mContext.getString(R.string.wrong_server_end),
                    this.mContext
            );
            Log.d(this.getClass().getSimpleName(), exception.getMessage());
        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext);
    }
}

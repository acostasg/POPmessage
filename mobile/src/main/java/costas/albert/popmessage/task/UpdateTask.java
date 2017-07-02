package costas.albert.popmessage.task;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.MyMessagesActivity;
import costas.albert.popmessage.PublishActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.MessageMapper;
import costas.albert.popmessage.services.PrintMessageService;
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;


public class UpdateTask extends AsyncHttpResponseHandler {

    private static UpdateTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private final PrintMessageService printMessageService = new PrintMessageService();
    private PublishActivity mContext;

    private UpdateTask() {
    }

    private static UpdateTask getInstance(PublishActivity mContext) {
        if (instance == null)
            instance = new UpdateTask();
        instance.setContext(mContext);
        return instance;
    }

    public static void execute(
            PublishActivity mContext,
            String messageId,
            String text,
            Token token
    ) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(ApiValues.TEXT, text);
        requestParams.add(ApiValues.MESSAGE, messageId);
        RestClient.post(
                ApiValues.POST_UPDATE_MESSAGE,
                requestParams,
                getInstance(mContext),
                token
        );
    }

    private void setContext(PublishActivity mContext) {
        synchronized (this) {
            this.mContext = mContext;
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Message message = MessageMapper.build(responseBody);
            this.mContext.sendMessagesView(message, MyMessagesActivity.class);
        } catch (Exception exception) {
            this.printMessageService.printBarMessage(
                    this.mContext.getString(R.string.wrong_server_end),
                    this.mContext
            );
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext);
    }
}

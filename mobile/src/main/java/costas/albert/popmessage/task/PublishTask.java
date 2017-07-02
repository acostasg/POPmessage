package costas.albert.popmessage.task;

import android.location.Location;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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


public class PublishTask extends AsyncHttpResponseHandler {

    private static PublishTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private final PrintMessageService printMessageService = new PrintMessageService();
    private PublishActivity mContext;

    private PublishTask() {
    }

    private static PublishTask getInstance(PublishActivity mContext) {
        if (instance == null)
            instance = new PublishTask();
        instance.setContext(mContext);
        return instance;
    }

    public static void execute(
            PublishActivity mContext,
            String text,
            Location location,
            Token token
    ) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(ApiValues.TEXT, text);
        requestParams.add(ApiValues.LAT, String.valueOf(location.getLatitude()));
        requestParams.add(ApiValues.LON, String.valueOf(location.getLongitude()));
        RestClient.post(
                ApiValues.POST_CREATE_MESSAGE,
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
            this.mContext.sendMessagesView(message);
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

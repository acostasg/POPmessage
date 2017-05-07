package costas.albert.popmessage.task;

import android.app.ProgressDialog;
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
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;


public class PublishTask extends AsyncHttpResponseHandler {

    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private ProgressDialog dialog;
    private PublishActivity mContext;


    private PublishTask(PublishActivity mContext) {
        this.mContext = mContext;
    }


    @Override
    public void onStart() {
        this.dialog = new ProgressDialog(mContext);
        this.dialog.setCancelable(false);
        this.dialog.setMessage(this.mContext.getString(R.string.sending_message));
        this.dialog.show();
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
                new PublishTask(mContext),
                token
        );
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Message message = MessageMapper.build(responseBody);
            this.dialog.hide();
            this.dialog.cancel();
            this.mContext.sendMessagesView(message);
        } catch (Exception exception) {
            this.dialog.setMessage(this.mContext.getString(R.string.wrong_server_end)
                    + " [" + exception.getMessage() + ']');
            this.dialog.setCancelable(true);
        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext, this.dialog);
    }
}

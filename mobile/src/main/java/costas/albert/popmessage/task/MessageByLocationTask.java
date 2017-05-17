package costas.albert.popmessage.task;

import android.app.ProgressDialog;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import costas.albert.popmessage.MessagesActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.MessageMapper;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;

public class MessageByLocationTask extends AsyncHttpResponseHandler {

    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private ProgressDialog dialog;
    private MessagesActivity mContext;

    private MessageByLocationTask(MessagesActivity mContext) {
        this.mContext = mContext;
    }

    public static void execute(MessagesActivity messagesActivity, Location location) {
        Token token = new Session(messagesActivity).getToken();
        if (!token.isEmpty()) {
            RequestParams requestParams = new RequestParams();
            requestParams.add(ApiValues.LAT, String.valueOf(location.getLatitude()));
            requestParams.add(ApiValues.LON, String.valueOf(location.getLongitude()));
            RestClient.get(
                    ApiValues.GET_MESSAGE_TO_LOCATION,
                    requestParams,
                    new MessageByLocationTask(messagesActivity),
                    token
            );
        }
    }

    @Override
    public void onStart() {
        this.dialog = new ProgressDialog(mContext);
        this.dialog.setCancelable(false);
        this.dialog.setMessage(this.mContext.getString(R.string.search_messages));
        this.dialog.show();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            List<Message> messages = MessageMapper.buildList(responseBody);
            this.mContext.listMessagesService().initListMessages(
                    this.mContext,
                    messages,
                    R.id.messages
            );
            ImageView iconNotImages = (ImageView) this.mContext.findViewById(R.id.not_messages_global);
            if (messages.isEmpty()) {
                iconNotImages.setVisibility(View.VISIBLE);
            } else {
                iconNotImages.setVisibility(View.INVISIBLE);
            }
            this.dialog.hide();
            this.dialog.cancel();
        } catch (Exception exception) {
            this.dialog.setMessage(
                    this.mContext.getString(R.string.unexpected_short)
                            + exception.getMessage()
            );
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext, this.dialog);
    }
}
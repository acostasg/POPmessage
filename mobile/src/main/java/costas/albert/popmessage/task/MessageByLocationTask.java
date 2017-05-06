package costas.albert.popmessage.task;

import android.app.ProgressDialog;
import android.location.Location;

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
import cz.msebera.android.httpclient.Header;

public class MessageByLocationTask extends AsyncHttpResponseHandler {

    private ProgressDialog dialog;
    private MessagesActivity mContext;

    private MessageByLocationTask(MessagesActivity mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onStart() {
        this.dialog = new ProgressDialog(mContext);
        this.dialog.setCancelable(false);
        this.dialog.setMessage("Search messages by your location...");
        this.dialog.show();
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
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            List<Message> messages = MessageMapper.buildList(responseBody);
            this.mContext.listMessagesService.initListMessages(
                    this.mContext,
                    messages,
                    R.id.messages
            );
            this.dialog.hide();
        } catch (Exception exception) {
            this.dialog.setMessage(
                    "Unexpected Error occurred! Requested resource not found. "
                            + exception.getMessage()
            );
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (statusCode == 404) {
            this.dialog.setMessage("Requested resource not found");
        } else if (statusCode == 500) {
            this.dialog.setMessage("Something went wrong at server end");
        } else {
            this.dialog.setMessage("Unexpected Error occurred! [Most common Error: Device might not" +
                    " be connected to Internet or remote server is not up and running]");
        }
        this.dialog.setCancelable(true);
    }
}
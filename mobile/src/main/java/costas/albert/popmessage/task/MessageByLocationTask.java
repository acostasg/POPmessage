package costas.albert.popmessage.task;

import android.app.ProgressDialog;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.util.Log;
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

    private static MessageByLocationTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private ProgressDialog dialog;
    private MessagesActivity mContext;

    private MessageByLocationTask() {
    }

    private static MessageByLocationTask getInstance(MessagesActivity mContext) {
        if (instance == null)
            instance = new MessageByLocationTask();
        instance.setContext(mContext);
        return instance;
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
                    getInstance(messagesActivity),
                    token
            );
        }
    }

    private void setContext(MessagesActivity mContext) {
        synchronized (this) {
            this.mContext = mContext;
            setMessage(this.mContext.getString(R.string.search_messages));
        }
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
        } catch (Exception exception) {
            setMessage(this.mContext.getString(R.string.unexpected_short));
            Log.d(this.getClass().getSimpleName(), exception.getMessage());
        }
    }

    private void setMessage(String message) {
        try {
            Snackbar.make(
                    this.mContext.findViewById(android.R.id.content).getRootView(),
                    message,
                    Snackbar.LENGTH_LONG
            ).show();
        } catch (Exception exception) {
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext);
    }
}
package costas.albert.popmessage.task;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import costas.albert.popmessage.MyMessagesActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.MessageMapper;
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;

public class MessageByUserTask extends AsyncHttpResponseHandler {

    private static MessageByUserTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private ProgressDialog dialog;
    private MyMessagesActivity mContext;

    private MessageByUserTask() {
    }

    private static MessageByUserTask getInstance(MyMessagesActivity mContext) {
        if (instance == null)
            instance = new MessageByUserTask();
        instance.setContext(mContext);
        return instance;
    }

    public static void execute(MyMessagesActivity messagesActivity, Token token) {
        if (!token.isEmpty()) {
            RestClient.get(
                    ApiValues.GET_MESSAGE_TO_USER,
                    new RequestParams(),
                    getInstance(messagesActivity),
                    token
            );
        }
    }

    private void setContext(MyMessagesActivity mContext) {
        synchronized (this) {
            this.mContext = mContext;
            this.dialog = new ProgressDialog(mContext);
        }
    }

    @Override
    public void onStart() {
        this.dialog.setCancelable(false);
        this.dialog.setMessage(this.mContext.getString(R.string.search_your_messages));
        this.dialog.show();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            List<Message> messages = MessageMapper.buildList(responseBody);
            this.mContext.listMessagesService().initListMessages(
                    this.mContext,
                    messages,
                    R.id.messages_your
            );
            ImageView iconNotImages = (ImageView) this.mContext.findViewById(R.id.not_messages);
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

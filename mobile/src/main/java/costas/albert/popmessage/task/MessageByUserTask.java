package costas.albert.popmessage.task;

import android.app.ProgressDialog;

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

    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private ProgressDialog dialog;
    private MyMessagesActivity mContext;

    private MessageByUserTask(MyMessagesActivity mContext) {
        this.mContext = mContext;
    }

    public static void execute(MyMessagesActivity messagesActivity, Token token) {
        if (!token.isEmpty()) {
            RestClient.get(
                    ApiValues.GET_MESSAGE_TO_USER,
                    new RequestParams(),
                    new MessageByUserTask(messagesActivity),
                    token
            );
        }
    }

    @Override
    public void onStart() {
        this.dialog = new ProgressDialog(mContext);
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

package costas.albert.popmessage.task;

import android.app.ProgressDialog;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.MessagesActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.MessageMapper;
import cz.msebera.android.httpclient.Header;


public class VoteMessageTask extends AsyncHttpResponseHandler {

    private ProgressDialog dialog;
    private MessagesActivity mContext;


    private VoteMessageTask(MessagesActivity mContext) {
        this.mContext = mContext;
    }


    @Override
    public void onStart() {
        this.dialog = new ProgressDialog(mContext);
        this.dialog.setCancelable(false);
        this.dialog.setMessage("Sending message...");
        this.dialog.show();
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
                new VoteMessageTask(mContext),
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
                new VoteMessageTask(mContext),
                token
        );
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Message message = MessageMapper.build(responseBody);
            this.dialog.hide();
            this.mContext.sendMessagesView(message);
        } catch (Exception exception) {
            this.dialog.setMessage(this.mContext.getString(R.string.wrong_server_end)
                    + " [" + exception.getMessage() + ']');
            this.dialog.setCancelable(true);
        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (statusCode == 404) {
            this.dialog.setMessage("Requested resource not found");
        } else if (statusCode == 500) {
            this.dialog.setMessage("Something went wrong at server end");
        } else {
            this.dialog.setMessage("Unexpected Error occcured! [Most common Error: Device" +
                    " might not be connected to Internet or remote server is not up and running]");
        }
        this.dialog.setCancelable(true);
    }
}
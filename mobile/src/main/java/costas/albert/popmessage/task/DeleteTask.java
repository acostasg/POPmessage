package costas.albert.popmessage.task;

import android.app.ProgressDialog;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.MyMessagesActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.MessageMapper;
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;


public class DeleteTask extends AsyncHttpResponseHandler {

    private static DeleteTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private ProgressDialog dialog;
    private MyMessagesActivity mContext;

    private DeleteTask() {
    }

    private static DeleteTask getInstance(MyMessagesActivity mContext) {
        if (instance == null)
            instance = new DeleteTask();
        instance.setContext(mContext);
        return instance;
    }

    public static void execute(
            MyMessagesActivity mContext,
            Message message,
            Token token
    ) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(ApiValues.MESSAGE, message.Id());
        RestClient.post(
                ApiValues.POST_REMOVE_MESSAGE,
                requestParams,
                getInstance(mContext),
                token
        );
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
        this.dialog.setMessage(this.mContext.getString(R.string.delete_message));
        this.dialog.show();
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

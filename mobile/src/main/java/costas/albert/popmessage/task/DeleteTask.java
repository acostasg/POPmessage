package costas.albert.popmessage.task;

import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import costas.albert.popmessage.MyMessagesActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.api.RestClient;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.mapper.MessageMapper;
import costas.albert.popmessage.services.PrintMessageService;
import costas.albert.popmessage.wrapper.StatusResponseWrapper;
import cz.msebera.android.httpclient.Header;


public class DeleteTask extends AsyncHttpResponseHandler {

    private static DeleteTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private final PrintMessageService printMessageService = new PrintMessageService();
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
        }
    }

    @Override
    public void onStart() {
        this.printMessageService.printMessage(
                this.mContext.getString(R.string.delete_message),
                this.mContext,
                Toast.LENGTH_SHORT
        );
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Message message = MessageMapper.build(responseBody);
            this.mContext.sendMessagesView(message);
        } catch (Exception exception) {
            this.printMessageService.printBarMessage(
                    mContext.getString(R.string.wrong_server_end),
                    this.mContext
            );
            Log.d(this.getClass().getSimpleName(), exception.getMessage());
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext);
    }
}

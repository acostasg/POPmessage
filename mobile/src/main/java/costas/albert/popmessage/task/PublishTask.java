package costas.albert.popmessage.task;

import android.app.ProgressDialog;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.util.Log;

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

    private static PublishTask instance;
    private final StatusResponseWrapper statusResponseWrapper = new StatusResponseWrapper();
    private ProgressDialog dialog;
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
            this.dialog = new ProgressDialog(mContext);
        }
    }

    @Override
    public void onStart() {
        this.dialog.setCancelable(false);
        this.dialog.setMessage(this.mContext.getString(R.string.sending_message));
        this.dialog.show();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            Message message = MessageMapper.build(responseBody);
            this.mContext.sendMessagesView(message);
        } catch (Exception exception) {
            Snackbar.make(
                    this.mContext.findViewById(android.R.id.content).getRootView(),
                    this.mContext.getString(R.string.wrong_server_end),
                    Snackbar.LENGTH_LONG
            ).show();
            Log.d(this.getClass().getSimpleName(), exception.getMessage());
        }
        this.dialog.hide();
        this.dialog.cancel();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        statusResponseWrapper.onFailure(statusCode, this.mContext);
    }
}

package costas.albert.popmessage.services.handler;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import costas.albert.popmessage.services.ListActivityInterface;

public class ServiceMessageHandler extends Handler {

    private static final int DELAY_MILLIS = 1000;
    private final AppCompatActivity messagesActivity;
    private int id;
    private View footer;
    private boolean isLoading = false;

    public ServiceMessageHandler(
            AppCompatActivity messagesActivity,
            int id,
            View footer
    ) {
        this.messagesActivity = messagesActivity;
        this.id = id;
        this.footer = footer;
    }

    public boolean IsLoading() {
        return this.isLoading;
    }

    public void sendMessageToLoadingMessage() {
        this.isLoading = true;
        this.sendEmptyMessage(0);
        this.postDelayed(new Runnable() {
            public void run() {
                executeMessageTask();
            }
        }, DELAY_MILLIS);
    }

    private void executeMessageTask() {
        if (this.messagesActivity instanceof ListActivityInterface) {
            ListActivityInterface listActivitythis = (ListActivityInterface) this.messagesActivity;
            listActivitythis.executeMessageTask();
        }
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        super.handleMessage(msg);
        ListView listview = (ListView) messagesActivity.findViewById(id);
        switch (msg.what) {
            case 0:
                if (listview.getFooterViewsCount() < 1)
                    listview.addFooterView(footer);
                break;
            case 1:
                if (listview.getFooterViewsCount() > 0)
                    listview.removeFooterView(footer);
                isLoading = false;
                break;
            default:
                break;
        }
    }
}
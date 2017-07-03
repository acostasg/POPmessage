package costas.albert.popmessage.services;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.services.adaptable.AdaptableListView;
import costas.albert.popmessage.services.handler.ServiceMessageHandler;

public class ListMessagesService {

    private List<Message> messages = new ArrayList<Message>();
    private AppCompatActivity appCompatActivity;
    private boolean lastPage = false;
    private View footer;
    private ServiceMessageHandler serviceMessageHandler;
    private ArrayList<String> list = new ArrayList<>();
    private AdaptableListView adapter;
    private ListView listview;
    private int position = 0;

    public void initListMessages(
            AppCompatActivity appCompatActivity,
            final List<Message> messages,
            @IdRes int id
    ) {
        this.appCompatActivity = appCompatActivity;
        this.checkEmptyMessage(messages);
        this.initFooter();
        this.iniServiceMessageHandler(appCompatActivity, id);
        this.initMassagesListView(appCompatActivity, messages, id);
        this.serviceMessageHandler.sendEmptyMessage(1);
        this.disableShowProgress();
    }

    private void disableShowProgress() {
        if (this.appCompatActivity instanceof ListActivityInterface) {
            ((ListActivityInterface) this.appCompatActivity).showProgress(false);
        }
    }

    private void iniServiceMessageHandler(AppCompatActivity messagesActivity, @IdRes int id) {
        this.serviceMessageHandler = new ServiceMessageHandler(
                messagesActivity,
                id,
                footer
        );
    }

    private void initMassagesListView(
            AppCompatActivity messagesActivity,
            List<Message> messages,
            @IdRes int id
    ) {
        this.addListViewsListeners(id);
        this.checkLastPage(messages);
        this.messages.addAll(messages);
        this.setAdapterForListView(messagesActivity, messages);
        this.adapter.notifyDataSetChanged();
    }

    private void setAdapterForListView(AppCompatActivity messagesActivity, List<Message> messages) {
        if (null == this.adapter) {
            setAdapter(messagesActivity, messages);
        } else {
            addItemToAdapter(messages);
        }
    }

    private void addListViewsListeners(@IdRes int id) {
        if (null == this.listview) {
            this.listview = (ListView) this.appCompatActivity.findViewById(id);
            this.listview.setTextFilterEnabled(true);
            this.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    view.showContextMenu();
                }

            });
            this.listview.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (
                            isLastVisibleItem(view, totalItemCount)
                                    && isLastItemToPage()
                                    && isNotLoadingItems()
                                    && isNotLastPage()
                            ) {
                        serviceMessageHandler.sendMessageToLoadingMessage();
                    }
                }
            });
        }
    }

    private void checkLastPage(List<Message> messages) {
        if (messages.size() < ApiValues.LIMIT) {
            this.lastPage = true;
        }
    }

    private boolean isNotLastPage() {
        return !lastPage;
    }

    private boolean isNotLoadingItems() {
        return !this.serviceMessageHandler.IsLoading();
    }

    private boolean isLastItemToPage() {
        return listview.getCount() >= ApiValues.LIMIT;
    }

    private boolean isLastVisibleItem(AbsListView view, int totalItemCount) {
        return view.getLastVisiblePosition() == totalItemCount - 1;
    }

    private void addItemToAdapter(List<Message> messages) {
        for (Message message : messages) {
            if (checkValidMessage(message)) continue;
            this.adapter.addItem(
                    this.position,
                    message,
                    message.getText()
            );
            this.position++;
        }
    }

    private void setAdapter(AppCompatActivity messagesActivity, List<Message> messages) {
        for (Message message : messages) {
            if (checkValidMessage(message)) continue;
            list.add(this.position, message.getText());
            this.position++;
        }
        this.adapter = new AdaptableListView(
                messagesActivity,
                R.layout.list_item,
                this.messages,
                list,
                messagesActivity
        );
        listview.setAdapter(adapter);
    }

    private boolean checkValidMessage(Message message) {
        if (null == message) {
            return true;
        }
        return false;
    }

    private void initFooter() {
        if (null == this.footer) {
            LayoutInflater li = (LayoutInflater) this.appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.footer = li.inflate(R.layout.footer_messages, null);
        }
    }

    private void checkEmptyMessage(List<Message> messages) {
        ImageView iconNotImages = (ImageView) this.appCompatActivity.findViewById(R.id.not_messages_global);
        TextView textView = (TextView) this.appCompatActivity.findViewById(R.id.text_not_found_global);
        if (messages.isEmpty() && position() < 1) {
            iconNotImages.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            iconNotImages.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }
    }

    public int position() {
        return this.position;
    }

    public void resetPosition() {
        this.position = 0;
        this.lastPage = false;
        this.messages = new ArrayList<Message>();
        this.list = new ArrayList<String>();
        this.listview = null;
        this.adapter = null;
    }

    public Message getMessages(int position) {
        return messages.get(position);
    }
}
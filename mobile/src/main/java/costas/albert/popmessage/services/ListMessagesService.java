package costas.albert.popmessage.services;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.R;
import costas.albert.popmessage.api.ApiValues;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.session.Session;

public class ListMessagesService {

    private static final char LIKE = '+';
    private static final char DISLIKE = '-';
    private static final int DELAY_MILLIS = 1000;
    private List<Message> messages = new ArrayList<Message>();
    private AppCompatActivity messagesActivity;
    private Session session;
    private boolean isLoading = false;
    private View footer;
    private ServiceMessageHandler serviceMessageHandler = new ServiceMessageHandler();
    private ArrayList<String> list = new ArrayList<>();
    private StableArrayAdapter adapter;
    private ListView listview;
    private int position = 0;
    private int id;

    public void initListMessages(
            AppCompatActivity messagesActivity,
            final List<Message> messages,
            @IdRes int id
    ) {
        this.messagesActivity = messagesActivity;
        this.session = new Session(this.messagesActivity);
        this.id = id;
        this.checkEmptyMessage(messages);
        this.initFooter();
        this.initMassagesListView(messagesActivity, messages, id);
        serviceMessageHandler.sendEmptyMessage(1);
    }

    private void initMassagesListView(
            AppCompatActivity messagesActivity,
            List<Message> messages,
            @IdRes int id
    ) {
        if (null == this.listview) {
            this.listview = (ListView) this.messagesActivity.findViewById(id);
            listview.setTextFilterEnabled(true);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    view.showContextMenu();
                }

            });
            listview.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (view.getLastVisiblePosition() == totalItemCount - 1 && listview.getCount() >= ApiValues.LIMIT && !isLoading) {
                        isLoading = true;
                        serviceMessageHandler.sendEmptyMessage(0);
                        serviceMessageHandler.postDelayed(new Runnable() {
                            public void run() {
                                executeMessageTask();
                            }
                        }, DELAY_MILLIS);

                    }
                }
            });
        }

        this.messages.addAll(messages);

        if (null == this.adapter) {
            setAdapter(messagesActivity, messages);
        } else {
            addItemToAdapter(messages);
        }
        this.adapter.notifyDataSetChanged();
    }

    private void addItemToAdapter(List<Message> messages) {
        for (Message message : messages) {
            if (null == message) {
                continue;
            }
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
            if (null == message) {
                continue;
            }
            list.add(this.position, message.getText());
            this.position++;
        }
        this.adapter = new StableArrayAdapter(
                messagesActivity,
                R.layout.list_item,
                this.messages,
                list
        );
        listview.setAdapter(
                adapter
        );
    }

    private void initFooter() {
        if (null == this.footer) {
            LayoutInflater li = (LayoutInflater) this.messagesActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.footer = li.inflate(R.layout.footer_messages, null);
        }
    }

    private void checkEmptyMessage(List<Message> messages) {
        ImageView iconNotImages = (ImageView) this.messagesActivity.findViewById(R.id.not_messages_global);
        TextView textView = (TextView) this.messagesActivity.findViewById(R.id.text_not_found_global);
        if (messages.isEmpty() && position() < 1) {
            iconNotImages.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            iconNotImages.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }
    }

    private void executeMessageTask() {
        if (this.messagesActivity instanceof ListActivityInterface) {
            ListActivityInterface listActivitythis = (ListActivityInterface) this.messagesActivity;
            listActivitythis.executeMessageTask();
        }
    }

    public int position() {
        return this.position;
    }

    public void resetPosition() {
        this.position = 0;
        this.messages = new ArrayList<Message>();
        this.list = new ArrayList<String>();
        this.listview = null;
        this.adapter = null;
    }

    public Message getMessages(int position) {
        return messages.get(position);
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        List<Message> messages;

        StableArrayAdapter(
                Context context,
                int textViewResourceId,
                List<Message> messages,
                List<String> list
        ) {
            super(context, textViewResourceId, R.id.label, list);
            this.messages = messages;
        }

        private void addItem(int position, Message message, String string) {
            this.messages.add(position, message);
            this.add(string);
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return messages.get(position).getText();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) messagesActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.list_item, null);
            }
            Message message = messages.get(position);
            if (message != null) {
                ImageView icon = view.findViewById(R.id.icon_list_message);
                TextView user = view.findViewById(R.id.user);
                TextView text = view.findViewById(R.id.label);
                TextView likes = view.findViewById(R.id.messageVotesLike);
                TextView dislikes = view.findViewById(R.id.messageVotesDislike);
                LinearLayout userNameLayout = view.findViewById(R.id.userNameLayout);
                String userString = "";

                if (session.hasUser() && message.userId().equals(session.getUser().Id())) {
                    icon.setImageDrawable(messagesActivity.getDrawable(R.drawable.ic_done_all_black_24dp));
                    userNameLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    text.setTextColor(ContextCompat.getColor(this.getContext(), R.color.yourMessages));
                } else {
                    icon.setImageDrawable(messagesActivity.getDrawable(R.drawable.ic_mail_outline_black_24dp));
                    text.setTextColor(ContextCompat.getColor(this.getContext(), R.color.textItem));
                    userNameLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    userString = message.getUser().getName();
                }

                user.setText(userString);
                text.setText(message.getText());
                String like = LIKE + String.valueOf(message.getSummaryVotesLike());
                likes.setText(like);
                String dislike = DISLIKE + String.valueOf(message.getSummaryVotesDislike());
                dislikes.setText(dislike);

            }
            return view;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    private class ServiceMessageHandler extends Handler {
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
}
package costas.albert.popmessage.services;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.R;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.session.Session;

public class ListMessagesService {

    private static final char LIKE = '+';
    private static final char DISLIKE = '-';
    private List<Message> messages;
    private AppCompatActivity messagesActivity;
    private Session session;

    public ListMessagesService() {
    }

    public void initListMessages(
            AppCompatActivity messagesActivity,
            final List<Message> messages,
            @IdRes int id
    ) {
        this.messagesActivity = messagesActivity;
        this.session = new Session(this.messagesActivity);
        final ListView listview = (ListView) this.messagesActivity.findViewById(id);

        ArrayList<String> list = new ArrayList<>();
        int position = 0;
        this.messages = messages;
        for (Message message : this.messages) {
            if (null == message) {
                continue;
            }
            list.add(position, message.getText());
            position++;
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(
                messagesActivity,
                R.layout.list_item,
                messages,
                list
        );
        listview.setAdapter(adapter);
        listview.setTextFilterEnabled(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                view.showContextMenu();
            }

        });
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
                ImageView icon = (ImageView) view.findViewById(R.id.icon_list_message);
                TextView user = (TextView) view.findViewById(R.id.user);
                TextView text = (TextView) view.findViewById(R.id.label);
                TextView likes = (TextView) view.findViewById(R.id.messageVotesLike);
                TextView dislikes = (TextView) view.findViewById(R.id.messageVotesDislike);
                LinearLayout userNameLayout = (LinearLayout) view.findViewById(R.id.userNameLayout);


                if (session.hasUser() && message.userId().equals(session.getUser().Id())) {
                    icon.setImageDrawable(messagesActivity.getDrawable(R.drawable.ic_done_all_black_24dp));
                    userNameLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    user.setTextColor(ContextCompat.getColor(this.getContext(), R.color.yourMessages));
                    text.setTextColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
                    String userString = messagesActivity.getString(R.string.you);
                    user.setText(userString);
                } else {
                    icon.setImageDrawable(messagesActivity.getDrawable(R.drawable.ic_mail_outline_black_24dp));
                    text.setTextColor(ContextCompat.getColor(this.getContext(), R.color.textItem));
                    userNameLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    String userString = messagesActivity.getString(R.string.from) + ' ' + message.getUser().getName();
                    user.setText(userString);
                }

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
}
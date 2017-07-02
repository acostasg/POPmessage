package costas.albert.popmessage.services.adaptable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import costas.albert.popmessage.R;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.session.Session;

public class AdaptableListView extends ArrayAdapter<String> {

    private static final char LIKE = '+';
    private static final char DISLIKE = '-';

    private List<Message> messages;
    private AppCompatActivity messagesActivity;
    private Session session;

    public AdaptableListView(
            Context context,
            int textViewResourceId,
            List<Message> messages,
            List<String> list,
            AppCompatActivity messagesActivity
    ) {
        super(context, textViewResourceId, R.id.label, list);
        this.messages = messages;
        this.messagesActivity = messagesActivity;
        this.session = new Session(this.messagesActivity);
    }

    public void addItem(int position, Message message, String string) {
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
            final LayoutInflater vi = (LayoutInflater) messagesActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_item, null);
        }
        Message message = messages.get(position);
        if (message != null) {
            ImageView icon = view.findViewById(R.id.icon_list_message);
            final TextView user = view.findViewById(R.id.user);
            final TextView text = view.findViewById(R.id.label);
            final TextView likes = view.findViewById(R.id.messageVotesLike);
            final TextView dislikes = view.findViewById(R.id.messageVotesDislike);
            final LinearLayout userNameLayout = view.findViewById(R.id.userNameLayout);
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
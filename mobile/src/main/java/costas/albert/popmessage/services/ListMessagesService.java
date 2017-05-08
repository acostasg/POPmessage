package costas.albert.popmessage.services;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.R;
import costas.albert.popmessage.entity.Message;

public class ListMessagesService {

    private List<Message> messages;
    private AppCompatActivity messagesActivity;

    public ListMessagesService() {
    }

    public void initListMessages(
            AppCompatActivity messagesActivity,
            final List<Message> messages,
            @IdRes int id
    ) {
        this.messagesActivity = messagesActivity;
        final ListView listview = (ListView) this.messagesActivity.findViewById(id);

        ArrayList<String> list = new ArrayList<>();
        int position = 0;
        this.messages = messages;
        for (Message message : this.messages) {
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
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) messagesActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            Message message = messages.get(position);
            if (message != null) {
                TextView tt = (TextView) v.findViewById(R.id.user);
                TextView bt = (TextView) v.findViewById(R.id.label);
                if (tt != null) {
                    //TODO API return user
                    tt.setText(message.getUser().getName());
                }
                if (bt != null) {
                    bt.setText(message.getText());
                }
            }
            return v;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
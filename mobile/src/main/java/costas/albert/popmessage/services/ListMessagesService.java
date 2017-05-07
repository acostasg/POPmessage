package costas.albert.popmessage.services;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.MessagesActivity;
import costas.albert.popmessage.entity.Message;

public class ListMessagesService {

    private List<Message> messages;

    public ListMessagesService() {
    }

    public void initListMessages(
            AppCompatActivity messagesActivity,
            final List<Message> messages,
            @IdRes int id
    ) {
        final ListView listview = (ListView) messagesActivity.findViewById(id);

        ArrayList<String> list = new ArrayList<>();
        int position = 0;
        this.messages = messages;
        for (Message message : this.messages) {
            list.add(position, message.getText());
            position++;
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(
                messagesActivity,
                android.R.layout.simple_list_item_1,
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
            super(context, textViewResourceId, list);
            this.messages = messages;
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return messages.get(position).getText();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
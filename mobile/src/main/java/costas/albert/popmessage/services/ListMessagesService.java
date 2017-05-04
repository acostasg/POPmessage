package costas.albert.popmessage.services;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import costas.albert.popmessage.MessagesActivity;
import costas.albert.popmessage.entity.Message;

public class ListMessagesService {
    public ListMessagesService() {
    }

    public void initListMessages(MessagesActivity messagesActivity, List<Message> messages, int id) {
        final ListView listview = (ListView) messagesActivity.findViewById(id);

        final ArrayList<String> list = new ArrayList<String>();
        for (Message message : messages) {
            list.add(String.valueOf(message.getText()));
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(
                messagesActivity,
                android.R.layout.simple_list_item_1,
                list
        );
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }

        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        StableArrayAdapter(
                Context context,
                int textViewResourceId,
                List<String> objects
        ) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
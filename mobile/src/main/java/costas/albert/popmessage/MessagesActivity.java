package costas.albert.popmessage;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import costas.albert.popmessage.services.ListMessagesService;
import costas.albert.popmessage.task.UserLogOutTask;

public class MessagesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final ListMessagesService listMessagesService = new ListMessagesService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        FloatingActionButton newMessage = (FloatingActionButton) this.findViewById(R.id.new_message);
        newMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO
            }
        });
        listMessagesService.initListMessages(this, R.id.messages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.log_out:
                UserLogOutTask.execute(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}

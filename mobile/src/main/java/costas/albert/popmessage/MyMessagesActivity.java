package costas.albert.popmessage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.lang.reflect.Method;

import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.listener.FloatingButtonToPublishMessageListener;
import costas.albert.popmessage.services.ListActivityInterface;
import costas.albert.popmessage.services.ListMessagesService;
import costas.albert.popmessage.services.PrintMessageService;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.task.DeleteTask;
import costas.albert.popmessage.task.MessageByUserTask;
import costas.albert.popmessage.task.UserLogOutTask;
import costas.albert.popmessage.wrapper.ProgressViewWrapper;
import costas.albert.popmessage.wrapper.SubStringWrapper;

public class MyMessagesActivity extends AppCompatActivity implements ListActivityInterface {

    public static final String MESSAGE_ID = "messageId";
    public static final String MESSAGE_TEXT = "messageText";

    private final ListMessagesService listMessagesService = new ListMessagesService();
    private final FloatingButtonToPublishMessageListener floatingButtonToPublishMessageListener
            = new FloatingButtonToPublishMessageListener(this);
    private final PrintMessageService printMessageService = new PrintMessageService();
    private ProgressViewWrapper progressViewWrapper;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);
        registerForContextMenu(findViewById(R.id.messages_your));
        this.session = new Session(this);
        floatingButtonToPublishMessageListener
                .createFloatingButtonToPublishMessage(R.id.new_message_your);
        executeMessageByUserTask();
        this.progressViewWrapper = new ProgressViewWrapper(this);
        this.showProgress(true);
    }

    public void showProgress(final boolean show) {
        this.progressViewWrapper.showProgress(show);
    }

    private void executeMessageByUserTask() {
        MessageByUserTask.execute(
                this,
                this.session.getToken(),
                listMessagesService.position()
        );
    }

    public ListMessagesService listMessagesService() {
        return this.listMessagesService;
    }

    @Override
    public void onCreateContextMenu(
            ContextMenu menu,
            View v,
            ContextMenu.ContextMenuInfo menuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_message, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_you_messages, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = listMessagesService.getMessages(info.position);
        switch (item.getItemId()) {
            case R.id.delete_message:
                DeleteTask.execute(this, message, this.session.getToken());
                return true;
            case R.id.delete_modify:
                Intent intent = new Intent(this.getApplicationContext(), PublishActivity.class);
                intent.putExtra(MESSAGE_ID, message.Id());
                intent.putExtra(MESSAGE_TEXT, message.getText());
                this.startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(),
                            "onMenuOpened...unable to set icons for overflow menu",
                            e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        switch (id) {
            case R.id.log_out:
                UserLogOutTask.execute(this);
                return true;
            case R.id.view_current_messages:
                intent = new Intent(this, MessagesActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.map_messages:
                intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.refresh:
                listMessagesService.resetPosition();
                executeMessageByUserTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendMessagesView(Message message) {
        this.printMessageService.printMessage(
                this.getString(R.string.removed)
                        + SubStringWrapper.subString(message.getText())
                        + this.getString(R.string.ellipsis),
                this
        );
        executeMessageByUserTask();
    }

    @Override
    public void executeMessageTask() {
        this.executeMessageByUserTask();
    }
}

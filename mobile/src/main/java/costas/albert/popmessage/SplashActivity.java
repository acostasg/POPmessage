package costas.albert.popmessage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import costas.albert.popmessage.task.ValidationTask;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //validate token or login
        ValidationTask.execute(this);
    }

    public void sendMessagesView() {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void sendLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}

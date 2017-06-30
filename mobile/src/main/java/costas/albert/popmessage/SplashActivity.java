package costas.albert.popmessage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import costas.albert.popmessage.task.ValidationTask;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //validate token or login
        ValidationTask.execute(this);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendMessagesView() {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
        this.finish();
    }

}

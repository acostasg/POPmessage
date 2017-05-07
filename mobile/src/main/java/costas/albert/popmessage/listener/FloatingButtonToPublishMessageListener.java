package costas.albert.popmessage.listener;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import costas.albert.popmessage.PublishActivity;

public class FloatingButtonToPublishMessageListener {

    private AppCompatActivity appCompatActivity;

    public FloatingButtonToPublishMessageListener(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void createFloatingButtonToPublishMessage(@IdRes int id) {
        FloatingActionButton newMessage
                = (FloatingActionButton) appCompatActivity.findViewById(id);
        newMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(appCompatActivity.getApplicationContext(), PublishActivity.class);
                appCompatActivity.startActivity(intent);
            }
        });
    }
}
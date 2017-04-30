package costas.albert.popmessage.Listener;

import android.support.annotation.NonNull;
import android.view.View;

import costas.albert.popmessage.LoginActivity;

public class EmailSignButtonListener {
    private final LoginActivity loginActivity;

    public EmailSignButtonListener(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @NonNull
    public View.OnClickListener EmailSignIsButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginActivity.attemptLogin();
            }
        };
    }
}
package costas.albert.popmessage.listener;

import android.support.annotation.NonNull;
import android.view.View;

import costas.albert.popmessage.LoginActivity;

public class EmailSignButtonListener extends AbstractLoginListener {

    public EmailSignButtonListener(LoginActivity loginActivity) {
        super(loginActivity);
    }

    @NonNull
    public View.OnClickListener EmailSignIsButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        };
    }
}
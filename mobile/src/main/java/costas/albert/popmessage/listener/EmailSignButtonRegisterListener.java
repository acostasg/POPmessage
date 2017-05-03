package costas.albert.popmessage.listener;

import android.support.annotation.NonNull;
import android.view.View;

import costas.albert.popmessage.RegisterActivity;

public class EmailSignButtonRegisterListener extends AbstractRegisterListener {

    public EmailSignButtonRegisterListener(RegisterActivity registerActivity) {
        super(registerActivity);
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
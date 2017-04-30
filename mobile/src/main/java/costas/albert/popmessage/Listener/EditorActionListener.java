package costas.albert.popmessage.Listener;

import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.R;

public class EditorActionListener {
    private final LoginActivity loginActivity;

    public EditorActionListener(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @NonNull
    public TextView.OnEditorActionListener EditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    loginActivity.attemptLogin();
                    return true;
                }
                return false;
            }
        };
    }
}
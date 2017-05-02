package costas.albert.popmessage.listener;

import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.R;

public class EditorActionListener extends AbstractLoginListener {

    public EditorActionListener(LoginActivity loginActivity) {
        super(loginActivity);
    }

    @NonNull
    public TextView.OnEditorActionListener EditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        };
    }
}
package costas.albert.popmessage.listener;

import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import costas.albert.popmessage.R;
import costas.albert.popmessage.RegisterActivity;

public class EditorActionRegisterListener extends AbstractRegisterListener {

    public EditorActionRegisterListener(RegisterActivity registerActivity) {
        super(registerActivity);
    }

    @NonNull
    public TextView.OnEditorActionListener EditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.email_sign_in_button || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        };
    }
}
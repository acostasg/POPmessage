package costas.albert.popmessage.listener.watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import costas.albert.popmessage.R;

public class TextCountWatcher implements TextWatcher {
    private static final String MAXIM = "160";
    private TextView mTextView;

    public TextCountWatcher(TextView mTextView) {
        this.mTextView = mTextView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTextView.setText(
                mTextView.getContext().getString(R.string.character)
                        + String.valueOf(s.length())
                        + mTextView.getContext().getString(R.string.of)
                        + MAXIM
        );
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

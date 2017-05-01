package costas.albert.popmessage.listener;

import android.view.View;
import android.widget.CheckedTextView;

import costas.albert.popmessage.R;
import costas.albert.popmessage.RegisterActivity;

public class PolicyCheckedListener {
    private RegisterActivity registerActivity;

    public PolicyCheckedListener(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }

    public void policyCheckedListener() {
        final CheckedTextView ctv = (CheckedTextView) registerActivity.findViewById(R.id.hasPrivacyPolicy);
        ctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctv.isChecked())
                    ctv.setChecked(false);
                else
                    ctv.setChecked(true);

            }
        });
    }
}
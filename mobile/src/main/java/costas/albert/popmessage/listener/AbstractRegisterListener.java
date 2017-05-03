package costas.albert.popmessage.listener;

import android.text.TextUtils;
import android.view.View;

import costas.albert.popmessage.R;
import costas.albert.popmessage.RegisterActivity;
import costas.albert.popmessage.task.UserRegisterTask;

public class AbstractRegisterListener {

    protected final RegisterActivity registerActivity;

    AbstractRegisterListener(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }

    protected void attemptLogin() {
        // Reset errors.
        registerActivity.mEmailView.setError(null);
        registerActivity.mPasswordView.setError(null);
        registerActivity.mNameView.setError(null);
        registerActivity.mDateOfBirthView.setError(null);
        registerActivity.mHasPrivacyPolicyView.setError(null);

        // Store values at the time of the login attempt.
        String email = registerActivity.mEmailView.getText().toString();
        String password = registerActivity.mPasswordView.getText().toString();
        String name = registerActivity.mNameView.getText().toString();
        String dateOfBirth = registerActivity.mDateOfBirthView.getText().toString();
        boolean hasPolicy = registerActivity.mHasPrivacyPolicyView.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            registerActivity.mPasswordView.setError(registerActivity.getString(R.string.error_invalid_password));
            focusView = registerActivity.mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            registerActivity.mNameView.setError(registerActivity.getString(R.string.error_field_required));
            focusView = registerActivity.mNameView;
            cancel = true;
        } else if (TextUtils.isEmpty(dateOfBirth)) {
            registerActivity.mDateOfBirthView.setError(registerActivity.getString(R.string.error_field_required));
            focusView = registerActivity.mDateOfBirthView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            registerActivity.mEmailView.setError(registerActivity.getString(R.string.error_field_required));
            focusView = registerActivity.mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            registerActivity.mEmailView.setError(registerActivity.getString(R.string.error_invalid_email));
            focusView = registerActivity.mEmailView;
            cancel = true;
        } else if (!hasPolicy) {
            registerActivity.mHasPrivacyPolicyView.setError(registerActivity.getString(R.string.accept_terms));
            focusView = registerActivity.mHasPrivacyPolicyView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            registerActivity.showProgress(true);
            UserRegisterTask.execute(
                    this.registerActivity,
                    email,
                    password,
                    name,
                    dateOfBirth,
                    hasPolicy
            );
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}

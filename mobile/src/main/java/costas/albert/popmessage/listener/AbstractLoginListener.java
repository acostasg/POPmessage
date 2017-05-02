package costas.albert.popmessage.listener;

import android.text.TextUtils;
import android.view.View;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.R;
import costas.albert.popmessage.task.UserLogInTask;

public class AbstractLoginListener {

    protected final LoginActivity loginActivity;

    AbstractLoginListener(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    protected void attemptLogin() {
        // Reset errors.
        loginActivity.mEmailView.setError(null);
        loginActivity.mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = loginActivity.mEmailView.getText().toString();
        String password = loginActivity.mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            loginActivity.mPasswordView.setError(loginActivity.getString(R.string.error_invalid_password));
            focusView = loginActivity.mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            loginActivity.mEmailView.setError(loginActivity.getString(R.string.error_field_required));
            focusView = loginActivity.mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            loginActivity.mEmailView.setError(loginActivity.getString(R.string.error_invalid_email));
            focusView = loginActivity.mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginActivity.showProgress(true);
            UserLogInTask.execute(loginActivity, email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}

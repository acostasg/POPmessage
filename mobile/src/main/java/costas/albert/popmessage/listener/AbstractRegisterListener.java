package costas.albert.popmessage.listener;

import android.text.TextUtils;
import android.view.View;

import costas.albert.popmessage.EmailValidator.EmailValidator;
import costas.albert.popmessage.R;
import costas.albert.popmessage.RegisterActivity;
import costas.albert.popmessage.task.UserRegisterTask;
import costas.albert.popmessage.wrapper.CipherPasswordWrapper;

class AbstractRegisterListener {

    private static final int MINIM = 8;
    private static final int MAXIM = 16;
    private final RegisterActivity registerActivity;
    private EmailValidator emailValidator = new EmailValidator();

    AbstractRegisterListener(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }

    void attemptLogin() {
        // Reset errors.
        registerActivity.mEmailView.setError(null);
        registerActivity.mPasswordView.setError(null);
        registerActivity.mNameView.setError(null);
        registerActivity.mDateOfBirthView.setError(null);
        registerActivity.mHasPrivacyPolicyView.setError(null);

        // Store values at the time of the login attempt.
        String email = registerActivity.mEmailView.getText().toString();
        String password = CipherPasswordWrapper.Encoder(
                registerActivity.mPasswordView.getText().toString()
        );

        String name = registerActivity.mNameView.getText().toString();
        String dateOfBirth = registerActivity.mDateOfBirthView.getText().toString();
        boolean hasPolicy = registerActivity.mHasPrivacyPolicyView.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (
                !TextUtils.isEmpty(password)
                        && !isPasswordWithMinimCharacters(password)
                        && !isPasswordWithMaximCharacters(password)
                ) {
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
        return this.emailValidator.validate(email);
    }

    private boolean isPasswordWithMinimCharacters(String password) {
        return password.length() > MINIM;
    }

    private boolean isPasswordWithMaximCharacters(String password) {
        return password.length() > MAXIM;
    }

}

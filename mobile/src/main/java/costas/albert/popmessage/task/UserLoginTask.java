package costas.albert.popmessage.task;

import android.os.AsyncTask;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.R;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;

    private LoginActivity mContext;


    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    public UserLoginTask(LoginActivity mContext, String email, String password) {
        this.mContext = mContext;
        this.mEmail = email;
        this.mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return false;
        }

        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(mEmail)) {
                // Account exists, return true if the password matches.
                return pieces[1].equals(mPassword);
            }
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        this.mContext.mAuthTask = null;
        this.mContext.showProgress(false);

        if (success) {
            this.mContext.finish();
        } else {
            this.mContext.mPasswordView.setError(this.mContext.getString(R.string.error_incorrect_password));
            this.mContext.mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        this.mContext.mAuthTask = null;
        this.mContext.showProgress(false);
    }
}
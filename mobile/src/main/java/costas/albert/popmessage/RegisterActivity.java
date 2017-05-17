package costas.albert.popmessage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.listener.EditorActionRegisterListener;
import costas.albert.popmessage.listener.EmailSignButtonRegisterListener;
import costas.albert.popmessage.listener.PolicyCheckedListener;
import costas.albert.popmessage.services.AccessContactsRegisterService;
import costas.albert.popmessage.services.ProfileQueryService;

public class RegisterActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EQUAL = " = ?";
    public static final String DESC = " DESC";
    private static final int REQUEST_READ_CONTACTS = 1;
    private final PolicyCheckedListener policyCheckedListener
            = new PolicyCheckedListener(this);
    private final EmailSignButtonRegisterListener emailSignButtonRegisterListener
            = new EmailSignButtonRegisterListener(this);
    private final AccessContactsRegisterService accessContacts
            = new AccessContactsRegisterService(this);
    private final EditorActionRegisterListener editorActionListener
            = new EditorActionRegisterListener(this);

    public AutoCompleteTextView mEmailView;
    public EditText mPasswordView;
    public EditText mNameView;
    public EditText mDateOfBirthView;
    public CheckedTextView mHasPrivacyPolicyView;
    public View mProgressView;
    public View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        policyCheckedListener.policyCheckedListener();

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        accessContacts.populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(editorActionListener.EditorActionListener());

        mNameView = (EditText) findViewById(R.id.name);
        mDateOfBirthView = (EditText) findViewById(R.id.dateOfBirth);
        mHasPrivacyPolicyView = (CheckedTextView) findViewById(R.id.hasPrivacyPolicy);

        mLoginFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(
                emailSignButtonRegisterListener.EmailSignIsButtonListener()
        );

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY
                ), ProfileQueryService.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE +
                        EQUAL, new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.Data.IS_PRIMARY + DESC);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQueryService.ADDRESS));
            cursor.moveToNext();
        }

        this.accessContacts.addEmailsToAutoComplete(emails);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessContacts.populateAutoComplete();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void emailInUse() {
        View focusView;
        this.mEmailView.setError(this.getString(R.string.user_in_use));
        focusView = this.mEmailView;
        focusView.requestFocus();
    }

    public void sendLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void sendLogin(View view) {
        this.sendLogin();
    }
}

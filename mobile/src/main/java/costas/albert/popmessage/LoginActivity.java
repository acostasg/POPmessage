package costas.albert.popmessage;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.listener.EditorActionLoginListener;
import costas.albert.popmessage.listener.EmailSignButtonLoginListener;
import costas.albert.popmessage.services.AccessContactsLoginService;
import costas.albert.popmessage.services.ProfileQueryService;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.wrapper.ProgressViewWrapper;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 1;
    private final EmailSignButtonLoginListener emailSignButtonListener
            = new EmailSignButtonLoginListener(this);
    private final EditorActionLoginListener editorActionListener
            = new EditorActionLoginListener(this);
    private final AccessContactsLoginService accessContacts
            = new AccessContactsLoginService(this);
    public AutoCompleteTextView mEmailView;
    public EditText mPasswordView;
    public View mLoginFormView;
    private ProgressViewWrapper progressViewWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        // Exist register user in session
        Session session = new Session(this);
        if (session.hasUser()) {
            mEmailView.setText(session.getUser().getUserLogin());
        }

        accessContacts.populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(editorActionListener.EditorActionListener());

        // listener to login
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(emailSignButtonListener.EmailSignIsButtonListener());

        mLoginFormView = findViewById(R.id.login_form);
        this.progressViewWrapper = new ProgressViewWrapper(this);
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
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessContacts.populateAutoComplete();
            }
        }
    }

    public void sendRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void showProgress(final boolean show) {
        this.progressViewWrapper.showProgress(show);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY
                ), ProfileQueryService.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQueryService.ADDRESS));
            cursor.moveToNext();
        }

        this.accessContacts.addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public void sendMessagesView() {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
        this.finish();
    }

}


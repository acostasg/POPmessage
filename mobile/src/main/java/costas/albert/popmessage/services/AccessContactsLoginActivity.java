package costas.albert.popmessage.services;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.List;

import costas.albert.popmessage.LoginActivity;
import costas.albert.popmessage.R;

import static android.Manifest.permission.READ_CONTACTS;

public class AccessContactsLoginActivity {

    private static final int REQUEST_READ_CONTACTS = 0;
    private LoginActivity loginActivity;

    public AccessContactsLoginActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        this.loginActivity.getLoaderManager().initLoader(0, null, this.loginActivity);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (this.loginActivity.checkSelfPermission(READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (this.loginActivity.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(
                    this.loginActivity.mEmailView,
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE
            )
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            loginActivity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            this.loginActivity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    public void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this.loginActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        emailAddressCollection
                );

        this.loginActivity.mEmailView.setAdapter(adapter);
    }
}

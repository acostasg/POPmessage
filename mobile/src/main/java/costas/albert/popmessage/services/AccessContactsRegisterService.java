package costas.albert.popmessage.services;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.List;

import costas.albert.popmessage.R;
import costas.albert.popmessage.RegisterActivity;

import static android.Manifest.permission.READ_CONTACTS;

public class AccessContactsRegisterService {

    private static final int REQUEST_READ_CONTACTS = 0;
    private RegisterActivity registerActivity;

    public AccessContactsRegisterService(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }

    public void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        this.registerActivity.getLoaderManager().initLoader(0, null, this.registerActivity);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (this.registerActivity.checkSelfPermission(READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (this.registerActivity.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(
                    this.registerActivity.mEmailView,
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE
            )
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            registerActivity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            this.registerActivity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    public void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this.registerActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        emailAddressCollection
                );

        this.registerActivity.mEmailView.setAdapter(adapter);
    }
}

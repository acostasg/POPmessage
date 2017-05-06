package costas.albert.popmessage;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import costas.albert.popmessage.Wrapper.LocationManagerWrapper;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.task.PublishTask;
import costas.albert.popmessage.task.UserLogOutTask;

public class PublishActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText editText;
    private LocationManager mLocationManager;
    private LocationManagerWrapper locationManagerWrapper;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_message_login);
        editText = (EditText) findViewById(R.id.publish_message_text);
        this.requestToPermissionsToAccessGPS();
        this.session = new Session(this);
    }

    private void requestToPermissionsToAccessGPS() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LocationManagerWrapper.REQUEST_GPS
        );
    }

    private void initFloatingActionButton() {
        FloatingActionButton newMessage
                = (FloatingActionButton) this.findViewById(R.id.publish_action_button);

        newMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setError(null);

                if (editText.getText().length() < 15) {
                    editText.setError(
                            PublishActivity.this.getString(R.string.short_text)
                    );
                    return;
                }

                PublishTask.execute(
                        PublishActivity.this,
                        editText.getText().toString(),
                        locationManagerWrapper.getBestLocation(mLocationManager),
                        session.getToken()
                );
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults
    ) {
        if (requestCode == LocationManagerWrapper.REQUEST_GPS) {
            this.mLocationManager
                    = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManagerWrapper
                    = new LocationManagerWrapper(mLocationManager, this);
            if (this.locationManagerWrapper.hasAccessFineLocation()) {
                this.locationManagerWrapper.setMessageAccessLocationInvalid();
                return;
            }
            this.locationManagerWrapper = new LocationManagerWrapper(mLocationManager, this);
            initFloatingActionButton();
        }
    }

    public void sendMessagesView(Message message) {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
        Toast.makeText(
                this.getBaseContext(),
                "Publish success: " + message.getText().substring(0, 15) + "...",
                Toast.LENGTH_LONG
        ).show();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.log_out:
                UserLogOutTask.execute(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

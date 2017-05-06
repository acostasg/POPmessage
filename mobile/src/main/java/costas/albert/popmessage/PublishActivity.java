package costas.albert.popmessage;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import costas.albert.popmessage.Wrapper.LocationManagerWrapper;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.task.PublishTask;
import costas.albert.popmessage.task.UserLogOutTask;

public class PublishActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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
        FloatingActionButton newMessage = (FloatingActionButton) this.findViewById(R.id.publish_action_button);
        newMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LocationManagerWrapper.REQUEST_GPS) {
            this.mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManagerWrapper = new LocationManagerWrapper(mLocationManager, this);
            if (this.locationManagerWrapper.hasAccessFineLocation()) {
                this.locationManagerWrapper.setMessageAccessLocationInvalid();
                return;
            }
            this.locationManagerWrapper = new LocationManagerWrapper(mLocationManager, this);
            initFloatingActionButton();
        }
    }

    public void sendMessagesView() {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
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

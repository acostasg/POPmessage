package costas.albert.popmessage;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
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

import costas.albert.popmessage.Wrapper.LocationManagerWrapper;
import costas.albert.popmessage.services.ListMessagesService;
import costas.albert.popmessage.task.MessageByLocationTask;
import costas.albert.popmessage.task.UserLogOutTask;

public class MessagesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, LocationListener {

    public final ListMessagesService listMessagesService = new ListMessagesService();
    private LocationManager mLocationManager;
    private LocationManagerWrapper locationManagerWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        requestToPermissionsToAccessGPS();
        createFloatingButtonToPublishMessage();
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

    private void createFloatingButtonToPublishMessage() {
        FloatingActionButton newMessage
                = (FloatingActionButton) this.findViewById(R.id.new_message);
        newMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PublishActivity.class);
                startActivity(intent);
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
            Criteria criteria = new Criteria();

            String provider = mLocationManager.getBestProvider(criteria, false);

            this.mLocationManager.requestLocationUpdates(
                    provider,
                    LocationManagerWrapper.MIN_TIME,
                    LocationManagerWrapper.MIN_DISTANCE,
                    this
            );
            this.getLastKnownLocationAndRefreshMessages();
        }
    }

    public void getLastKnownLocationAndRefreshMessages() {
        if (this.locationManagerWrapper.hasAccessFineLocation()) {
            this.locationManagerWrapper.setMessageAccessLocationInvalid();
            return;
        }
        Location bestLocation = this.locationManagerWrapper.getBestLocation(mLocationManager);
        if (null != bestLocation) {
            MessageByLocationTask.execute(
                    this,
                    bestLocation
            );
        }
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

    @Override
    public void onLocationChanged(Location location) {
        this.getLastKnownLocationAndRefreshMessages();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        this.getLastKnownLocationAndRefreshMessages();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

package costas.albert.popmessage;

import android.Manifest;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import costas.albert.popmessage.services.ListMessagesService;
import costas.albert.popmessage.task.MessageByLocationTask;
import costas.albert.popmessage.task.UserLogOutTask;

public class MessagesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, LocationListener {

    private static final int REQUEST_GPS = 1;
    private static final int MIN_DISTANCE = 5;
    private static final int MIN_TIME = 20000;

    public final ListMessagesService listMessagesService = new ListMessagesService();
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                REQUEST_GPS
        );

        FloatingActionButton newMessage = (FloatingActionButton) this.findViewById(R.id.new_message);
        newMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_GPS) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                setMessageAccesLocationInvalid();
                return;
            }
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            String provider = mLocationManager.getBestProvider(criteria, false);

            mLocationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this);
            getLastKnownLocationAndRefreshMessages();
        }
    }

    private void getLastKnownLocationAndRefreshMessages() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setMessageAccesLocationInvalid();
            return;
        }
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(provider);
            if (lastKnownLocation == null) {
                continue;
            }
            if (bestLocation == null || lastKnownLocation.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = lastKnownLocation;
            }
        }
        if (null != bestLocation) {
            MessageByLocationTask.execute(
                    this,
                    bestLocation
            );
        }
    }

    private void setMessageAccesLocationInvalid() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("It is necessary to have permission to location service for" +
                " use this application.");
        dialog.show();
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
        getLastKnownLocationAndRefreshMessages();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        getLastKnownLocationAndRefreshMessages();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

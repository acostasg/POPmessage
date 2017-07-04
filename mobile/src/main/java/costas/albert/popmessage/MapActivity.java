package costas.albert.popmessage;

import android.Manifest;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Method;
import java.util.List;

import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.task.MapMessagesByLocationTask;
import costas.albert.popmessage.task.UserLogOutTask;
import costas.albert.popmessage.wrapper.LocationManagerWrapper;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationManagerWrapper locationManagerWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_messages);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.requestToPermissionsToAccessGPS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        switch (id) {
            case R.id.log_out:
                UserLogOutTask.execute(this);
                return true;
            case R.id.my_messages:
                intent = new Intent(this, MyMessagesActivity.class);
                startActivity(intent);
                return true;
            case R.id.view_current_messages:
                intent = new Intent(this, MessagesActivity.class);
                startActivity(intent);
                return true;
            case R.id.refresh:
                this.getLastKnownLocationAndRefreshMessages();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    (LocationListener) this
            );
            this.getLastKnownLocationAndRefreshMessages();
        }
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(),
                            "onMenuOpened...unable to set icons for overflow menu",
                            e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
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

    public void getLastKnownLocationAndRefreshMessages() {
        if (this.locationManagerWrapper.hasAccessFineLocation()) {
            this.locationManagerWrapper.setMessageAccessLocationInvalid();
            return;
        }
        Location bestLocation = this.locationManagerWrapper.getBestLocation(mLocationManager);
        if (null != bestLocation) {
            this.mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(
                                    bestLocation.getLatitude(),
                                    bestLocation.getLongitude()),
                            13.5f
                    )
            );
            MapMessagesByLocationTask.execute(
                    this,
                    bestLocation,
                    20
            );
        }
    }

    public void initMapMessages(List<Message> messages) {
        for (Message message : messages) {
            this.mMap.addMarker(new MarkerOptions().position(
                    new LatLng(
                            Double.valueOf(message.getLocation().getLon()), //TODO change to APi return
                            Double.valueOf(message.getLocation().getLat())

                    )
            )
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_location))
                    .title(message.getText()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.mMap.getUiSettings().setScrollGesturesEnabled(false);
        this.mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        this.mMap.getUiSettings().setMapToolbarEnabled(false);
        this.mMap.getUiSettings().setZoomGesturesEnabled(false);
    }
}

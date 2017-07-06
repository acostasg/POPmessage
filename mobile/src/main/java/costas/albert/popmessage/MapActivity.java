package costas.albert.popmessage;

import android.Manifest;
import android.content.Intent;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.listener.FloatingButtonToPublishMessageListener;
import costas.albert.popmessage.services.google.maps.CustomClusterRenderer;
import costas.albert.popmessage.services.google.maps.GroupMessages;
import costas.albert.popmessage.task.MapMessagesByLocationTask;
import costas.albert.popmessage.task.UserLogOutTask;
import costas.albert.popmessage.wrapper.LocationManagerWrapper;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    public static final float ZOOM_MAX = 17f;
    public static final float ZOOM_MIN = 13f;
    public static final double RADIUS = 2500;

    private final FloatingButtonToPublishMessageListener floatingButtonToPublishMessageListener
            = new FloatingButtonToPublishMessageListener(this);
    private ClusterManager<GroupMessages> clusterManager;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationManagerWrapper locationManagerWrapper;
    private FloatingActionButton moreMessages;
    private int LAST = 0;
    private boolean isLoading = false;
    private ArrayList<GroupMessages> clusterList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.map_messages);
        this.initGoogleMaps();
        this.floatingButtonToPublishMessageListener.createFloatingButtonToPublishMessage(R.id.new_message);
        this.addListenerToButtonMoreMessages();
        this.requestToPermissionsToAccessGPS();
    }

    private void initGoogleMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void addListenerToButtonMoreMessages() {
        moreMessages
                = (FloatingActionButton) this.findViewById(R.id.more_messages);
        moreMessages.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isLoading) {
                    isLoading = true;
                    getLastKnownLocationAndRefreshMessages(LAST);
                }
            }
        });
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
                    //TODO
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.getLastKnownLocationAndRefreshMessages();
        clusterManager.cluster();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void getLastKnownLocationAndRefreshMessages() {
        this.clusterList = new ArrayList<>();
        this.getLastKnownLocationAndRefreshMessages(0);
    }


    public void getLastKnownLocationAndRefreshMessages(int LAST) {
        if (this.locationManagerWrapper.hasAccessFineLocation()) {
            this.locationManagerWrapper.setMessageAccessLocationInvalid();
            return;
        }
        Location bestLocation = this.locationManagerWrapper.getBestLocation(mLocationManager);
        if (null != bestLocation) {
            this.mMap.setLatLngBoundsForCameraTarget(this.toBounds(
                    new LatLng(
                            bestLocation.getLatitude(),
                            bestLocation.getLongitude()
                    ),
                    RADIUS
                    )
            );
            MapMessagesByLocationTask.execute(
                    this,
                    bestLocation,
                    LAST
            );
        }
    }

    public LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    public void initMapMessages(List<Message> messages) {
        LAST = LAST + messages.size();
        this.clusterManager = new ClusterManager<GroupMessages>(this, mMap);
        final CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, this.clusterManager);

        this.clusterManager.setRenderer(renderer);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        initMarkers(messages);
        isLoading = false;
    }

    private void initMarkers(List<Message> messages) {
        this.addMessagesToClusters(messages);
        clusterManager.clearItems();
        mMap.clear();
        clusterManager.addItems(clusterList);
        clusterManager.cluster();
    }

    private void addMessagesToClusters(List<Message> messagesList) {
        for (Message message : messagesList) {
            GroupMessages cluster = new GroupMessages(message);
            clusterList.add(cluster);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.mMap.getUiSettings().setMapToolbarEnabled(false);
        this.mMap.setMinZoomPreference(ZOOM_MIN);
        this.mMap.setMaxZoomPreference(ZOOM_MAX);
        this.mMap.setBuildingsEnabled(false);
    }

}

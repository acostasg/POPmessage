package costas.albert.popmessage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.listener.FloatingButtonToPublishMessageListener;
import costas.albert.popmessage.services.google.maps.GroupMessages;
import costas.albert.popmessage.task.MapMessagesByLocationTask;
import costas.albert.popmessage.task.UserLogOutTask;
import costas.albert.popmessage.wrapper.LocationManagerWrapper;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    public static final int LAST = 0;
    public static final float ZOOM = 13.5f;
    private final FloatingButtonToPublishMessageListener floatingButtonToPublishMessageListener
            = new FloatingButtonToPublishMessageListener(this);
    private ClusterManager<GroupMessages> clusterManager;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationManagerWrapper locationManagerWrapper;
    private int OFFSET = 268435456;
    private double RADIUS = 85445659.4471;
    private double pi = 3.1444;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_messages);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.floatingButtonToPublishMessageListener.createFloatingButtonToPublishMessage(R.id.new_message);
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
                            ZOOM
                    )
            );
            MapMessagesByLocationTask.execute(
                    this,
                    bestLocation,
                    LAST
            );
        }
    }

    public void initMapMessages(List<Message> messages) {
        this.clusterManager = new ClusterManager<GroupMessages>(this, mMap);
        final CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, this.clusterManager);

        this.clusterManager.setRenderer(renderer);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        initMarkers(messages);
    }

    private void initMarkers(List<Message> messages) {

        List<GroupMessages> clusters = clusters(messages);
        clusterManager.addItems(clusters);
        clusterManager.cluster();
    }

    private List<GroupMessages> clusters(List<Message> messagesList) {

        ArrayList<GroupMessages> clusterList = new ArrayList<>();
        for (Message message : messagesList) {
            GroupMessages cluster = new GroupMessages(message);
            clusterList.add(cluster);
        }

        return clusterList;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        //this.mMap.getUiSettings().setScrollGesturesEnabled(false);
        this.mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    public class CustomClusterRenderer extends DefaultClusterRenderer<GroupMessages> {

        private final Context mContext;
        private final IconGenerator mClusterIconGenerator;

        public CustomClusterRenderer(Context context, GoogleMap map,
                                     ClusterManager<GroupMessages> clusterManager) {
            super(context, map, clusterManager);
            mClusterIconGenerator = new IconGenerator(context.getApplicationContext());
            mContext = context;
        }

        @Override
        protected void onBeforeClusterItemRendered(GroupMessages item,
                                                   MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_location))
                    .snippet(item.message().userName())
                    .title(item.message().getText());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<GroupMessages> cluster, MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);

            mClusterIconGenerator.setBackground(
                    ContextCompat.getDrawable(mContext, R.drawable.background_circle));
            mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);
            final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

    }
}

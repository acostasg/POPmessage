package costas.albert.popmessage.wrapper;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import costas.albert.popmessage.R;

public class LocationManagerWrapper {

    private LocationManager mLocationManager;
    private AppCompatActivity appCompatActivity;

    public static final int REQUEST_GPS = 1;
    public static final int MIN_DISTANCE = 5;
    public static final int MIN_TIME = 20000;

    public LocationManagerWrapper(LocationManager mLocationManager, AppCompatActivity activity) {
        this.mLocationManager = mLocationManager;
        this.appCompatActivity = activity;
    }

    public boolean hasAccessFineLocation() {
        return ActivityCompat.checkSelfPermission(
                this.appCompatActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.appCompatActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED;
    }

    @Nullable
    public Location getBestLocation(LocationManager mLocationManager) {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location lastKnownLocation = this.mLocationManager.getLastKnownLocation(provider);
            if (lastKnownLocation == null) {
                continue;
            }
            if (bestLocation == null || lastKnownLocation.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = lastKnownLocation;
            }
        }
        return bestLocation;
    }

    public void setMessageAccessLocationInvalid() {
        ProgressDialog dialog = new ProgressDialog(this.appCompatActivity);
        dialog.setMessage(this.appCompatActivity.getString(R.string.not_permission));
        dialog.show();
    }
}
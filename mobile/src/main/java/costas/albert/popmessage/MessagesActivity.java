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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.lang.reflect.Method;

import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.listener.FloatingButtonToPublishMessageListener;
import costas.albert.popmessage.services.ListMessagesService;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.task.MessageByLocationTask;
import costas.albert.popmessage.task.UserLogOutTask;
import costas.albert.popmessage.task.VoteMessageTask;
import costas.albert.popmessage.wrapper.LocationManagerWrapper;
import costas.albert.popmessage.wrapper.SubStringWrapper;

public class MessagesActivity extends AppCompatActivity
        implements LocationListener {

    private final ListMessagesService listMessagesService = new ListMessagesService();
    private final FloatingButtonToPublishMessageListener floatingButtonToPublishMessageListener
            = new FloatingButtonToPublishMessageListener(this);
    private LocationManager mLocationManager;
    private LocationManagerWrapper locationManagerWrapper;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        requestToPermissionsToAccessGPS();
        registerForContextMenu(findViewById(R.id.messages));
        this.session = new Session(this);
        floatingButtonToPublishMessageListener.createFloatingButtonToPublishMessage(R.id.new_message);
    }

    @Override
    public void onCreateContextMenu(
            ContextMenu menu,
            View v,
            ContextMenu.ContextMenuInfo menuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vote_message, menu);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = listMessagesService.getMessages(info.position);
        switch (item.getItemId()) {
            case R.id.voteLike:
                VoteMessageTask.executeLike(
                        this,
                        message,
                        this.session.getToken()
                );
                return true;
            case R.id.voteDislike:
                VoteMessageTask.executeDislike(
                        this,
                        message,
                        this.session.getToken()
                );
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendMessagesView(Message message) {
        this.getLastKnownLocationAndRefreshMessages();

        Toast.makeText(
                this.getBaseContext(),
                this.getString(R.string.vote)
                        + SubStringWrapper.subString(message.getText())
                        + this.getString(R.string.ellipsis),
                Toast.LENGTH_LONG
        ).show();
    }

    public ListMessagesService listMessagesService() {
        return this.listMessagesService;
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

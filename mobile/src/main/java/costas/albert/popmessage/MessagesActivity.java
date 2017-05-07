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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import costas.albert.popmessage.Wrapper.LocationManagerWrapper;
import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.services.ListMessagesService;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.task.MessageByLocationTask;
import costas.albert.popmessage.task.UserLogOutTask;
import costas.albert.popmessage.task.VoteMessageTask;

public class MessagesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, LocationListener {

    public final ListMessagesService listMessagesService = new ListMessagesService();
    private LocationManager mLocationManager;
    private LocationManagerWrapper locationManagerWrapper;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        requestToPermissionsToAccessGPS();
        createFloatingButtonToPublishMessage();
        registerForContextMenu(findViewById(R.id.messages));
        this.session = new Session(this);
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
        int id = item.getItemId();
        switch (id) {
            case R.id.log_out:
                UserLogOutTask.execute(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendMessagesView(Message message) {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
        Toast.makeText(
                this.getBaseContext(),
                "Vote: " + message.getText().substring(0, 15) + "...",
                Toast.LENGTH_LONG
        ).show();
        this.finish();
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

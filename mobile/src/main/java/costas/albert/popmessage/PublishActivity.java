package costas.albert.popmessage;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

import costas.albert.popmessage.entity.Message;
import costas.albert.popmessage.listener.watcher.TextCountWatcher;
import costas.albert.popmessage.session.Session;
import costas.albert.popmessage.task.PublishTask;
import costas.albert.popmessage.task.UserLogOutTask;
import costas.albert.popmessage.wrapper.EncodeMessageWrapper;
import costas.albert.popmessage.wrapper.LocationManagerWrapper;
import costas.albert.popmessage.wrapper.SubStringWrapper;

public class PublishActivity extends AppCompatActivity {

    private EditText editText;
    private LocationManager mLocationManager;
    private LocationManagerWrapper locationManagerWrapper;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_message);
        editText = (EditText) findViewById(R.id.publish_message_text);
        editText.addTextChangedListener(new TextCountWatcher((TextView) findViewById(R.id.maxim_char)));
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

                EncodeMessageWrapper encodeMessageWrapper
                        = new EncodeMessageWrapper(editText.getText().toString());

                if (encodeMessageWrapper.isShort()) {
                    editText.setText(encodeMessageWrapper.clearCode());
                    editText.setError(
                            PublishActivity.this.getString(R.string.short_text)
                    );
                    return;
                }

                if (encodeMessageWrapper.isSmall()) {
                    editText.setText(encodeMessageWrapper.clearCode());
                    editText.setError(
                            PublishActivity.this.getString(R.string.big_text)
                    );
                    return;
                }

                PublishTask.execute(
                        PublishActivity.this,
                        encodeMessageWrapper.encode(),
                        locationManagerWrapper.getBestLocation(mLocationManager),
                        session.getToken()
                );
            }
        });
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
                this.getString(R.string.publish_success)
                        + SubStringWrapper.subString(message.getText())
                        + this.getString(R.string.ellipsis),
                Toast.LENGTH_LONG
        ).show();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_publish_messages, menu);
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
            case R.id.view_current_messages:
                intent = new Intent(this, MessagesActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.my_messages:
                intent = new Intent(this, MyMessagesActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

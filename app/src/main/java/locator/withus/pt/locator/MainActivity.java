package locator.withus.pt.locator;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.LocationServices;

import java.net.MalformedURLException;
import java.net.URL;

import locator.withus.pt.domain.GenderPositions;
import locator.withus.pt.tasks.SendLocation;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener{


    private GoogleApiClient mGoogleApiClient;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] genders = {getString(R.string.male), getString(R.string.female)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        buildGoogleApiClient();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitGender(View view) {
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.getSelectedItemId();

        double latitude = 0;
        double longitude = 0;

        String gender = (spinner.getSelectedItemId() == GenderPositions.MALE.getPosition()) ? GenderPositions.MALE.getGenderDescription() : GenderPositions.FEMALE.getGenderDescription();

        if (isConnected){
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }
        }else{
            Toast.makeText(this, R.string.cannot_locate,Toast.LENGTH_SHORT);
        }

        new SendLocation().execute(gender, String.valueOf(latitude), String.valueOf(longitude));
    }

    @Override
    public void onConnected(Bundle bundle) {
        isConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        isConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        isConnected = false;
    }
}

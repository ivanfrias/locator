package locator.withus.pt.locator;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import locator.withus.pt.domain.GenderPositions;
import locator.withus.pt.tasks.SendLocation;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener{


    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
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

    private void submitGender(GenderPositions gender) {
        ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);

        double latitude;
        double longitude;

        if (lastLocation!=null){
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
            new SendLocation(this).execute(gender.getGenderDescription(), String.valueOf(latitude), String.valueOf(longitude));
        }else{
            Toast.makeText(this, R.string.cannot_locate, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void setFemale(View view) {
        submitGender(GenderPositions.FEMALE);
    }

    public void setMale(View view) {
        submitGender(GenderPositions.MALE);
    }

    public void setNotSpecified(View view) {
        submitGender(GenderPositions.NOT_SPECIFIED);
    }
}

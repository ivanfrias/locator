package locator.withus.pt.locator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.text.NumberFormat;
import java.util.Locale;

import locator.withus.pt.domain.GenderPositions;
import locator.withus.pt.listeners.LocationChangedListener;
import locator.withus.pt.tasks.SendLocation;


public class MainActivity extends ActionBarActivity implements LocationChangedListener{

    private LocationRequester requester;
    private Location lastPosition;
    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requester = LocationRequester.getInstance(this, this);
        setContentView(R.layout.activity_main);
        connectToGoogleServices();
        prefs = getSharedPreferences("locator.withus.pt", MODE_PRIVATE);
    }

    private void connectToGoogleServices() {
        int errorCode = requester.servicesAvailable();
        if(errorCode != ConnectionResult.SUCCESS){
            GooglePlayServicesUtil.showErrorNotification(errorCode, this);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        requester.disconnect();
    }

    @Override
    protected void onResume(){
        super.onResume();
        requester.connect();
        if(!prefs.getBoolean("firstrun", true)){
            Intent displayMap = new Intent(this, MapsActivity.class);
            displayMap.putExtra(SendLocation.GENDER, prefs.getString(SendLocation.GENDER, ""));
            this.startActivity(displayMap);
        }else{
            prefs.edit().putBoolean("firstrun", false).commit();
        }
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

        prefs.edit().putString("GENDER", gender.getGenderDescription()).commit();

        if (lastPosition!=null){
            new SendLocation(this).execute(gender.getGenderDescription(),  Double.toString(lastPosition.getLatitude())  , Double.toString(lastPosition.getLongitude()));
        }else{
            Toast.makeText(this, R.string.cannot_locate, Toast.LENGTH_LONG);
        }
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

    @Override
    public void locationChanged(Location newLocation) {
        this.lastPosition = newLocation;
    }
}

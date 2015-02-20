package locator.withus.pt.locator;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import locator.withus.pt.domain.GenderPositions;
import locator.withus.pt.listeners.LocationChangedListener;
import locator.withus.pt.tasks.SendLocation;

public class MapsActivity extends FragmentActivity implements LocationChangedListener{

    private GoogleMap mMap;
    private LocationRequester requester;
    private Location lastPosition;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_with_filter);

        requester = LocationRequester.getInstance(this, this);

        int errorCode = requester.servicesAvailable();
        if(!(errorCode == ConnectionResult.SUCCESS)){
            finish();
        }else {
            setUpMapIfNeeded();
            gender = getIntent().getStringExtra(SendLocation.GENDER);
            String latitude = getIntent().getStringExtra(SendLocation.LATITUDE);
            String longitude = getIntent().getStringExtra(SendLocation.LONGITUDE);
            addMarkersToMap(latitude, longitude, gender);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requester.connect();
        requester.requestLastPosition();
    }

    @Override
    protected void onPause() {
        super.onPause();
        requester.disconnect();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
    }

    private void addMarkersToMap(String userLatitude, String userLongitude, String gender) {
        mMap.clear();

        LatLng coordinate = new LatLng(Double.valueOf(userLatitude), Double.valueOf(userLongitude));
        BitmapDescriptor icon;

        if (GenderPositions.MALE.getGenderDescription().equals(gender)) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_male);
        } else if (GenderPositions.FEMALE.getGenderDescription().equals(gender)) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_female);
        } else {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_home);
        }
        MarkerOptions options = new MarkerOptions().position(coordinate).icon(icon).title("Latitude " + userLatitude + ", Longitude " + userLongitude);
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18f));
    }

    @Override
    public void locationChanged(Location newLocation) {
        this.lastPosition = newLocation;
        addMarkersToMap(String.valueOf(lastPosition.getLatitude()), String.valueOf(lastPosition.getLongitude()), gender);
    }
}

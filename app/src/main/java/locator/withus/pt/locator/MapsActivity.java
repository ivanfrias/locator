package locator.withus.pt.locator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import locator.withus.pt.domain.GenderPositions;
import locator.withus.pt.tasks.SendLocation;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(findViewById(R.id.map));
        Intent intent = getIntent();
        String userLatitude = intent.getStringExtra(SendLocation.LATITUDE);
        String userLongitude = intent.getStringExtra(SendLocation.LONGITUDE);
        String userGender = intent.getStringExtra(SendLocation.GENDER);
        setUpMapIfNeeded();
        addMarkersToMap(userLatitude, userLongitude, userGender);
    }




    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        // TODO:
        // TODO: 1.query last position
        // TODO: 2. send to server
        // TODO: 3. query people
        // TODO: 4. display on map
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
    }

    private void addMarkersToMap(String userLatitude, String userLongitude, String gender) {
        MarkerOptions options = new MarkerOptions().position(new LatLng(Double.valueOf(userLatitude), Double.valueOf(userLongitude)));
        BitmapDescriptor icon = null;

        if (GenderPositions.MALE.getGenderDescription().equals(gender)) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_male);
        } else if (GenderPositions.FEMALE.getGenderDescription().equals(gender)){
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_female);
        } else{
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_home);
        }

        options.icon(icon);
        options.title("Latitude " + userLatitude + ", Longitude " + userLongitude);
        LatLng center = new LatLng(Double.valueOf(userLatitude), Double.valueOf(userLongitude));
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 21f));
    }
}

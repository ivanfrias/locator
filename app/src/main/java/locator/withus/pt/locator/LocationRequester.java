package locator.withus.pt.locator;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import locator.withus.pt.listeners.LocationChangedListener;

/**
 * Created by ivan.frias on 16-02-2015.
 */
public class LocationRequester implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;

    private List<LocationChangedListener> listeners = new ArrayList<LocationChangedListener>();

    private static LocationRequester instance;
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private Location lastLocation;
    private Context currentContext;

    public void register(LocationChangedListener listener){
        listeners.add(listener);
    }

    public void unregister(LocationChangedListener listener){
        listeners.remove(listener);
    }

    private LocationRequester(Context context, LocationChangedListener listener){
        register(listener);
        this.currentContext = context;
        if(servicesAvailable() == ConnectionResult.SUCCESS){
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(POLLING_FREQ);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);
            buildGoogleApiClient(context);
        }
    }

    protected synchronized void buildGoogleApiClient(Context context) {
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void connect(){
        if(!(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())){
            mGoogleApiClient.connect();
        }
    }

    public void disconnect(){
        if(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
            mGoogleApiClient.disconnect();
        }
    }

    public static LocationRequester getInstance(Context ctx, LocationChangedListener listener){
        if(instance==null){
            instance = new LocationRequester(ctx, listener);
        }
        return instance;
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Get first reading. Get additional location updates if necessary
        if (servicesAvailable() == ConnectionResult.SUCCESS) {
            // Get best last location measurement meeting criteria
            setLastLocation(bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN));

            if (null == getLastLocation()
                    || getLastLocation().getAccuracy() > MIN_LAST_READ_ACCURACY
                    || getLastLocation().getTime() < System.currentTimeMillis() - TWO_MIN) {

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                // Schedule a runnable to unregister location listeners
                Executors.newScheduledThreadPool(1).schedule(new Runnable() {


                    @Override
                    public void run() {
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, LocationRequester.this);
                    }

                }, ONE_MIN, TimeUnit.MILLISECONDS);
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // Determine whether new location is better than current best
        // estimate
        if (null == getLastLocation() || location.getAccuracy() < getLastLocation().getAccuracy()) {
            setLastLocation(location);
            if (getLastLocation().getAccuracy() < MIN_ACCURACY) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }

    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }



    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public int servicesAvailable() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(currentContext);
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
        fireLocationChangedListeners(this.lastLocation);
    }

    private void fireLocationChangedListeners(Location newLocation) {
        if(this.listeners!=null && this.listeners.size()>0){
            for (LocationChangedListener listener : listeners){
                listener.locationChanged(newLocation);
            }
        }
    }
}

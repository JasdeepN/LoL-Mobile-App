package uoit.csci4100u.mobileapp.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jasdeep on 2017-11-06.
 *
 * used to listen for GPS based movement, set sensitivity based on MIN_DISTANCE and MIN_TIME
 * location must be received from LocationUpdate before things can be checked
 */

//TODO: get current location on app launch
//TODO: create set home location method
//TODO: create check if at home method and call this in the on location changed to see
// if still at home

public class LocationUtil extends Service implements LocationListener {
    /**
     * Global Variable Declarations
     *
     * set MIN_DISTANCE and MIN_TIME to tune the sensitivity of the Location Listener
     *
     * MIN_DISTANCE = minimum distance to trigger LocationListener
     * MIN_TIME = minimum time to wait before checking location again
     * loc = current optimal location
     * locManager = Location Manager object, contains methods related to Location
     * mContext = Context acquired from calling Activity
     */
    private static final String TAG = "location util";
    private Location loc;
    public LocationManager locManager;

    //static time variables
    private static final int MILLISECONDS = 1000;
    private static final int SECONDS = 60;

    // x2 makes it 2 minutes (1000 * 60 * 2ms)
    private static final int MIN_TIME = MILLISECONDS * SECONDS * 2;
    private static final int MIN_DISTANCE = 10;
    private final Context mContext;

    /**
     * Default constructor for LocationUtil
     *
     * @param context calling Activity's context
     */
    public LocationUtil(Context context) {
        this.mContext = context;
        locManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE,
                    this);
        } catch (SecurityException e) {
            Log.i(TAG, e.toString());
        }
    }


    //TODO: implement the onBind method
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Returns the current optimal location
     *
     * @return current optimal Location object
     */
    public Location getLocation() {
        return loc;
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     *
     * @return True if the new Location is better then the current best Location
     *         False if the new Location is worse then the current best Location
     *
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > MIN_TIME;
        boolean isSignificantlyOlder = timeDelta < -MIN_TIME;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, loc)) {
            loc = location;
            //TODO: remove log after debugging
            Log.d(TAG, loc.toString());
        }
    }

    //TODO: implement what happens when the GPS status changes
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    //TODO: implement what happens when the GPS provider is enabled
    @Override
    public void onProviderEnabled(String s) {

    }

    //TODO: implement what happens when the GPS provider is disabled
    @Override
    public void onProviderDisabled(String s) {

    }
}

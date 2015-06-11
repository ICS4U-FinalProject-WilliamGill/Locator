package com.williamgill.com.locator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.Criteria.ACCURACY_FINE;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.location.LocationManager.PASSIVE_PROVIDER;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.model.people.Person;

import java.util.ArrayList;
/**
 * William Gill
 * ICS4U-1
 * Locator
 * 6/09/2015
 */

public class MapsActivity extends FragmentActivity {

    public static EditText userSpace;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Marker curMarker;
    public static Location myLocation;
    Firebase userRef;
    public String lookFor = "";

   public boolean check = true;



    ArrayList<PersonalLocation> personalLocations;

    Firebase myFirebaseRef;

    public double longitude = 0;
    public double latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        userSpace = (EditText) findViewById(R.id.editUser_ET);

         myFirebaseRef = new Firebase("https://intense-inferno-6530.firebaseio.com/");
        userRef = myFirebaseRef.child("Users");

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }


    /**
     *  Goes through the different providers to get the current location
     *  If the 'best' location is null it will look for the last known location
     * @param context
     * @return Location - gives a Location object
     */
    public static Location getLatestLocation(final Context context) {
        LocationManager manager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(ACCURACY_FINE);
        String provider = manager.getBestProvider(criteria, true);
        Location bestLocation;
        if (provider != null)
            bestLocation = manager.getLastKnownLocation(provider);
        else
            bestLocation = null;
        Location latestLocation = getLatest(bestLocation,
                manager.getLastKnownLocation(GPS_PROVIDER));
        latestLocation = getLatest(latestLocation,
                manager.getLastKnownLocation(NETWORK_PROVIDER));
        latestLocation = getLatest(latestLocation,
                manager.getLastKnownLocation(PASSIVE_PROVIDER));
        return latestLocation;


    }

    /**
     * Looks at the two locations and determines which one is more recent
     * @param location1 -
     * @param location2
     * @return - the more recent location
     */
    private static Location getLatest(final Location location1,
                                      final Location location2) {
        if (location1 == null)
            return location2;

        if (location2 == null)
            return location1;

        if (location2.getTime() > location1.getTime())
            return location2;
        else
            return location1;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();

            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    public void setUpMap() {
        // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        // myLocation = locationManager.getLastKnownLocation(provider);

        myLocation = getLatestLocation(getApplicationContext());

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get latitude of the current location

        double latitude = myLocation.getLatitude();
        // double latitude = 5;


        // Get longitude of the current location
        double longitude = myLocation.getLongitude();
        // double longitude = 5;

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        curMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));


    }

    /**
     * Animate Marker
     * Allows the marker to move to a desired location
     * credit to http://stackoverflow.com/questions/13872803/how-to-animate-marker-in-android-map-api-v2
     * @param marker
     * @param toPosition
     * @param hideMarker
     */

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    /**
     * Updates the latitude and longitude from the Firebase database
     */
    public void updateData(){



        userRef.child(lookFor).child("Longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.getValue() == null) {
                    Toast.makeText(getApplicationContext(), "User does not exist!",
                            Toast.LENGTH_LONG).show();
                    check = false;

                }
                else {

                    longitude = (Double) snapshot.getValue();
                }
            }
            @Override public void onCancelled(FirebaseError error) {

            }
        });

        userRef.child(lookFor).child("Latitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(check) {
                    latitude = (Double) snapshot.getValue();
                }



            }
            @Override public void onCancelled(FirebaseError error) {

            }
        });



    }


    /**
     * Method that controls the buttons on the MapsActivity
     * animate_button - controls when to move the markers
     * animate_back - move the marker back to the current users location
     * confirm_buttonB - locks in the users decision of a usernames
     * @param v
     */

    public void onAnimate(View v){
        switch(v.getId()){
            case R.id.animate_button:
                //LatLng a = new LatLng(myLocation.getLatitude()+0.5, myLocation.getLongitude() +0.5);
                //Marker ad = mMap.addMarker(new MarkerOptions().position(a).title("updated!").snippet("Consider yourself located"));

                //animateMarker(curMarker, new LatLng(myLocation.getLatitude() + 0.5, myLocation.getLongitude() + 0.5), false);

                updateData();




                SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                String name = settings.getString("Name", "DEFAULT");

                if(MainActivity.sendData.isChecked()) {
                    userRef.child(name).setValue(name);
                    userRef.child(name).child("Longitude").setValue(myLocation.getLongitude() + 0.5);
                    userRef.child(name).child("Latitude").setValue(myLocation.getLatitude() + 0.5);
                }




                if(check) {
                    animateMarker(curMarker, new LatLng(latitude, longitude), false);
                }

                    userSpace.setEnabled(true);


                break;

            case R.id.animate_back:
                animateMarker(curMarker, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), false);
               // myFirebaseRef.child("Latitude").setValue(myLocation.getLatitude() + 0.5);
               // myFirebaseRef.child("Longitude").setValue(myLocation.getLongitude() + 0.5);

                break;

            case R.id.confirm_buttonB:
                userSpace.setEnabled(false);
                    lookFor = userSpace.getText().toString();
                break;

        }


    }


}


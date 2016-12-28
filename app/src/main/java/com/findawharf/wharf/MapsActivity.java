package com.findawharf.wharf;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private GoogleMap mMap;
    private static final String TAG = "venueData";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference venueRef = database.getReference().child("venues");
    private Location mLastLocation;
    private Location mCurrentLocation;
    private LatLng mLastLatLng;
    LocationRequest mLocationRequest = new LocationRequest();
    private GoogleApiClient mGoogleApiClient;
    private MarkerOptions mLocMarker;
    List<HashMap<String, String>> mVenues = new ArrayList<HashMap<String, String>>();
    ListView venueListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Map");
        spec1.setContent(R.id.mapsTab);
        spec1.setIndicator("Map");
        tabHost.addTab(spec1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("List");
        spec2.setContent(R.id.venuesTab);
        spec2.setIndicator("List");
        tabHost.addTab(spec2);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents (Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView venueName = (TextView) v.findViewById(R.id.venue_name);
                TextView venueAddress = (TextView) v.findViewById(R.id.venue_address);
                TextView venueDistance = (TextView) v.findViewById(R.id.venue_distance);
                TextView venueAppleStock = (TextView) v.findViewById(R.id.venue_apple_stock);
                TextView venueUSBStock = (TextView) v.findViewById(R.id.venue_usb_stock);

                float[] results = new float[1];

                Integer i = Integer.parseInt(marker.getTitle());
                LatLng venueLatLng = marker.getPosition();
                Double distanceToVenue = CalculationByDistance(mLastLatLng, venueLatLng);
                String stringDistance = String.format("%.1f",distanceToVenue);

                venueName.setText(mVenues.get(i).get("Name"));
                venueAddress.setText(mVenues.get(i).get("Address"));
                venueDistance.setText(stringDistance + " km");
                venueAppleStock.setText("3");
                venueUSBStock.setText("2");

                return v;
            }

        });

        Log.i(TAG, "Made it into onMapReady() method");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        }

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        //AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
        Log.i(TAG, "Made it into onStart() method");

    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            new GetLocation().execute();

        }

        createLocationRequest();

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Request location permissions
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, (LocationListener) this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (location != null) {
            // TODO: not sure yet
        }
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                *Math.cos(Math.toRadians(lat2))
                *Math.sin(dLon/2)
                *Math.sin(dLon/2);
        double c = 2*Math.asin(Math.sqrt(a));
        double valueResult = Radius*c;
        double km = valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult%1000;
        int meterInDec = Integer.valueOf(newFormat.format(km));
        Log.i(TAG, "" + valueResult + " km " + kmInDec + " meter " + meterInDec);

        return Radius*c;
    }

    public class VenueAdapter extends ArrayAdapter {

        private int resource;
        private LayoutInflater inflater;

        public VenueAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.venue_list, null);
            }

            TextView venueName = (TextView) convertView.findViewById(R.id.venue_name);
            TextView venueAddress = (TextView) convertView.findViewById(R.id.venue_address);
            TextView venueDistance = (TextView) convertView.findViewById(R.id.venue_distance);
            TextView venueAppleStock = (TextView) convertView.findViewById(R.id.venue_apple_stock);
            TextView venueUSBStock = (TextView) convertView.findViewById(R.id.venue_usb_stock);

            Double latitude = Double.parseDouble(mVenues.get(position).get("Latitude"));
            Double longitude = Double.parseDouble(mVenues.get(position).get("Longitude"));

            LatLng venueLatLng = new LatLng(latitude, longitude);
            Double distanceToVenue = CalculationByDistance(mLastLatLng, venueLatLng);
            String stringDistance = String.format("%.1f",distanceToVenue);

            venueName.setText(mVenues.get(position).get("Name"));
            venueAddress.setText(mVenues.get(position).get("Address"));
            venueDistance.setText(stringDistance + " km");
            venueAppleStock.setText("3");
            venueUSBStock.setText("2");

            return convertView;
        }
    }

    private class GetLocation extends AsyncTask<Object, Object, Void> {

        @Override
        protected Void doInBackground(Object... params) {

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    mLastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mLocMarker = new MarkerOptions()
                            .position(mLastLatLng)
                            .title("Current Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mLastLocation != null) {

                mMap.addMarker(mLocMarker);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mLastLatLng)      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to north
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

            venueRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mVenues = new ArrayList<HashMap<String, String>>();
                    int i = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //Typecasting java.util.Object as HashMap<String,String>>
                        HashMap<String, String> thisVenue = (HashMap<String, String>) postSnapshot.getValue();
                        Double latitude = Double.parseDouble(thisVenue.get("Latitude"));
                        Double longitude = Double.parseDouble(thisVenue.get("Longitude"));

                        LatLng venueLatLng = new LatLng(latitude,longitude);

                        Double distanceToVenue = CalculationByDistance(mLastLatLng, venueLatLng);
                        String stringDistance = String.format("%.1f",distanceToVenue);

                        thisVenue.put("Distance",stringDistance);

                        Log.i(TAG, "Array is " + thisVenue);

                        mVenues.add(i, thisVenue);
                        i++;
                        Log.i(TAG, "Value is: " + postSnapshot);
                    }

                    for (HashMap<String, String> v : mVenues) {

                        final String name = v.get("Name");
                        final String address = v.get("Address");
                        String city = v.get("City");
                        String category = v.get("Category");
                        Double latitude = Double.parseDouble(v.get("Latitude"));
                        Double longitude = Double.parseDouble(v.get("Longitude"));
                        String machine = v.get("Machine");

                        Log.i(TAG, "Name is " + name + ", Latitude is " + latitude + ", Longitude is " + longitude);
                        Log.i(TAG, "Array number is " + v);

                        String catImage;
                        switch (category) {
                            case "Bar":  catImage = "beer";
                                break;
                            case "Cafe":  catImage = "cafe";
                                break;
                            case "Casino":  catImage = "casino";
                                break;
                            case "Hotel":  catImage = "hotel";
                                break;
                            case "Restaurant":  catImage = "restaurant";
                                break;
                            case "Transit":  catImage = "transit";
                                break;
                            default: catImage = "hotel";
                                break;
                        }

                        MarkerOptions venueMarker = new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title("" + mVenues.indexOf(v))
                                .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(catImage, "drawable", getPackageName())));

                        mMap.addMarker(venueMarker);

                        VenueAdapter adapter = new VenueAdapter(getApplicationContext(), R.layout.venue_list, mVenues);

                        venueListView = (ListView) findViewById(R.id.list);

                        venueListView.setAdapter(adapter);

                    }

                    Log.i(TAG, "Firebase Connected");

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.i(TAG, "Failed to read value.", error.toException());
                }
            });

        }

        @Override
        protected void onPreExecute() {}

    }

}


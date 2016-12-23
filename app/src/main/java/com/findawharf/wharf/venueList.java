package com.findawharf.wharf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/*public class VenueList extends ListActivity {

    List<HashMap<String, String>> mVenues = new ArrayList<HashMap<String, String>>();
    private ProgressDialog pDialog;
    private static final String TAG = "venueData";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference venueRef = database.getReference().child("venues");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_venues);

        // Loading INBOX in Background Thread
        new LoadInbox().execute();
    }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VenueList.this);
            pDialog.setMessage("Loading List ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        venueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mVenues = new ArrayList<HashMap<String, String>>();
                int i = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Typecasting java.util.Object as HashMap<String,String>>
                    HashMap<String, String> thisVenue = (HashMap<String, String>) postSnapshot.getValue();
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

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter = new SimpleAdapter(
                            InboxActivity.this, inboxList,
                            R.layout.inbox_list_item, new String[] { TAG_FROM, TAG_SUBJECT, TAG_DATE },
                            new int[] { R.id.from, R.id.subject, R.id.date });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }

}*/

package dk.reuseapp.reuseapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class SearchActivity extends Activity {
    private ArrayList<PostInfo> postInfoArrayList;
    private ArrayList<PostInfo> postsForView;
    private DatabaseReference fdatabase;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private String searchFilter;
    private SeekBar geoSlider;
    private float maxDistance;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private TextView distanceView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        fdatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLocations().size() == 0) {
                    return;
                }
                else {
                    lastLocation = locationResult.getLastLocation();
                    fusedLocationProviderClient.removeLocationUpdates(this);
                }
            }
        };
        final SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*searchFilter = query;
                applyFilter();*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFilter = newText;
                applyFilters();
                return false;
            }
        });
        /*int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = searchView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("",true);
            }
        });*/

        searchFilter = null;
        postInfoArrayList = new ArrayList();
        postsForView = new ArrayList<>();
        getAllPosts();

        distanceView = findViewById(R.id.distanceView);

        setDistanceView(0);
        geoSlider = findViewById(R.id.slider);
        geoSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setDistanceView(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ;
            }
        });
        applyFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(SearchActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
        }
        LocationRequest locationRequest  = LocationRequest.create()
                .setNumUpdates(1)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(0);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                null);
    }

    private float getMaxDistance(int progress) {
        return (float) (0.5 * progress);
    }

    private void setDistanceView(int progress) {
        if (progress == 0) {
            distanceView.setText(context.getString(R.string.infinity) +" KM");
            applyFilters();
            maxDistance = 0;
            return;
        }
        String distanceText = getMaxDistance(progress) + " KM";
        distanceView.setText(distanceText);
        maxDistance = getMaxDistance(progress)*1000;
        applyFilters();
    }

    public void getAllPosts(){
        fdatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostInfo postInfo = dataSnapshot.getValue(PostInfo.class);
                postInfo.id = Long.parseLong(dataSnapshot.getKey());
                if(!postInfoArrayList.contains(postInfo)){
                    postInfoArrayList.add(postInfo);
                    Collections.sort(postInfoArrayList);
                    addPostForView(postInfo, true);
                    //recyclerViewAdapter.notifyItemInserted(0);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
        setUpRecyclerView();
    }

    private boolean checkDistance(PostInfo postInfo) {
        if (lastLocation == null || distanceView.getText().toString().equals(context.getString(R.string.infinity) +" KM")) {
            return true;
        }
        return Util.parseLocation(postInfo.getLocation()).distanceTo(lastLocation) < maxDistance;
    }

    private boolean checkTitle(PostInfo postInfo) {
        if (searchFilter == null || searchFilter.equals("") || searchFilter.trim().equals("")) {
            return true;
        }
        else {
            return postInfo.getTitle().contains(searchFilter);
        }
    }

    private void applyFilters() {
        ArrayList<PostInfo> newList = new ArrayList<>();
        for (PostInfo p : postInfoArrayList) {
            if (checkTitle(p) && checkDistance(p)) {
                newList.add(p);
            }
        }
        postsForView.clear();
        postsForView.addAll(newList);
        Collections.sort(postsForView);

        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void addPostForView(PostInfo post, boolean sort) {
        if (searchFilter == null || post.getTitle().contains(searchFilter)) {
            postsForView.add(post);
            if (sort) {
                Collections.sort(postsForView);
            }
            recyclerViewAdapter.notifyItemInserted(postsForView.indexOf(post));
        }
    }

    public void setUpRecyclerView(){
        recyclerView = findViewById(R.id.recyclerviewid);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(postsForView,this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}

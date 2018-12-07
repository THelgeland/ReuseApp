package dk.reuseapp.reuseapp;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.SupportMapFragment;


public class ResultActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private PostInfo post;
    private boolean creator;
    private boolean owner;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.post_container);
        long visit = System.currentTimeMillis();

        Intent intent = getIntent();
        post = (PostInfo) intent.getSerializableExtra("PostInfo");
        if (visit > post.id && visit - post.id < 4000) {
            creator = true;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLocations().size() == 0) {
                    return;
                }
                else {
                    lastLocation = locationResult.getLastLocation();
                    if (lastLocation.distanceTo(Util.parseLocation(post.getLocation())) < 50) {
                        owner = true;
                    }
                    else {
                        owner = false;
                    }
                }
                updateView();
            }
        };
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMapsID);
        mapFragment.getMapAsync(this);
        TextView titleView = findViewById(R.id.postcontainerTitleID);
        titleView.setText(post.getTitle());
        TextView descView = findViewById(R.id.postcontainerDescriptionID);
        descView.setText(post.getDescription());
        ImageView imageView =findViewById(R.id.picturecontainerID);
        Glide.with(this).load(post.getPicture()).into(imageView);
        RequestOptions options= new RequestOptions();
        options.centerCrop();
        Glide.with(this).load(post.getPicture()).apply(options).into(imageView);
    }

    private void updateView() {
        if (owner) {
            Toast.makeText(ResultActivity.this, "You own this", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(ResultActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
        }
        LocationRequest locationRequest  = LocationRequest.create()
                .setNumUpdates(1000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(1000);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        Location postLocation = Util.parseLocation(post.getLocation());
        LatLng postLatLng = new LatLng(postLocation.getLatitude(), postLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(postLatLng).title("Post Position"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(postLatLng,17.0f));
    }

    /**
     * When back is pressed by the creator (just after publishing) the back stack is popped.
     * Otherwise behaviour is normal. This is of course a heuristic solution, but it should work
     * virtually all the time and when it doesn't it isn't critical.
     */
    @Override
    public void onBackPressed() {
        if (creator) {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            super.onBackPressed();
        }
    }
}

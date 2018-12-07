package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.post_container);

        Intent intent = getIntent();
        post = (PostInfo) intent.getSerializableExtra("PostInfo");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMapsID);
        mapFragment.getMapAsync(this);
        TextView titleView = findViewById(R.id.postcontainerTitleID);
        titleView.setText(post.getTitle());
        TextView descView = findViewById(R.id.postcontainerDescriptionID);
        descView.setText(post.getDescription());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        Location postLocation = Util.parseLocation(post.getLocation());
        LatLng postLatLng = new LatLng(postLocation.getLatitude(), postLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(postLatLng).title("Post Position"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(postLatLng,17.0f));
    }
}

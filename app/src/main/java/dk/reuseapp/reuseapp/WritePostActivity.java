package dk.reuseapp.reuseapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * This activity is for writing a title and description for the post. It also uploads the post
 * with the user location.
 *
 * @author Torkil Helgeland
 */

public class WritePostActivity extends Activity {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference fdatabase;
    private FirebaseAuth fauth;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private Context context;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        image = intent.getExtras().getParcelable("data");

        setContentView(R.layout.activity_write_post);
        context = this;
        fdatabase=FirebaseDatabase.getInstance().getReference();
        fauth = FirebaseAuth.getInstance();
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

        final EditText title = findViewById(R.id.title_field);
        final EditText description = findViewById(R.id.description_field);

        Button postButton = findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().length() == 0) {
                    Toast.makeText(WritePostActivity.this,
                            "Please write a title before posting", Toast.LENGTH_LONG).show();
                } else {

                    upload(description.getText().toString(), title.getText().toString());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = fauth.getCurrentUser();
        if (user == null) {
            fauth.signInAnonymously().addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(WritePostActivity.this,
                                "Database Authentication Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(WritePostActivity.this,
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

    /*@Override
    protected void onSaveInstanceState(Bundle bundleToSave) {
        super.onSaveInstanceState(bundleToSave);
        bundleToSave.putParcelable("data", image);
    }*/

    public String getDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df= new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate=df.format(c);
        return formattedDate;
    }
    public void upload(final String text, final String title){
        if (lastLocation == null) {
            Toast.makeText(WritePostActivity.this, "Couldn't find your location", Toast.LENGTH_LONG).show();
            return;
        }
        final String locationString = lastLocation.getLatitude() + ";" + lastLocation.getLongitude();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] data = baos.toByteArray();

        String path = "img" + UUID.randomUUID() + ".png";
        final StorageReference ref = storage.getReference().child(path);
        UploadTask uploadTask = ref.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadURL = downloadUri.toString();
                    long timeInMillis = System.currentTimeMillis();
                    PostInfo postToSend = new PostInfo(locationString, text, downloadURL, title, getDate());
                    postToSend.id = timeInMillis;
                    Map<String, Object> postValues = postToSend.toMap();
                    fdatabase.child("Post").child(timeInMillis + "").updateChildren(postValues);
                    Intent intent = new Intent(context, ResultActivity.class);
                    intent.putExtra("PostInfo", postToSend);
                    context.startActivity(intent);

                } else {
                    // Handle errors
                }
            }
        });
    }
}

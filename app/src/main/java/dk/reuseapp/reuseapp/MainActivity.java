package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * This is the main activity that users enter when opening the app, it has two buttons
 * one for each high level action
 *
 * @author Torkil Helgeland
 */
public class MainActivity extends Activity {
    private ImageButton searchButton;
    private ImageButton uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clicking the search button starts the search activity
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clicking the upload button starts the upload activity
                Intent intent = new Intent(MainActivity.this, UploadCameraActivity.class);
                startActivity(intent);
            }
        });
    }
}

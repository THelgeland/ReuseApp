package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This is the in app camera activity. It's a bare bone camera interface with a camera preview
 * and a capture button.
 *
 * @author Torkil Helgeland
 */
//TODO: Refactor to control the camera object directly in this view and use it to improve its
//TODO: behaviour in the activity life cycle

//TODO: Implement saving location data here when taking a picture, the camera should have permission
public class UploadCameraActivity extends Activity {
    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_uploadcamera);
        //We initialize the camera view and add it to the layout
        cameraPreview = new CameraPreview(this);

        cameraPreview.startCamera(Util.getCamera(0));
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
        super.onCreate(savedInstanceState);


        ImageButton captureButton = findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This architecture is slightly confusing, the real magic happens in the
                //callback function further down.
                //cameraPreview.takePicture(pictureCallBack);
                Intent intent = new Intent(UploadCameraActivity.this,
                        VerifyPictureActivity.class);
                startActivity(intent);
            }
        });
    }

    private Camera.PictureCallback pictureCallBack = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //When the capture button is clicked, the data stream from the camera is written to a
            //temporary file on disk
            File pictureFile = Util.getTempImageFile();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                fileOutputStream.write(data);
                fileOutputStream.close();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                Log.d("ReuseApp", "Something went wrong saving picture");
            }
            //After writing the image to disk, we start the image verification view
            //The code for starting new the next activity is here because takePicture
            //is an asynchronous call.
            Intent intent = new Intent(UploadCameraActivity.this,
                    VerifyPictureActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

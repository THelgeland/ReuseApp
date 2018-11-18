package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UploadCameraActivity extends Activity {
    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_uploadcamera);
        cameraPreview = new CameraPreview(this);

        cameraPreview.startCamera(Util.getCamera(0));
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
        super.onCreate(savedInstanceState);

        ImageButton captureButton = findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreview.takePicture(pictureCallBack);
            }
        });
    }

    private Camera.PictureCallback pictureCallBack = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = Util.getOutPutMediaFile();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                fileOutputStream.write(data);
                fileOutputStream.close();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                Log.d("ReuseApp", "Something went wrong saving picture");
            }
            cameraPreview.stopPreview();
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

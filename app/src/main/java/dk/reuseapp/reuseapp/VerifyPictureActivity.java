package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;

public class VerifyPictureActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_verify_picture);
        File imageFile = Util.getOutPutMediaFile();
        if (imageFile.exists()) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            ImageView image = findViewById(R.id.pictureViewForVerification);
            image.setImageBitmap(imageBitmap);
        }
        else {
            Log.d("ReuseApp", "Couldn't find image file");
        }
    }
}

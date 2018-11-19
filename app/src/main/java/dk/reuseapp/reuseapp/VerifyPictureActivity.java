package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

/**
 * In this activity the user can see the picture they've just taken and decide whether they're
 * happy with it.
 *
 * @author Torkil Helgeland
 */
public class VerifyPictureActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_verify_picture);
        //The temporary image file is retrieved from disk and decoded into a bitmap image
        File imageFile = Util.getTempImageFile();
        if (imageFile.exists()) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            ImageView image = findViewById(R.id.pictureViewForVerification);
            image.setImageBitmap(imageBitmap);
        }
        else {
            Log.d("ReuseApp", "Couldn't find image file");
        }
        //The flow here works quite nicely thanks to only allowing one photo, if the user goes back
        //instead of clicking next, they simply get to overwrite the first image.
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerifyPictureActivity.this,
                        WritePostActivity.class);
                startActivity(intent);
            }
        });
    }
}

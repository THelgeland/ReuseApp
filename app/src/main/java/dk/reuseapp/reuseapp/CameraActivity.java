package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

public class CameraActivity extends Activity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            Bundle extras = data.getExtras();
            //image = (Bitmap) extras.get("data");
            Intent intent = new Intent(CameraActivity.this, WritePostActivity.class);
            intent.putExtra("data", (Bitmap) extras.get("data"));
            startActivity(intent);
        }
    }

    private void startTakePictureIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent, 1);
        }
    }
}

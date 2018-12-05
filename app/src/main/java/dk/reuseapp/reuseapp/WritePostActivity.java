package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Map;
import java.util.UUID;

/**
 * This activity is for writing a title and description for the post.
 *
 * @author Torkil Helgeland
 */

//TODO: Write description to file as well, figure out a suitable data model.

//TODO: Modify the back stack when pressing 'Post'
public class WritePostActivity extends Activity {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference fdatabase;
    private FirebaseAuth fauth;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_write_post);
        startTakePictureIntent();
        fdatabase=FirebaseDatabase.getInstance().getReference();
        fauth = FirebaseAuth.getInstance();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            Bundle extras = data.getExtras();
            image = (Bitmap) extras.get("data");
        }
    }

    private void startTakePictureIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent, 1);
        }
    }

    public void upload(final String text, final String title){

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
                    PostInfo testPost = new PostInfo("testdate", text, downloadURL, title, "testdate");
                    Map<String, Object> postValues = testPost.toMap();
                    fdatabase.child("Post").child(System.currentTimeMillis() + "").updateChildren(postValues);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
}

package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This activity is for writing a title and description for the post.
 *
 * @author Torkil Helgeland
 */

//TODO: Write description to file as well, figure out a suitable data model.

//TODO: Modify the back stack when pressing 'Post'
public class WritePostActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_write_post);

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
                    File textFile = Util.getTempTextFile();
                    try {
                        BufferedWriter bf = new BufferedWriter(new FileWriter(textFile));
                        bf.write(title.getText().toString());
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        });
    }
}

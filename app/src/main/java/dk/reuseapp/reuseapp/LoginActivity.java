package dk.reuseapp.reuseapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
/**
 * This is the login activity that users enter when opening the app, they enter their name
 * and email adress
 *
 * @author Henrik Tran
 */

public class LoginActivity extends Activity {

    private Button fBaseButton;

    private DatabaseReference fDatabase;

    private EditText fNameField;
    private EditText fEmailField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fBaseButton = (Button) findViewById(R.id.base_button);
        fDatabase = FirebaseDatabase.getInstance().getReference();

        fNameField = (EditText) findViewById(R.id.name_field);
        fEmailField = (EditText) findViewById(R.id.email_field);

        fBaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= fNameField.getText().toString().trim();
                String email=fEmailField.getText().toString().trim();
                HashMap<String,String> dataMap=new HashMap<String,String>();
                dataMap.put("Name",name);
                dataMap.put("Email",email);
                fDatabase.push().setValue(dataMap);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

        });
    }

}

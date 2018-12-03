package dk.reuseapp.reuseapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private EditText mName,mEmail;
    private Button mloginButton;
    private CheckBox mcheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mName =(EditText) findViewById(R.id.name_field);
        mEmail=(EditText) findViewById(R.id.email_field);
        mloginButton=(Button) findViewById(R.id.loginButton);
        mcheckBox=(CheckBox) findViewById(R.id.checkBox);

        mPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mPreferences.edit();

        checkSharedPreferences();
        mloginButton.findViewById(R.id.loginButton);
        mloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcheckBox.isChecked()) {
                    mEditor.putString(getString(R.string.checkbox), "True");
                    mEditor.commit();

                    String name = mName.getText().toString();
                    mEditor.putString(getString(R.string.name), name);
                    mEditor.commit();

                    String email = mEmail.getText().toString();
                    mEditor.putString(getString(R.string.email), email);
                    mEditor.commit();
                } else {
                    mEditor.putString(getString(R.string.checkbox), "False");
                    mEditor.commit();

                    String name = mName.getText().toString();
                    mEditor.putString(getString(R.string.name), "");
                    mEditor.commit();

                    String email = mEmail.getText().toString();
                    mEditor.putString(getString(R.string.email), "");
                    mEditor.commit();
                    Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkSharedPreferences(){
        String name = mPreferences.getString(getString(R.string.name),"");
        String email = mPreferences.getString(getString(R.string.email),"");
        String checkbox = mPreferences.getString(getString(R.string.checkbox),"False");

        mName.setText(name);
        mEmail.setText(email);

        if(checkbox.equals("True")) {
            mcheckBox.setChecked(true);
        }else{
                mcheckBox.setChecked(false);
            }
    }

}

package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG=RegisterActivity.class.getName();
    private static final String PREF_KEY= RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY=99;
    EditText userNameEditText;
    EditText userEamilEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    EditText phoneEditText;
    EditText addressEditText;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Bundle bundle=getIntent().getExtras();
        int secret_key=getIntent().getIntExtra("SECRET_KEY",0);

        if(secret_key!=99){
            finish();
        }

        userNameEditText=findViewById(R.id.usernameEditText);
        userEamilEditText=findViewById(R.id.userEmailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        passwordAgainEditText=findViewById(R.id.passwordAgainEditText);
        phoneEditText=findViewById(R.id.phoneEditText);
        addressEditText=findViewById(R.id.addressEditText);

        preferences=getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName","");
        String password= preferences.getString("password", "");

        userNameEditText.setText(userName);
        passwordEditText.setText(password);
        passwordAgainEditText.setText(password);

        mAuth=FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");

    }

    public void register(View view) {
        String userName=userNameEditText.getText().toString();
        String email=userEamilEditText.getText().toString();
        String password=passwordEditText.getText().toString();
        String passwordAgain=passwordAgainEditText.getText().toString();

        if (!password.equals(passwordAgain)){
            Log.e(LOG_TAG,"Nem egyezik meg a jelszó megerősjtése.");
            return;
        }

        String phoneNumber=phoneEditText.getText().toString();
        String address=addressEditText.getText().toString();

        Log.i(LOG_TAG,"Regiszrált: "+ userName + ", "+email+", " + password + ", "+passwordAgain);

        //openShop();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG, "User sikeresen létrehozva");
                    openShop();
                }else{
                    Log.i(LOG_TAG, "User-t nem sikerült létrehozni");
                    Toast.makeText(RegisterActivity.this, "User-t nem sikerült létrehozni: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void cancel(View view) {
        finish();
    }

    private void openShop(/* user data */){
        Intent intent = new Intent(this, ShopListActivity.class);
        //intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
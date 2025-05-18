package com.example.test;


import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private static final String LOG_TAG=MainActivity.class.getName();
    private static final String PREF_KEY= MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY=99;
    EditText userNameET;
    EditText passwordET;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameET=findViewById(R.id.userName);
        passwordET=findViewById(R.id.jelszoID);

        preferences=getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        //random async task
        //Button button=findViewById(R.id.loginGuest);
        //new RandomAsyncTask(button).execute();

        //random async loader
        getSupportLoaderManager().restartLoader(0, null, this);


        Log.i(LOG_TAG, "onCreate");
    }

    public void login(View view){

        String userName=userNameET.getText().toString();
        String password=passwordET.getText().toString();

        mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG,"Bejelentkezett: "+ userName + ", " + password);
                    openShop();
                }else{
                    Log.i(LOG_TAG, "Nem sikerült bejelentkezni");
                    Toast.makeText(MainActivity.this, "Nem sikerült bejelentkezni: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.i(LOG_TAG,"Bejelentkezett: "+ userName + ", " + password);
    }

    public void register(View view) {
        Intent intent=new Intent(this, RegisterActivity.class);

        intent.putExtra("SECRET_KEY",99);
        startActivity(intent);
    }

    private void openShop(/* user data */){
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);

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

        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("userName", userNameET.getText().toString());
        editor.putString("password", passwordET.getText().toString());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG,"Anopnymus loged in suscessfully!");
                    openShop();
                }else{
                    Log.i(LOG_TAG, "Nem sikerült bejelentkezni");
                    Toast.makeText(MainActivity.this, "Nem sikerült bejelentkezni: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new RandomAsyncLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Button button=findViewById(R.id.loginGuest);
        button.setText(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
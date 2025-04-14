package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShopListActivity extends AppCompatActivity {

    private static final String LOG_TAG=ShopListActivity.class.getName();
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        user= FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.i(LOG_TAG, "Authenticated User!");
        }else{
            Log.i(LOG_TAG, "Unauthenticated User!");
            finish();
        }

    }

    public void logout(View view) {
        finish();
    }
}
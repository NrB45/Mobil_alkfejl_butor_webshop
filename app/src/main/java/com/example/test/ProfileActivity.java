package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameTextView, emailTextView;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = findViewById(R.id.profile_name);
        emailTextView = findViewById(R.id.profile_email);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (user.isAnonymous()) {
                nameTextView.setText("Vendégként használja a shoppot!");
                animateText(nameTextView);
                emailTextView.setVisibility(View.GONE); // elrejtjük az email mezőt
            }  else {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");

                            nameTextView.setText("Név: " + name);
                            emailTextView.setText("Email: " + email);

                            animateText(nameTextView);
                            animateText(emailTextView);
                        });
            }
        }
    }

    public void goBack(View view) {
        finish();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void animateText(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.startAnimation(animation);
    }
}

package com.example.luxevista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is already logged in
        if (FirebaseManager.getInstance().isUserLoggedIn()) {
            String email = FirebaseManager.getInstance().getCurrentUser().getEmail();
            if (email != null && email.equalsIgnoreCase("admin@gmail.com")) {
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
            finish();
        }

        Button loginButton = findViewById(R.id.loginButton);
        TextView signUpText = findViewById(R.id.signUpText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }
}

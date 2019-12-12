package com.example.problem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.problem.Fragment.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    SharedPreferences shared_preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        loadText();


    }

    private void loadText() {
        shared_preferences = getPreferences(Context.MODE_PRIVATE);
        String login = shared_preferences.getString("login", "");
        String pass = shared_preferences.getString("pass", "");
        if (isNetworkConnected()) {
            if (!login.isEmpty() && !pass.isEmpty()) {
                sing_in(login, pass);
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_conteneier, new LoginFragment()).commit();
            }
        } else {
            Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void sing_in(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        LoginFragment.user = mAuth.getCurrentUser();
                        Intent intent = new Intent(MainActivity.this, PostActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_conteneier, new LoginFragment()).commit();
                    }

                });

    }

    private boolean isNetworkConnected() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            return (returnVal == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

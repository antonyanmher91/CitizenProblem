package com.example.problem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.problem.Fragment.LoginFragment;
import com.example.problem.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_conteneier,new LoginFragment()).commit();

    }
}

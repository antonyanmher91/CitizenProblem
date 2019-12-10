package com.example.problem.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.problem.PostActivity;
import com.example.problem.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private Button login;
    private TextView registr;
    private FirebaseAuth mAuth;
    public static FirebaseUser user;
    SharedPreferences sharedPreferences;

    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        findId(view);

        mAuth = FirebaseAuth.getInstance();
        registr.setOnClickListener(v -> getFragmentManager().beginTransaction().add(R.id.main_conteneier, new RegistrFragment()).commit());
        login.setOnClickListener(v1 -> sing_in(email.getText().toString(), password.getText().toString()));
        return view;
    }

    private void sing_in(String email, String password) {
        if (validate(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginFragment.this.getActivity(), PostActivity.class);
                            startActivity(intent);
                            saveText(email, password);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    });
        }

    }


    private boolean validate(String email, String password) {
        boolean valid = true;
        if (email.isEmpty()) {
            emailLayout.setError("Field shouldn't be empty");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Enter a valid email address");
            valid = false;
        } else {
            emailLayout.setError(null);
        }
        if (password.isEmpty()) {
            passwordLayout.setError("Field shouldn't be empty");
            valid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must not be less than 6 characters");
            valid = false;
        } else {
            passwordLayout.setError(null);
        }
        return valid;
    }


    private void findId(View view) {
        emailLayout = view.findViewById(R.id.email_layout);
        passwordLayout = view.findViewById(R.id.pass_layout);
        email = view.findViewById(R.id.login);
        password = view.findViewById(R.id.pass);
        login = view.findViewById(R.id.btn_login);
        registr = view.findViewById(R.id.registr);
    }

    public void saveText(String mail, String pass) {
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login", mail);
        editor.putString("pass", pass);
        editor.commit();

    }


}

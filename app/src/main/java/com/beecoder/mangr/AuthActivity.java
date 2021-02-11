package com.beecoder.mangr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private TextInputLayout til_email, til_password;
    private Button btn_login, btn_signUp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            openHomePage();
            finish();
            return;
        }
        setViews();
    }

    private void setViews() {
        til_email = findViewById(R.id.til_email);
        til_password = findViewById(R.id.til_password);
        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signUp);
        progressBar = findViewById(R.id.progressBar);

        btn_login.setOnClickListener(v -> handleLogin());
        btn_signUp.setOnClickListener(v -> {
            openSignUpPage();
            finish();
        });

        String emailFromSignUpPage = getIntent().getStringExtra(SignUpActivity.EMAIL_KEY);
        if (emailFromSignUpPage != null) {
            til_email.getEditText().setText(emailFromSignUpPage);
        }
    }

    private void handleLogin() {
        String email = til_email.getEditText().getText().toString();
        String password = til_password.getEditText().getText().toString();
        til_email.setErrorEnabled(false);
        til_password.setErrorEnabled(false);

        if (email.isEmpty())
            til_email.setError("Enter Email");
        else if (password.isEmpty())
            til_password.setError("Enter Password");
        else login(email, password);
    }

    private void login(String email, String password) {
        btn_login.setEnabled(false);
        btn_signUp.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btn_login.setEnabled(true);
                    btn_signUp.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        if (firebaseUser.isEmailVerified()) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                            openHomePage();
                            finish();
                        } else
                            Toast.makeText(this, "Your email address has not been verified", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                });
    }

    private void openHomePage() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void openSignUpPage() {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
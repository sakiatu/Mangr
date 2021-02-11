package com.beecoder.mangr;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    public static final String EMAIL_KEY = "EMAIL";
    private TextInputLayout til_name, til_email, til_password, til_confirm_password;
    private Button btn_login, btn_signUp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setViews();

    }

    private void setViews() {
        til_name = findViewById(R.id.til_name);
        til_email = findViewById(R.id.til_email);
        til_password = findViewById(R.id.til_password);
        til_confirm_password = findViewById(R.id.til_confirm_password);
        progressBar = findViewById(R.id.progressBar);

        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signUp);

        btn_login.setOnClickListener(v -> openLoginPage(null));
        btn_signUp.setOnClickListener(v -> handleSignUp());
    }

    private void handleSignUp() {
        String name = til_name.getEditText().getText().toString();
        String email = til_email.getEditText().getText().toString();
        String password = til_password.getEditText().getText().toString();
        String confirm_password = til_confirm_password.getEditText().getText().toString();

        til_name.setErrorEnabled(false);
        til_email.setErrorEnabled(false);
        til_password.setErrorEnabled(false);
        til_confirm_password.setErrorEnabled(false);

        if (name.isEmpty())
            til_name.setError("Enter Name");
        else if (email.isEmpty())
            til_email.setError("Enter Email");
        else if (password.isEmpty())
            til_password.setError("Enter Password");
        else if (confirm_password.isEmpty())
            til_confirm_password.setError("Confirm Password");
        else if (!password.equals(confirm_password))
            til_confirm_password.setError("Password not match");
        else
            createUser(email, password);

    }

    private void createUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        btn_signUp.setEnabled(false);
        btn_login.setEnabled(false);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            btn_signUp.setEnabled(true);
                            btn_login.setEnabled(true);
                            if (task1.isSuccessful())
                                showSuccessDialog(email);
                            else
                                Toast.makeText(this, task1.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        btn_signUp.setEnabled(true);
                        btn_login.setEnabled(true);
                        Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showSuccessDialog(String email) {
        String message = "We've sent an email to " + email +
                " to verify your address. Check email and click the verification link to verify.";
        new AlertDialog.Builder(this)
                .setTitle("Sign Up Successful!!!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (a, b) -> openLoginPage(email))
                .create().show();
    }

    private void openLoginPage(String email) {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.putExtra(EMAIL_KEY, email);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        openLoginPage(null);
        finish();
    }
}
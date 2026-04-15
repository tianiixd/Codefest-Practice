package com.example.codefestpractice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.codefestpractice.database.DatabaseHelper;
import com.example.codefestpractice.models.User;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextInputLayout tilFirstName, tilMiddleName, tilLastName, tilEmail, tilPassword;
    private TextInputEditText etFirstName, etMiddleName, etLastName, etEmail, etPassword;

    private TextView tvGoToLogin;

    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        connectComponents();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateRegister();
            }
        });

        tvGoToLogin.setOnClickListener(v -> intentToLogin());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void intentToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void connectComponents() {
        dbHelper = new DatabaseHelper(this);

        tilFirstName = findViewById(R.id.tilFirstName);
        tilMiddleName = findViewById(R.id.tilMiddleName);
        tilLastName = findViewById(R.id.tilLastName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);

        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);

        tvGoToLogin = findViewById(R.id.tvGoToLogin);
    }

    private void validateRegister() {
        boolean isValid = true;

        String firstName = String.valueOf(etFirstName.getText()).trim();
        String middleName = String.valueOf(etMiddleName.getText()).trim();
        String lastName = String.valueOf(etLastName.getText()).trim();

        String email = String.valueOf(etEmail.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();

        tilFirstName.setError(null);
        tilMiddleName.setError(null);
        tilLastName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        if (firstName.isEmpty()) {
            tilFirstName.setError("FirstName is required");
            isValid = false;
        }

        if (lastName.isEmpty()) {
            tilLastName.setError("LastName is required");
            isValid = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        } else {
            boolean emailExists = dbHelper.checkEmailExists(email);
            if (emailExists) {
                tilEmail.setError("This email is already registered");
                isValid = false;
            }
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }


        if (isValid) {
            User user = new User(email, password, "Customer", firstName, middleName, lastName);
            boolean isInserted = dbHelper.insertUser(user);

            if (isInserted) {
                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                intentToLogin();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "System Error: Could not save user.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
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
    private User user;
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

        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void connectComponents() {
        dbHelper = new DatabaseHelper(this);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);

        tvGoToLogin = findViewById(R.id.tvGoToLogin);
    }

    private void validateRegister() {
        boolean isvalid = true;

        String firstName = String.valueOf(etFirstName.getText()).trim();
        String middleName = String.valueOf(etMiddleName.getText()).trim();
        String lastName = String.valueOf(etLastName.getText()).trim();

        String email = String.valueOf(etEmail.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();

        tilEmail.setError(null);
        tilPassword.setError(null);

        if (firstName.isEmpty()) {
            tilFirstName.setError("FirstName is required");
            isvalid = false;
        }

        if (lastName.isEmpty()) {
            tilLastName.setError("LastName is required");
            isvalid = false;
        }


        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isvalid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isvalid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            isvalid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isvalid = false;
        }


        if (isvalid) {
            user = new User(firstName, middleName, lastName, email, password);
            boolean isInserted = dbHelper.insertUser(user);

            if (isInserted) {
                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        }

    }
}
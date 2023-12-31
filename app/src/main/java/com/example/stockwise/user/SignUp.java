package com.example.stockwise.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stockwise.R;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class SignUp extends AppCompatActivity {

    TextInputEditText textInputEditTextFullname, textInputEditTextUsername, textInputEditTextPassword,textInputEditTextEmail;
    Button buttonSignup;
    TextView textViewLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputEditTextFullname = findViewById(R.id.fullname);
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextEmail = findViewById(R.id.email);
        buttonSignup = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });


        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname, username,password, email;
                fullname = String.valueOf(textInputEditTextFullname.getText());
                username = String.valueOf(textInputEditTextUsername.getText());
                password = String.valueOf(textInputEditTextPassword.getText());
                email = String.valueOf(textInputEditTextEmail.getText());

                if (!fullname.equals("") && !username.equals("") && !password.equals("") && !email.equals("")) {
                    if (isValidPassword(password)){
                        progressBar.setVisibility(View.VISIBLE);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Starting Write and Read data with URL
                                //Creating array for parameters
                                String[] field = new String[4];
                                field[0] = "fullname";
                                field[1] = "username";
                                field[2] = "password";
                                field[3] = "email";
                                //Creating array for data
                                String[] data = new String[4];
                                data[0] = fullname;
                                data[1] = username;
                                data[2] = password;
                                data[3] = email;
                                PutData putData = new PutData("http://192.168.1.78/LoginRegister/signup.php", "POST", field, data);
                                if (putData.startPut()) {
                                    if (putData.onComplete()) {
                                        progressBar.setVisibility(View.GONE);
                                        String result = putData.getResult();
                                        if (result.equals("Sign Up Success")){
                                            Toast.makeText(SignUp.this, result, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), Login.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Toast.makeText(SignUp.this, result, Toast.LENGTH_SHORT).show();
                                        }
                                        Log.i("PutData", result);
                                    }
                                }
                                //End Write and Read data with URL
                            }
                        });
                    }else {
                        Toast.makeText(SignUp.this, "Password must be at least 8 characters long and contain at least one symbol.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(SignUp.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        boolean hasSymbol = !password.matches("[A-Za-z0-9 ]*"); // Checks if password contains a character that is not a letter, digit, or space
        return hasSymbol;
    }
}
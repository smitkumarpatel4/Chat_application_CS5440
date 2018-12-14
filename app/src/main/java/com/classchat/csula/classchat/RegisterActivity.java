package com.classchat.csula.classchat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout usernameTextInputLayout;
    private TextInputLayout mEmailTextInputLayout;
    private TextInputLayout mPasswordTextInputLayout;
    private TextInputLayout mConfirmPasswordTextInputLayout;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameTextInputLayout = findViewById(R.id.username);
        mEmailTextInputLayout = findViewById(R.id.text_input_email);
        mPasswordTextInputLayout = findViewById(R.id.text_input_password);
        mConfirmPasswordTextInputLayout = findViewById(R.id.text_input_password_confirm);

        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create an account");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



//        Get hold of an instance of FirebaseAuth and Database Reference
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    private void attemptRegistration() {

        if(!validateUsername() |  !validateEmail()  | !validatePassword() | !validateConfirmPassword()) {
            return;
        } else {
            createFirebaseUser();
        }
    }

    private void createFirebaseUser(){

        String email = mEmailTextInputLayout.getEditText().getText().toString();
        String password = mPasswordTextInputLayout.getEditText().getText().toString();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful()){
                    showErrorDialog("Registration attempt failed");

                    //Toast.makeText(getApplicationContext(),"Registration attempt failed",Toast.LENGTH_SHORT).show();

                }else {
                    saveDisplayName();
//                    saveUser();

                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    private void saveDisplayName() {

        FirebaseUser user = mAuth.getCurrentUser();
        String displayName = usernameTextInputLayout.getEditText().getText().toString();

        if (user !=null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Personal Feed App", "User name updated.");
                            }
                        }
                    });

        }

    }


    // Validate Username
    private boolean validateUsername(){
        String firstNameInput = usernameTextInputLayout.getEditText().getText().toString().trim();

        if(firstNameInput.isEmpty()){
            usernameTextInputLayout.setError("Username cannot be empty");
            usernameTextInputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));



            return  false;
        } else {

            usernameTextInputLayout.setError(null);
            return true;

        }

    }

    // Validate Email
    private boolean validateEmail(){
        String emailInput = mEmailTextInputLayout.getEditText().getText().toString().trim();

        if(emailInput.isEmpty()){
            mEmailTextInputLayout.setError("Email cannot be empty");
            mEmailTextInputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));
            return  false;
        } else if(!emailInput.contains("@")){

            mEmailTextInputLayout.setError("Email is invalid");
            mEmailTextInputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));
            return  false;

        } else {

            mEmailTextInputLayout.setError(null);
            return true;

        }

    }

    //    Validate Password

    private boolean validatePassword(){
        String passwordInput = mPasswordTextInputLayout.getEditText().getText().toString().trim();

        if(passwordInput.isEmpty()){
            mPasswordTextInputLayout.setError("Password  cannot be empty");
            mPasswordTextInputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));

            return  false;
        } else if(passwordInput.length() < 8){

            mPasswordTextInputLayout.setError("Password cannot be at least than 8 characters");
            mPasswordTextInputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));

            return  false;


        } else {

            mPasswordTextInputLayout.setError(null);
            return true;

        }
    }


    //    Validate Confirm Password

    private boolean validateConfirmPassword(){

        String passwordInput = mPasswordTextInputLayout.getEditText().getText().toString().trim();
        String confirmPasswordInput = mConfirmPasswordTextInputLayout.getEditText().getText().toString().trim();

        if(confirmPasswordInput.isEmpty() ||!confirmPasswordInput.equals(passwordInput)){
            mConfirmPasswordTextInputLayout.setError("Confirm Password not the same as Password");
            mConfirmPasswordTextInputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));

            return  false;
        } else {

            mConfirmPasswordTextInputLayout.setError(null);
            return true;

        }
    }


    // Alert Dialog if Registration fails

    private void showErrorDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Executed when Sign Up button is pressed.
    public void signUp(View v) {
        attemptRegistration();
    }

}

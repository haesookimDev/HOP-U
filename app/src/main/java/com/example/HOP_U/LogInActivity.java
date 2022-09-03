package com.example.HOP_U;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.example.HOP_U.viewModel.DatabaseViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

public class LogInActivity extends AppCompatActivity {

    Button signIn;
    EditText email;
    EditText password;
    TextView signUp;
    TextView findIdPw;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseViewModel databaseViewModel;
    private ChatUsersData chatUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        signIn = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUp);
        findIdPw = findViewById(R.id.findIdPw);
        email = findViewById(R.id.signInEmail);
        password = findViewById(R.id.signInPw);
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(DatabaseViewModel.class);

        mAuth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if ( email.getText().toString().length() == 0 ) {
                    Toast.makeText(LogInActivity.this, "Email 입력하세요", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if ( password.getText().toString().length() == 0 ) {
                    Toast.makeText(LogInActivity.this, "Password 입력하세요", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }

                loginUser();

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
    }

    private void updateUI(FirebaseUser user) {
        if( user!= null){
            user.reload();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            //getUserStatus(user.getUid());
            finish();
        }
    }

    private void getUserStatus(String UID){
        databaseViewModel.fetchUserStatus(UID);
        databaseViewModel.fetchUserStatus.observe(LogInActivity.this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                chatUserData = dataSnapshot.getValue(ChatUsersData.class);
                if(chatUserData.getStatus().equals("online")){
                    Toast.makeText(getApplicationContext(), "이미 로그인된 계정입니다.", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                } else{
                    setUserStatus(UID);
                }
            }
        });
    }

    private void setUserStatus(String UID) {
        databaseViewModel.addStatusInDatabase("status", "online", UID);
        databaseViewModel.successAddStatusInDatabase.observe(LogInActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

            }
        });
    }

    private void loginUser(){
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            user = mAuth.getCurrentUser();

                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
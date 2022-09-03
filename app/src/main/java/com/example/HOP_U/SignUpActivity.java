package com.example.HOP_U;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.HOP_U.viewModel.DatabaseViewModel;
import com.example.HOP_U.viewModel.SignInViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseUser user;

    private FirebaseAuth mAuth;

    private EditText signUpEmail;
    private EditText signUpPw;
    private EditText signUpPwRe;
    private EditText signUpName;
    private Spinner signUpMbti;
    private Spinner signUpJob;

    private String userId;
    private String imageUrl;
    private String timeStamp;

    private DatabaseViewModel databaseViewModel;
    private SignInViewModel signInViewModel;

    private Context context;

    private Boolean verify;
    private Boolean verified;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;


    private Button signUp;
    private Button emailVerify;

    private Pattern pattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        emailVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pattern.matcher(signUpEmail.getText().toString()).matches()){
                    Log.d("test", String.valueOf(pattern.matcher(signUpEmail.getText().toString()).matches()));
                    db.collection("users")
                            .whereEqualTo("email", signUpEmail.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            if (Objects.equals(document.getData().get("email"), signUpEmail.getText().toString())){
                                                verify = false;
                                                Toast.makeText(SignUpActivity.this, "이미 아이디가 있습니다.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        if (verify) {
                                            Toast.makeText(SignUpActivity.this, "사용 가능한 아이디 입니다.",
                                                    Toast.LENGTH_SHORT).show();
                                            verified = true;
                                        }
                                    }
                                }
                            });
                } else{
                    Toast.makeText(SignUpActivity.this, "이메일 형식이 아닙니다.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verified) {
                    if (Objects.equals(signUpPw.getText().toString(), signUpPwRe.getText().toString())) {
                        mAuth.createUserWithEmailAndPassword(signUpEmail.getText().toString().trim(), signUpPw.getText().toString().trim())
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            user = mAuth.getCurrentUser();

                                            final String uid = task.getResult().getUser().getUid();

                                            final String userName = signUpName.getText().toString().trim();
                                            final String job = signUpJob.getSelectedItem().toString();
                                            final String mbti = signUpMbti.getSelectedItem().toString();

                                            UserData userModel = new UserData();
                                            userModel.setUserName(userName);
                                            userModel.setUid(uid);
                                            userModel.setMbti(mbti);
                                            userModel.setJob(job);
                                            userModel.setDesiredJob(job);
                                            userModel.setPoint(0);
                                            userModel.setBanCount(0);
                                            userModel.setEmail(signUpEmail.getText().toString());

                                            db.collection("users").document(uid).set(userModel);
                                            getUserSession();
                                            addUserInDatabase(userName, signUpEmail.getText().toString(), uid);
                                            updateUI(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(SignUpActivity.this, "비밀번호가 다릅니다. 비밀번호를 확인해주세요",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SignUpActivity.this, "이메일 중복확인을 해주세요.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


        final Spinner spinner_mbti = findViewById(R.id.signUpMBTI);
        final Spinner spinner_job = findViewById(R.id.signUpJob);



        //1번에서 생성한 field.xml의 item을 String 배열로 가져오기

        String[] strMbti = getResources().getStringArray(R.array.mbti);
        String[] strJob = getResources().getStringArray(R.array.job);

        //2번에서 생성한 spinner_item.xml과 str을 인자로 어댑터 생성.

        final ArrayAdapter<String> adapterMbti= new ArrayAdapter<String>(this,R.layout.sign_up_spinner_item,strMbti);
        final ArrayAdapter<String> adapterJob= new ArrayAdapter<String>(this,R.layout.sign_up_spinner_item,strJob);

        adapterMbti.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterJob.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner_mbti.setAdapter(adapterMbti);
        spinner_job.setAdapter(adapterJob);

        //spinner 이벤트 리스너

        spinner_mbti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });
        spinner_job.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI(FirebaseUser user) {
        if( user!= null){
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void getUserSession() {
        signInViewModel.getUserFirebaseSession();
        signInViewModel.userFirebaseSession.observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                currentUser = firebaseUser;
            }
        });

    }

    private void addUserInDatabase(String userName, String email, String idUser) {
        Timestamp tsLong = Timestamp.now();
        timeStamp = tsLong.toString();
        imageUrl = "default";
        userId = idUser;
        databaseViewModel.addUserDatabase(userId, userName, email, timeStamp, imageUrl);
        databaseViewModel.successAddUserDb.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(context, "ERROR WHILE ADDING DATA IN DATABASE.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        context = SignUpActivity.this;
        signInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(SignInViewModel.class);
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(DatabaseViewModel.class);

        db = FirebaseFirestore.getInstance();

        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPw = findViewById(R.id.signUpPw);
        signUpPwRe = findViewById(R.id.signUpPwRe);
        signUpName = findViewById(R.id.signUpName);
        signUp = findViewById(R.id.signUpButton);
        emailVerify = findViewById(R.id.emailVerifyButton);
        signUpJob = findViewById(R.id.signUpJob);
        signUpMbti = findViewById(R.id.signUpMBTI);
        pattern = Patterns.EMAIL_ADDRESS;

        mAuth = FirebaseAuth.getInstance();
        verify = true;
        verified = false;
    }
}
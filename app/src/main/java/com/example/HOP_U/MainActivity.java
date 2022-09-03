package com.example.HOP_U;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Path;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.HOP_U.viewModel.DatabaseViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    public UserData userData;
    private Bundle bundle;
    private MyPageFragment myPageFragment;
    private MainHomeFragment mainHomeFragment;
    private ExperienceFragment experienceFragment;




    private DocumentReference docRef;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;


    public static Context context_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context_main = this;

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        userData = new UserData();
        myPageFragment = new MyPageFragment();
        mainHomeFragment = new MainHomeFragment();
        experienceFragment = new ExperienceFragment();

        bottomNavigationView = findViewById(R.id.navigation);

        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, mainHomeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.menu_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, mainHomeFragment).commit();
                        break;
                    case R.id.menu_consulting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new ConsultingFragment()).commit();
                        break;
                    case R.id.menu_experience:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, experienceFragment).commit();
                        break;
                    case R.id.menu_news:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new NewsFragment()).commit();
                        break;
                    case R.id.menu_personal:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, myPageFragment).commit();
                        break;
                }
                return true;
            }
        });


    }


    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            currentUser.reload();

            loadUserData();

        } else {
            Toast.makeText(getApplicationContext(), "로그인을 하셔야 접속할 수 있습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
            finish();
        }


    }

    private void loadUserData() {

        bundle = new Bundle();
        bundle.putString("logInOut", "로그아웃");

        myPageFragment.setArguments(bundle);

        docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> data = document.getData();
                        userData.setUserName(data.get("userName").toString());
                        userData.setUid(data.get("uid").toString());
                        userData.setMbti(data.get("mbti").toString());
                        userData.setPoint(Integer.parseInt(data.get("point").toString()));
                        userData.setJob(data.get("job").toString());
                        userData.setBanCount(Integer.parseInt(data.get("banCount").toString()));

                        if(userData.getJob().equals("또래 상담사") && userData.getUid().equals(currentUser.getUid())){

                            final Map<String, Object> match = new HashMap<>();

                            match.put(userData.getUid(), true);

                            db.collection("users")
                                    .document(userData.getUid())
                                    .collection("matching")
                                    .document(userData.getUid()).set(match);
                        }
                        if(userData.getBanCount() >= 10){
                            mAuth.signOut();
                            Toast.makeText(getApplicationContext(), "차단된 사용자입니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



    }

    public boolean isNetworkConnected() throws InterruptedException, IOException {   //check internet connectivity
        final String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }






}
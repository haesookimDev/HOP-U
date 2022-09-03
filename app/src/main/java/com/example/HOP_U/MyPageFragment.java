package com.example.HOP_U;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.HOP_U.viewModel.DatabaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.auth.User;

public class MyPageFragment extends Fragment {

    private TextView myMbti;
    private TextView myName;
    private TextView myPoint;
    private TextView myLavel;

    private ImageView pointGraph;
    private ImageView character;

    private Button loginOut;

    private Bundle bundle;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private UserData userData;
    private DatabaseViewModel databaseViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_my_page, container, false);

        userData = ((MainActivity)MainActivity.context_main).userData;

        bundle = getArguments();

        myLavel = view.findViewById(R.id.character_level);
        myName = view.findViewById(R.id.my_name);
        myMbti = view.findViewById(R.id.my_mbti);
        myPoint = view.findViewById(R.id.character_point);
        loginOut = view.findViewById(R.id.login_out);
        character = view.findViewById(R.id.character_image);
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(DatabaseViewModel.class);

        pointGraph = view.findViewById(R.id.pointGraph);

        user = mAuth.getCurrentUser();

        final String name = userData.getUserName();
        final int point = userData.getPoint();
        final String mbti = userData.getMbti();
        final String logInOut = bundle.getString("logInOut");

        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) pointGraph.getLayoutParams();
        final int Level1standard = 10;
        final int Level2standard = 20;
        final int Level3standard = 30;
        final int Level4standard = 40;
        final int Level5standard = 50;
        final int Level6standard = 60;
        final int Level7standard = 70;
        final int Level8standard = 80;
        int totalPoint;
        String level = "알 1";

        if (userData.getPoint() >= 0 & userData.getPoint() <= Level1standard){
            totalPoint = Level1standard - 0;

            final Double pointRatio = (Double.valueOf(userData.getPoint())/totalPoint);

            params.weight  = (float) (Math.round(pointRatio*10)/1.0);
            pointGraph.setLayoutParams(params);
            level = "알 1";

            character.setImageResource(R.drawable.level_default);


        } else if (userData.getPoint() > Level1standard & userData.getPoint() <= Level2standard){
            totalPoint = Level2standard - Level1standard;
            final Double pointRatio = (Double.valueOf(userData.getPoint()-Level1standard)/totalPoint);

            params.weight  = (float) (Math.round(pointRatio*10)/1.0);
            pointGraph.setLayoutParams(params);
            level = "탄생 2";
            character.setImageResource(R.drawable.level2);

        } else if (userData.getPoint() > Level2standard & userData.getPoint() <= Level3standard){
            totalPoint = Level3standard - Level2standard;
            final Double pointRatio = (Double.valueOf(userData.getPoint()-Level2standard)/totalPoint);

            params.weight  = (float) (Math.round(pointRatio*10)/1.0);
            pointGraph.setLayoutParams(params);
            level = "아기 3";
            character.setImageResource(R.drawable.level3);

        } else if (userData.getPoint() > Level3standard & userData.getPoint() <= Level4standard){
            totalPoint = Level4standard - Level3standard;
            final Double pointRatio = (Double.valueOf(userData.getPoint()-Level3standard)/totalPoint);

            params.weight  = (float) (Math.round(pointRatio*10)/1.0);
            pointGraph.setLayoutParams(params);
            level = "어린이 4";
            character.setImageResource(R.drawable.level4);

        } else if (userData.getPoint() > Level4standard & userData.getPoint() <= Level5standard){
            totalPoint = Level5standard - Level4standard;
            final Double pointRatio = (Double.valueOf(userData.getPoint()-Level4standard)/totalPoint);

            params.weight  = (float) (Math.round(pointRatio*10)/1.0);
            pointGraph.setLayoutParams(params);
            level = "청소년 5";
            character.setImageResource(R.drawable.level5);
        } else if (userData.getPoint() > Level5standard & userData.getPoint() <= Level6standard){
            totalPoint = Level6standard - Level5standard;
            final Double pointRatio = (Double.valueOf(userData.getPoint()-Level5standard)/totalPoint);

            params.weight  = (float) (Math.round(pointRatio*10)/1.0);
            pointGraph.setLayoutParams(params);
            level = "청년 6";
            character.setImageResource(R.drawable.level6);
        } else if (userData.getPoint() > Level6standard & userData.getPoint() <= Level7standard){
            totalPoint = Level7standard - Level6standard;
            final Double pointRatio = (Double.valueOf(userData.getPoint()-Level6standard)/totalPoint);

            params.weight  = (float) (Math.round(pointRatio*10)/1.0);
            pointGraph.setLayoutParams(params);
            level = "성인 7";
            character.setImageResource(R.drawable.level7);
        } else if (userData.getPoint() > Level7standard & userData.getPoint() <= Level8standard){
            totalPoint = Level8standard - Level7standard;
            Double pointRatio = (Double.valueOf(userData.getPoint()-Level7standard)/totalPoint);
            if (pointRatio > 10){
                pointRatio = 10.0;
            }

            params.weight  = (float) (Math.round(pointRatio*10)/1.0);
            pointGraph.setLayoutParams(params);
            level = "가디언 8";
            character.setImageResource(R.drawable.level8);
        }


        myLavel.setText(level+"단계");
        myName.setText(name+ " 님");
        myPoint.setText(point+"P");
        myMbti.setText(mbti);
        loginOut.setText(logInOut);

        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                setUserStatus(user.getUid());
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                loginOut.setText("로그인");
                removeFragment(MyPageFragment.this);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private void removeFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(fragment).commit();
            fragmentManager.popBackStack();
        }
    }

    private void setUserStatus(String UID) {
        databaseViewModel.addStatusInDatabase("status", "offline", UID);
        databaseViewModel.successAddStatusInDatabase.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

            }
        });
    }
}
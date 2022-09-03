package com.example.HOP_U;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class QuizGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_game);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 프래그먼트매니저를 통해 사용
        Quiz_Fragment1 quizFragment1 = new Quiz_Fragment1(); // 객체 생성
        transaction.replace(R.id.quiz_frameLayout, quizFragment1); //layout, 교체될 layout
        transaction.commit(); //commit으로 저장 하지 않으면 화면 전환이 되지 않음
    }
}
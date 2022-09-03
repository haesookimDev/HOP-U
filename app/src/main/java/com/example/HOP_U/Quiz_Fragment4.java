package com.example.HOP_U;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Quiz_Fragment4 extends Fragment{

    private View view;
    private Button btn_frag1_o;
    private Button btn_frag1_x;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_quiz_4, container, false);

        btn_frag1_o = view.findViewById(R.id.btn_frag1_o);
        btn_frag1_x = view.findViewById(R.id.btn_frag1_x);
        //fragment에서는 그냥 findViewById로 Button id를 가져올 수 없음.
        //인플레이터된 view를 사용하여 가져옴.
        btn_frag1_o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Quiz_Commentary_Fragment4 quiz_solution_Fragment4 = new Quiz_Commentary_Fragment4();
                transaction.replace(R.id.quiz_frameLayout, quiz_solution_Fragment4);
                transaction.commit();
            }
        });

        btn_frag1_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Quiz_Commentary_Fragment4 quiz_solution_Fragment4 = new Quiz_Commentary_Fragment4();
                transaction.replace(R.id.quiz_frameLayout, quiz_solution_Fragment4);
                transaction.commit();
            }
        });

        return view;
    }
}
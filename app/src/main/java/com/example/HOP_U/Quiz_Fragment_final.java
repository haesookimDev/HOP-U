package com.example.HOP_U;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Quiz_Fragment_final extends Fragment {

    private View view;
    private Button btn_next_frag;
    private TextView pointAcc;

    private FirebaseFirestore db;
    private UserData userData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_final_quiz, container, false);

        db = FirebaseFirestore.getInstance();
        userData = ((MainActivity)MainActivity.context_main).userData;

        pointAcc = view.findViewById(R.id.pointAcc);

        db.collection("quiz")
                .document(userData.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    pointAcc.setText("이미 적립하셨습니다.");
                } else{
                    final Map<String, Object> quiz = new HashMap<>();

                    quiz.put("quiz", true);

                    db.collection("quiz")
                            .document(userData.getUid()).set(quiz);

                    db.collection("users")
                            .document(userData.getUid())
                            .update("point", userData.getPoint()+5);



                    userData.setPoint(userData.getPoint()+5);
                }
            }
        });

        btn_next_frag = view.findViewById(R.id.btn_next_frag);
        btn_next_frag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //프레그먼트에서 액티비티로 화면 전환
                removeFragment(Quiz_Fragment_final.this);
                getActivity().supportFinishAfterTransition();
            }
        });

        return view;
    }

    private void removeFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(fragment).commit();
            fragmentManager.popBackStack();
        }
    }


}


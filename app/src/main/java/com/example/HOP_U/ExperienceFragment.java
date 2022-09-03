package com.example.HOP_U;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ExperienceFragment extends Fragment {

    private LinearLayout in_progress;
    private LinearLayout previous;
    private LinearLayout quiz_level_1;
    private LinearLayout quiz_level_2;
    private LinearLayout quiz_level_3;
    private TextView progressTrialTitle;
    private TextView progressTrialTime;
    private ImageView addTrialButton;

    private FirebaseFirestore db;

    private String trialId;

    private List<Map<String, Object>> finalTrialList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_experience, container, false);
        db = FirebaseFirestore.getInstance();


        in_progress = view.findViewById(R.id.trial_in_progress);
        previous = view.findViewById(R.id.previous_trial);
        addTrialButton = view.findViewById(R.id.addTrialButton);

        quiz_level_1 = view.findViewById(R.id.level_1);
        quiz_level_2 = view.findViewById(R.id.level_2);
        quiz_level_3 = view.findViewById(R.id.level_3);

        progressTrialTime = view.findViewById(R.id.progressTrialTime);
        progressTrialTitle = view.findViewById(R.id.progressTrialTitle);

        try {
            loadMockTrial();
        } catch (ParseException e) {
            e.printStackTrace();
        }



        in_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trialId != null){
                    Intent intent = new Intent(getActivity(), TrialActivity.class);
                    intent.putExtra("tId",trialId);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "진행중인 재판이 없습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "종료된 재판입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        addTrialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTrialActivity.class);
                startActivity(intent);
            }
        });


        quiz_level_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), com.example.HOP_U.QuizGameActivity.class);
                startActivity(intent);
            }
        });
        quiz_level_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "quiz_level_2입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        quiz_level_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "quiz_level_3입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void loadMockTrial() throws ParseException {

        List<Map<String, Object>> trialList = null;
        trialList = new ArrayList<>();

        finalTrialList = trialList;

        final Date[] trialTime = new Date[1];

        SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat dateSec = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

        trialTime[0] = date.parse("2000.01.01");

        Timestamp timestamp = Timestamp.now();

        db.collection("mockTrial")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                data.put("tId", document.getId());


                                final String trialStartTime = date.format(Objects.requireNonNull(document.getDate("start")));
                                final String trialEndTime = date.format(Objects.requireNonNull(document.getDate("end")));

                                final String trialEndTimeCp = dateSec.format(Objects.requireNonNull(document.getDate("end")));
                                final String currentTime = dateSec.format(Objects.requireNonNull(timestamp.toDate()));

                                data.put("trialStartTime", trialStartTime);
                                data.put("trialEndTime", trialEndTime);
                                //data.get("post").equals(true)

                                if(true){
                                    Optional result1 = finalTrialList.stream().filter(x -> Objects.equals(x.get("tId"), document.getId())).findFirst();
                                    if (!result1.isPresent()) {
                                        finalTrialList.add(data);
                                    }
                                    try {
                                        if (trialTime[0].before(date.parse(trialEndTime))) {
                                            if (dateSec.parse(trialEndTimeCp).after(dateSec.parse(currentTime))) {
                                                trialTime[0] = date.parse(trialEndTime);

                                                progressTrialTitle.setText(data.get("title").toString());
                                                progressTrialTime.setText("기간 " +trialStartTime+ " ~ " +trialEndTime);
                                                trialId = data.get("tId").toString();
                                            }
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }


                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}
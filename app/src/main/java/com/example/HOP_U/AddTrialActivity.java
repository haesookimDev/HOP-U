package com.example.HOP_U;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTrialActivity extends AppCompatActivity {

    private EditText title;
    private EditText explanation;
    private Button addButton;
    private ImageView exit;
    private TextView tempTrial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trial);

        // Access a Cloud Firestore instance from your Activity
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.addTrialTitle);
        explanation = findViewById(R.id.addTrialEx);

        addButton = findViewById(R.id.addTrial);
        exit = findViewById(R.id.exitAddTrial);
        tempTrial = findViewById(R.id.tempTrial);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( title.getText().toString().length() == 0 ) {
                    Toast.makeText(AddTrialActivity.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
                    title.requestFocus();
                    return;
                }
                if ( explanation.getText().toString().length() == 0 ) {
                    Toast.makeText(AddTrialActivity.this, "설명을 입력하세요", Toast.LENGTH_SHORT).show();
                    explanation.requestFocus();
                    return;
                }

                Calendar rightNow = Calendar.getInstance();
                rightNow.add(Calendar.MONTH, 1);

                Timestamp now = Timestamp.now();
                Timestamp end = new Timestamp(rightNow.getTime());

                // Create a new user with a first and last name
                Map<String, Object> trial = new HashMap<>();
                trial.put("title", title.getText().toString());
                trial.put("explanation", explanation.getText().toString());
                trial.put("agreeCount", 0);
                trial.put("disagreeCount", 0);
                trial.put("time", now);
                trial.put("post", false);
                trial.put("start", now);
                trial.put("end", end);
                trial.put("totalCount", 0);


                db.collection("mockTrial")
                        .add(trial)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

                finish();
            }
        });



    }
}
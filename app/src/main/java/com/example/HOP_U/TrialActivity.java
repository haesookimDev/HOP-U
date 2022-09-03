package com.example.HOP_U;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TrialActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String tId;
    private TextView trialTitle;
    private TextView trialExplanation;
    private TextView agreeCount;
    private TextView disagreeCount;
    private TextView trialCommentEdit;
    private TextView trialCommentMore;
    private Button enterComment;
    private TextView trialTime;
    private Spinner trialSpinner;
    private String trialStartTime;
    private String trialEndTime;
    private String trialTotalCount;
    private TextView totalCount;
    private ImageView trialAgreeRatio;
    private ImageView trialDisagreeRatio;
    private TextView trialViewMore;
    private Double ag;
    private Double Dg;
    private int agree;
    private int disagree;
    private int total;

    UserData userData;

    private ImageView exit;

    private RecyclerView trialCommentRecyclerView = null;
    private trialCommentAdapter trialCommentAdapter = null;
    private ArrayList<trialCommentData> trialCommentListRecycler;
    private ArrayList<trialCommentData> trialCommentListRecyclerMore;
    private trialCommentRecyclerDeco trialCommentRecyclerDeco;

    private List<Map<String, Object>> finalTrialCommentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial);

        Intent intent = getIntent();
        tId = intent.getStringExtra("tId");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        trialTitle = findViewById(R.id.trialTitleText);
        trialExplanation = findViewById(R.id.trialExText);
        trialViewMore = findViewById(R.id.viewMore);
        trialCommentMore = findViewById(R.id.trialCommentMore);
        agreeCount = findViewById(R.id.agreeCount);
        disagreeCount = findViewById(R.id.disagreeCount);
        trialAgreeRatio = findViewById(R.id.trialAgreeRatio);
        trialDisagreeRatio = findViewById(R.id.trialDisagreeRatio);
        trialTime = findViewById(R.id.trialTime);
        enterComment = findViewById(R.id.enterComment);
        trialCommentEdit = findViewById(R.id.trialCommentEdit);
        trialSpinner = findViewById(R.id.spinnerTrial);
        totalCount = findViewById(R.id.totalCount);

        userData = ((MainActivity)MainActivity.context_main).userData;

        exit = findViewById(R.id.exitProgressTrial);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        trialCommentRecyclerView = findViewById(R.id.trialCommentRecycler);
        trialCommentListRecycler = new ArrayList<>();
        trialCommentListRecyclerMore = new ArrayList<>();
        trialCommentAdapter = new trialCommentAdapter(trialCommentListRecycler);
        trialCommentRecyclerView.setAdapter(trialCommentAdapter);
        trialCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));

        trialCommentRecyclerDeco = new trialCommentRecyclerDeco(this);
        trialCommentRecyclerView.addItemDecoration(trialCommentRecyclerDeco);

        loadMockTrial(tId);
        loadTrialComment(tId);

        List<Map<String, Object>> trialCommentList = null;
        trialCommentList = new ArrayList<>();

        finalTrialCommentList = trialCommentList;

        enterComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser==null){
                    Toast.makeText(getApplicationContext(), "로그인 해주세요", Toast.LENGTH_SHORT).show();
                } else{
                    db.collection("mockTrial")
                            .document(tId)
                            .collection("comments")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> data = document.getData();
                                            Optional result = finalTrialCommentList.stream().filter(x -> Objects.equals(x.get("uid"), currentUser.getUid())).findFirst();
                                            if (!result.isPresent()) {
                                                finalTrialCommentList.add(data);
                                            }
                                        }

                                        Optional result = finalTrialCommentList.stream().filter(x -> Objects.equals(x.get("uid"), currentUser.getUid())).findFirst();
                                        if (!result.isPresent()) {

                                            Date date = new Date();

                                            trialCommentData trialCommentData = new trialCommentData(currentUser.getUid(),
                                                    userData.userName,
                                                    trialCommentEdit.getText().toString(),
                                                    trialSpinner.getSelectedItem().toString(),
                                                    new Timestamp(date));

                                            db.collection("mockTrial")
                                                    .document(tId)
                                                    .collection("comments")
                                                    .document(currentUser.getUid()).set(trialCommentData);

                                            if (trialSpinner.getSelectedItem().equals("찬성")){
                                                db.collection("mockTrial")
                                                        .document(tId)
                                                        .update("agreeCount", agree+1);
                                            } else {
                                                db.collection("mockTrial")
                                                        .document(tId)
                                                        .update("disagreeCount", disagree+1);
                                            }
                                            db.collection("mockTrial")
                                                    .document(tId)
                                                    .update("totalCount", total+1);

                                            db.collection("users")
                                                    .document(userData.getUid())
                                                    .update("point", userData.getPoint()+5);

                                            loadMockTrial(tId);
                                            loadTrialComment(tId);
                                            userData.setPoint(userData.getPoint()+5);

                                        } else {
                                            Toast.makeText(getApplicationContext(), "이미 작성하셨습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        trialCommentEdit.setText("");
                                    } else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }

            }

        });

        trialCommentMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trialCommentAdapter = new trialCommentAdapter(trialCommentListRecyclerMore);
                trialCommentAdapter.notifyDataSetChanged();
                trialCommentRecyclerView.setAdapter(trialCommentAdapter);
                trialCommentMore.setVisibility(View.GONE);
            }
        });


        final Spinner spinner_field = findViewById(R.id.spinnerTrial);



        //1번에서 생성한 field.xml의 item을 String 배열로 가져오기

        String[] str = getResources().getStringArray(R.array.trialSpinner_color);


        //2번에서 생성한 spinner_item.xml과 str을 인자로 어댑터 생성.

        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,R.layout.trial_spinner_item,str);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner_field.setAdapter(adapter);

        //spinner 이벤트 리스너

        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().equals("찬성")){
                    ((TextView)adapterView.getChildAt(0)).setTextColor(Color.parseColor("#2D5BEF"));
                } else {
                    ((TextView)adapterView.getChildAt(0)).setTextColor(Color.parseColor("#FF5D49"));
                }

            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void addItem(String uid, String name, String comment, String prosAndCons, Timestamp timestamp) {
        trialCommentData item = new trialCommentData(uid, name, comment, prosAndCons, timestamp);
        trialCommentListRecycler.add(item);
    }

    private void addItemMore(String uid, String name, String comment, String prosAndCons, Timestamp timestamp) {
        trialCommentData item = new trialCommentData(uid, name, comment, prosAndCons, timestamp);
        trialCommentListRecyclerMore.add(item);
    }

    private void setViewMore(TextView contentTextView, TextView viewMoreTextView){
        contentTextView.setMaxLines(5);
        // getEllipsisCount()을 통한 더보기 표시 및 구현
        int lineCount = contentTextView.getLineCount();
        if (lineCount > 0) {
            if (contentTextView.getLayout().getEllipsisCount(contentTextView.getLineCount()-1)>0) {
                viewMoreTextView.setVisibility(View.VISIBLE);
            } else {
                viewMoreTextView.setVisibility(View.GONE);
            }
        }
        viewMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentTextView.setMaxLines(Integer.MAX_VALUE);
                viewMoreTextView.setVisibility(View.GONE);
            }
        });
    }

    private void loadMockTrial(String trialId) {

        SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd");

        final DocumentReference docRef = db.collection("mockTrial").document(trialId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        try {
                            if (data != null) {
                                trialStartTime = date.format(Objects.requireNonNull(document.getDate("start"))).toString();
                                trialEndTime = date.format(Objects.requireNonNull(document.getDate("end"))).toString();
                                trialTotalCount = document.getData().get("totalCount").toString();
                                data.put("trialStartTime", trialStartTime);
                                data.put("trialEndTime", trialEndTime);
                                data.put("trialTotalCount", trialTotalCount);
                                agree = Integer.parseInt(document.getData().get("agreeCount").toString());
                                disagree = Integer.parseInt(document.getData().get("disagreeCount").toString());
                                total = Integer.parseInt(document.getData().get("totalCount").toString());

                                try{
                                    ag = Double.valueOf((Double.valueOf(document.getData().get("agreeCount").toString()) / Double.valueOf(trialTotalCount)));
                                    Log.d("test", String.valueOf(ag));
                                    data.put("ratio", ag);
                                } catch (Exception e) {
                                    ag = 0.0000;
                                    data.put("ratio", ag);
                                }

                                try {
                                    Dg = Double.valueOf((Double.valueOf(document.getData().get("disagreeCount").toString()) / Double.valueOf(trialTotalCount)));
                                    data.put("disRatio", Dg);
                                } catch (Exception e){
                                    Dg = 0.0000;
                                    data.put("disRatio", Dg);
                                }

                                trialTime.setText("기간 [ "+data.get("trialStartTime").toString()+" ~ "+data.get("trialEndTime").toString()+" ]");
                                trialTitle.setText(Objects.requireNonNull(data.get("title")).toString());
                                trialExplanation.setText(data.get("explanation").toString());
                                totalCount.setText("현재 총 "+data.get("trialTotalCount").toString()+"표");
                            }
                            Double a = (Double) data.get("ratio");
                            Double da = (Double) data.get("disRatio");

                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) trialAgreeRatio.getLayoutParams();
                            params.weight = (float) (Math.round(a*100)/10.0);
                            trialAgreeRatio.setLayoutParams(params);

                            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) trialDisagreeRatio.getLayoutParams();
                            params2.weight = (float) (Math.round(da*100)/10.0);
                            trialDisagreeRatio.setLayoutParams(params2);

                            int agg = (int) ((Math.round(a*100)/10.0)*10);
                            int Dgg = (int) ((Math.round(da*100)/10.0)*10);

                            agreeCount.setText(agg+"%");
                            disagreeCount.setText(Dgg+"%");
                            trialExplanation.post(new Runnable() {
                                @Override
                                public void run() {
                                    setViewMore(trialExplanation, trialViewMore);
                                }
                            });

                        } catch (IndexOutOfBoundsException e){
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

    private void loadTrialComment(String trialId){
        try {
            trialCommentListRecycler.clear();
            db.collection("mockTrial")
                    .document(trialId)
                    .collection("comments")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    if (trialCommentListRecycler.size() < 4) {
                                        addItem(data.get("uid").toString(),
                                                data.get("name").toString(),
                                                data.get("comment").toString(),
                                                data.get("prosAndCons").toString(),
                                                (Timestamp) data.get("timestamp"));
                                    }
                                    addItemMore(data.get("uid").toString(),
                                            data.get("name").toString(),
                                            data.get("comment").toString(),
                                            data.get("prosAndCons").toString(),
                                            (Timestamp) data.get("timestamp"));

                                }
                                trialCommentAdapter.notifyDataSetChanged();
                                if (trialCommentListRecyclerMore.size() > 4 ){
                                    trialCommentMore.setVisibility(View.VISIBLE);
                                }else {
                                    trialCommentMore.setVisibility(View.GONE);
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }catch (IndexOutOfBoundsException e){
        }
    }
}
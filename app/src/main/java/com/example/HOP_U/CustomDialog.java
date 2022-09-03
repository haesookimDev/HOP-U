package com.example.HOP_U;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CustomDialog {

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private RecyclerView dialogRecyclerView = null;
    private DialogAdapter dialogAdapter = null;
    private ArrayList<DialogData> dialogListRecycler;

    public CustomDialog(Context context)
    {
        this.context = context;
    }

    public void callDialog()
    {
        final Dialog dialog = new Dialog(context);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        //아래 2줄 다이얼로그 배경 커스텀 설정하는 코드
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();

        final Button phone_num_insert = (Button) dialog.findViewById(R.id.phone_num_insert);
        final ImageButton dial_exit = (ImageButton) dialog.findViewById(R.id.exitAddTrial);

        dialogRecyclerView = dialog.findViewById(R.id.dial_recyclerView);
        dialogListRecycler = new ArrayList<>();
        dialogAdapter = new DialogAdapter(dialogListRecycler);
        dialogRecyclerView.setAdapter(dialogAdapter);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(dialog.getContext(), RecyclerView.VERTICAL, true));

        loadDialog(user.getUid());

        // 다이얼로그 나가기
        dial_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });

        // 번호 추가하기 버튼
        phone_num_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {   //(!!!!!번호 추가하는 페이지로 전환하기 수정해야함!!!)
                (MainActivity.context_main).startActivity(new Intent(view.getContext(), AddDialNumberActivity.class));
                dialog.dismiss();
            }
        });

    }

    private void addItem(String number, String name, String memo) {
        DialogData item = new DialogData(number, name, memo);
        dialogListRecycler.add(item);
    }


    private void loadDialog(String uid){
        db.collection("users")
                .document(uid)
                .collection("dialog")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    addItem(data.get("number").toString(),
                            data.get("name").toString(),
                            data.get("memo").toString());

                }
                dialogAdapter.notifyDataSetChanged();
            }
        });
    }

}

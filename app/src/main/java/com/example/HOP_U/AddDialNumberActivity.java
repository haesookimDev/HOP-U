package com.example.HOP_U;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddDialNumberActivity extends AppCompatActivity {

    private Button addButton;
    private ImageView exit;

    private EditText name;
    private EditText number;
    private EditText memo;
    private DialogData dialogData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dial_number);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        addButton = findViewById(R.id.addDial);
        exit = findViewById(R.id.exitAddDial);

        name = findViewById(R.id.editNumberName);
        number = findViewById(R.id.editDialNumber);
        memo = findViewById(R.id.editNumberMemo);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( name.getText().toString().length() == 0 ) {
                    Toast.makeText(AddDialNumberActivity.this, "번호이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                    return;
                }
                if ( number.getText().toString().length() == 0 ) {
                    Toast.makeText(AddDialNumberActivity.this, "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    number.requestFocus();
                    return;
                }

                dialogData = new DialogData(number.getText().toString(), name.getText().toString(), memo.getText().toString());

                db.collection("users").document(user.getUid()).collection("dialog").add(dialogData);
                finish();
            }
        });


    }
}
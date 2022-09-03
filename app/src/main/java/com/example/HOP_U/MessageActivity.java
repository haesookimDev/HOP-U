package com.example.HOP_U;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.HOP_U.notifications.Client;
import com.example.HOP_U.notifications.MyResponse;
import com.example.HOP_U.notifications.Sender;
import com.example.HOP_U.notifications.Token;
import com.example.HOP_U.viewModel.DatabaseViewModel;
import com.example.HOP_U.viewModel.LogInViewModel;
import com.example.HOP_U.notifications.Data;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    LogInViewModel logInViewModel;
    DatabaseViewModel databaseViewModel;

    CircleImageView iv_profile_image;
    TextView tv_profile_user_name;
    ImageView iv_back_button;
    ImageView iv_user_status_message_view;
    private ChatList chats;

    String profileUserNAme;
    String profileImageURL;
    String bio;
    FirebaseUser currentFirebaseUser;

    EditText et_chat;
    ImageView btn_sendIv;
    private Button ban;
    private FirebaseFirestore db;

    String chat;
    String timeStamp;
    String userId_receiver; // userId of other user who'll receive the text // Or the user id of profile currently opened
    String userId_sender;  // current user id
    String user_status;
    MessageAdapter messageAdapter;
    ArrayList<Chats> chatsArrayList;
    RecyclerView recyclerView;
    Context context;

    private UserData user;

    APIService apiService;
    boolean notify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        init();
        getCurrentFirebaseUser();
        fetchAndSaveCurrentProfileTextAndData();
        getBan();

        btn_sendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;

                chat = et_chat.getText().toString().trim();
                if (!chat.equals("")) {
                    addChatInDataBase();
                } else {
                    Toast.makeText(MessageActivity.this, "Message can't be empty.", Toast.LENGTH_SHORT).show();
                }
                et_chat.setText("");
                final Map<String, Object> match = new HashMap<>();

                match.put(getIntent().getStringExtra("matchingUID"), true);

                db.collection("users")
                        .document(currentFirebaseUser.getUid())
                        .collection("matching")
                        .document(getIntent().getStringExtra("matchingUID")).set(match);

            }
        });

        ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBanChat();
                et_chat.setEnabled(false);
                btn_sendIv.setEnabled(false);
                Toast.makeText(MessageActivity.this, "차단했습니다. 이후로 채팅을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
    }


    private void getCurrentFirebaseUser() {
        logInViewModel.getFirebaseUserLogInStatus();
        logInViewModel.firebaseUserLoginStatus.observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                currentFirebaseUser = firebaseUser;
                userId_sender = currentFirebaseUser.getUid();
            }
        });
    }

    private void fetchAndSaveCurrentProfileTextAndData() {
        if(userId_receiver == null){
            userId_receiver= getIntent().getStringExtra("matchingUID");
        }
        databaseViewModel.fetchSelectedUserProfileData(userId_receiver);
        databaseViewModel.fetchSelectedProfileUserData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                ChatUsersData user = dataSnapshot.getValue(ChatUsersData.class);

                assert user != null;
                profileUserNAme = user.getUsername();
                profileImageURL = user.getImageUrl();
                bio = user.getBio();
                user_status = user.getStatus();

                fetchChatFromDatabase(userId_receiver, userId_sender);
            }
        });

        addIsSeen();
    }

    private void fetchChatFromDatabase(String myId, String senderId) {
        databaseViewModel.fetchChatUser();
        databaseViewModel.fetchedChat.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                chatsArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if (chats.getReceiverId().equals(senderId) && chats.getSenderId().equals(myId) || chats.getReceiverId().equals(myId) && chats.getSenderId().equals(senderId)) {
                        chatsArrayList.add(chats);
                    }

                    messageAdapter = new MessageAdapter(chatsArrayList, context, userId_sender);
                    recyclerView.setAdapter(messageAdapter);
                }
            }
        });
    }

    public void updateBanChat(){
        databaseViewModel.getChaListUserDataSnapshot(currentFirebaseUser.getUid());
        databaseViewModel.getChaListUserDataSnapshot.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatList chats = dataSnapshot1.getValue(ChatList.class);
                    assert chats != null;
                    if (chats.getId().equals(userId_receiver)) {
                        databaseViewModel.addBanUser(dataSnapshot1);
                    }
                }
            }
        });
        databaseViewModel.getChaListUserDataSnapshot(userId_receiver);
        databaseViewModel.getChaListUserDataSnapshot.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                    ChatList chats = dataSnapshot2.getValue(ChatList.class);
                    assert chats != null;
                    if (chats.getId().equals(currentFirebaseUser.getUid())) {
                        databaseViewModel.addBanUser(dataSnapshot2);
                    }
                }
            }
        });

        final Map<String, Object> ban = new HashMap<>();

        ban.put("ban", true);

        db.collection("users")
                .document(userId_receiver)
                .collection("ban")
                .document(userId_sender).set(ban);

        db.collection("users")
                .document(userId_receiver)
                .update("banCount",user.getBanCount()+1);

    }

    public void getBan() {
        databaseViewModel.getChaListUserDataSnapshot(userId_sender);
        databaseViewModel.getChaListUserDataSnapshot.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    chats = dataSnapshot1.getValue(ChatList.class);
                    assert chats != null;
                    try {
                        if (chats.getId().equals(userId_receiver) & chats.getBan()) {
                            et_chat.setEnabled(false);
                            btn_sendIv.setEnabled(false);
                            Toast.makeText(MessageActivity.this, "차단된 채팅입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }catch (NullPointerException e){

                    }

                }

            }
        });
    }


    public void updateRecentChat(String chat){
        databaseViewModel.fetchRecentChat(currentFirebaseUser.getUid());
        databaseViewModel.fetchRecentChat.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    RecentChat chats = dataSnapshot1.getValue(RecentChat.class);
                    assert chats != null;
                    if (chats.getId().equals(userId_receiver)) {
                        databaseViewModel.addRecentChatInDatabase(chat, dataSnapshot1);
                    }
                }
            }
        });
        databaseViewModel.fetchRecentChat(userId_receiver);
        databaseViewModel.fetchRecentChat.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                    RecentChat chats = dataSnapshot2.getValue(RecentChat.class);
                    assert chats != null;
                    if (chats.getId().equals(currentFirebaseUser.getUid())) {
                        databaseViewModel.addRecentChatInDatabase(chat, dataSnapshot2);
                    }
                }
            }
        });
    }

    public void addIsSeen() {
        String isSeen = "seen";
        databaseViewModel.fetchChatUser();
        databaseViewModel.fetchedChat.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Chats chats = dataSnapshot1.getValue(Chats.class);
                    assert chats != null;
                    if (chats.getSenderId().equals(userId_receiver) && chats.getReceiverId().equals(userId_sender)) {
                        databaseViewModel.addIsSeenInDatabase(isSeen, dataSnapshot1);
                    }
                }

            }
        });

    }

    private void sendNotification(String userId_receiver, String username, String msg) {
        databaseViewModel.getTokenDatabaseRef();
        databaseViewModel.getTokenRefDb.observe(this, new Observer<DatabaseReference>() {
            @Override
            public void onChanged(DatabaseReference databaseReference) {
                Query query = databaseReference.orderByKey().equalTo(userId_receiver);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Token token = snapshot.getValue(Token.class);
                            Data data = new Data(userId_sender, String.valueOf(R.drawable.ic_baseline_textsms_24), username + ": " + msg, "New Message", userId_receiver);

                            assert token != null;
                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                assert response.body() != null;
                                                if (response.body().success != 1) {

                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    private void addChatInDataBase() {
        long date = new Date().getTime();
                //((MainActivity)MainActivity.context_main).getCurrentNetworkTime();

        timeStamp = Long.toString(date);
        databaseViewModel.addChatDb(userId_receiver, userId_sender, chat, timeStamp);
        databaseViewModel.successAddChatDb.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    // Toast.makeText(MessageActivity.this, "Sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MessageActivity.this, "Message can't be sent.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final String msg = chat;
        databaseViewModel.fetchingUserDataCurrent();
        databaseViewModel.fetchUserCurrentData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                ChatUsersData users = dataSnapshot.getValue(ChatUsersData.class);
                assert users != null;
                if (notify) {
                    sendNotification(userId_receiver, users.getUsername(), msg);

                }
                notify = false;
            }
        });
    }


    private void init() {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(DatabaseViewModel.class);
        logInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(LogInViewModel.class);
        context = MessageActivity.this;

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        iv_back_button = findViewById(R.id.backChatting);

        iv_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_chat = findViewById(R.id.et_chat);
        btn_sendIv = findViewById(R.id.iv_send_button);
        ban = findViewById(R.id.chat_ban);
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler_view_messages_record);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        user = ((MainActivity)MainActivity.context_main).userData;

        chatsArrayList = new ArrayList<>();


    }
}
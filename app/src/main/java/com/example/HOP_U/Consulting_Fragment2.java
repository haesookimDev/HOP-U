package com.example.HOP_U;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.HOP_U.viewModel.DatabaseViewModel;
import com.example.HOP_U.viewModel.LogInViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class Consulting_Fragment2 extends Fragment {

    private Button chat_application;
    private MainActivity mainActivity;
    private Context context;
    private UserFragmentAdapter userAdapter;
    private ArrayList<ChatUsersData> mUsers;
    private ArrayList<RecentChat> recentChat;
    private String currentUserId;
    private ArrayList<ChatList> userList;  //list of all other users with chat record
    private DatabaseViewModel databaseViewModel;
    private LogInViewModel logInViewModel;
    private RecyclerView recyclerView_chat_fragment;
    private String matchingUID = null;
    private FirebaseFirestore db;
    private UserData userData;
    private boolean initialize;

    private ArrayList<String> finalMatchUserList;

    public Consulting_Fragment2(Context context){this.context = context;}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_consulting_2,container,false);

        init(v);
        fetchAllChat();
        setUserAdapter();
        getTokens();
        matchingUser();

        chat_application = v.findViewById(R.id.chatting_application);
        chat_application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matching();
            }
        });

        return v;
    }

    private void matchingUser(){
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> data = document.getData();
                        if (data.get("uid").equals(currentUserId)){
                            continue;
                        }
                        if(data.get("mbti").equals(userData.getMbti())){
                            if(data.get("job").equals("또래 상담사")){
                                try {
                                    db.collection("users")
                                            .document(currentUserId)
                                            .collection("matching")
                                            .document(data.get("uid").toString())
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (!document.exists()){
                                                    Optional result = finalMatchUserList.stream().filter(x -> Objects.equals(x, data.get("uid"))).findFirst();
                                                    if (!result.isPresent()){
                                                        finalMatchUserList.add(data.get("uid").toString());
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }catch (Exception e){
                                    Optional result = finalMatchUserList.stream().filter(x -> Objects.equals(x, data.get("uid"))).findFirst();
                                    if (!result.isPresent()){
                                        finalMatchUserList.add(data.get("uid").toString());
                                    }
                                }
                            }
                        }

                    }
                }
            }

        });

    }

    private void matching(){
        if (finalMatchUserList.size()>0){
            int index = 0;
            final Random random = new Random();

            index = random.nextInt(finalMatchUserList.size());

            matchingUID = finalMatchUserList.get(index);
            finalMatchUserList.clear();

            Intent intent = new Intent(getActivity(), MessageActivity.class);
            intent.putExtra("matchingUID", matchingUID);

            matchingUID = null;
            startActivity(intent);
        }else{
            Toast.makeText(getActivity(), "또래 상담사가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getTokens() {

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    updateToken(idToken);
                }
            }
        });
    }

    private void fetchRecentChat() {
        databaseViewModel.fetchRecentChat(currentUserId);
        databaseViewModel.fetchRecentChat.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    RecentChat chatList = dataSnapshot1.getValue(RecentChat.class);
                    recentChat.add(chatList);
                }
            }
        });
    }

    private void fetchAllChat() {
        databaseViewModel.fetchingUserDataCurrent();
        databaseViewModel.fetchUserCurrentData.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                ChatUsersData users = dataSnapshot.getValue(ChatUsersData.class);
                assert users != null;
                currentUserId = users.getId();
            }
        });

        databaseViewModel.getChaListUserDataSnapshot(currentUserId);
        databaseViewModel.getChaListUserDataSnapshot.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatList chatList = dataSnapshot1.getValue(ChatList.class);
                    userList.add(chatList);
                }
                chatLists();
            }
        });


    }

    private void setUserAdapter(){
        userAdapter = new UserFragmentAdapter(mUsers, context, true, userList);
        recyclerView_chat_fragment.setAdapter(userAdapter);
        initialize = true;
    }

    private void chatLists() {
        databaseViewModel.fetchUserByNameAll();
        databaseViewModel.fetchUserNames.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                mUsers.clear();
                recentChat.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatUsersData users = dataSnapshot1.getValue(ChatUsersData.class);
                    for (ChatList chatList : userList) {
                        assert users != null;
                        if (users.getId().equals(chatList.getId())) {
                            if(!mUsers.contains(users))
                                mUsers.add(users);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }
        });
    }


    private void updateToken(String token) {
        logInViewModel.updateToken(token);
    }


    private void init(View view) {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()))
                .get(DatabaseViewModel.class);

        logInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()))
                .get(LogInViewModel.class);

        recyclerView_chat_fragment = view.findViewById(R.id.recycler_view_chat_fragment);
        recyclerView_chat_fragment.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView_chat_fragment.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView_chat_fragment.addItemDecoration(dividerItemDecoration);
        mUsers = new ArrayList<>();
        userList = new ArrayList<>();
        recentChat = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        userData = ((MainActivity)MainActivity.context_main).userData;

        finalMatchUserList = new ArrayList<>();
        initialize = false;
    }

}
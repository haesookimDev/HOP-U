package com.example.HOP_U.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

public class FirebaseInstanceDatabase {
    private FirebaseDatabase instance = FirebaseDatabase.getInstance();
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
    @NonNull private String user;


    public MutableLiveData<DataSnapshot> fetchAllUserByNames() {
        final MutableLiveData<DataSnapshot> fetchAllUSerName = new MutableLiveData<>();

        instance.getReference("Users").orderByChild("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchAllUSerName.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return fetchAllUSerName;
    }


    private String getFileExtension(Uri uri, Context context) {
        ContentResolver contentResolver = Objects.requireNonNull(context).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }


    public MutableLiveData<StorageReference> fetchFileReference(String timeStamp, Uri imageUri, Context context) {
        final MutableLiveData<StorageReference> fetchFileReferenceImage = new MutableLiveData<>();
        final StorageReference fileReference = storageReference.child(timeStamp + "." + getFileExtension(imageUri, context));
        fetchFileReferenceImage.setValue(fileReference);
        return fetchFileReferenceImage;
    }

    public MutableLiveData<DataSnapshot> fetchSelectedUserIdData(String userId) {
        final MutableLiveData<DataSnapshot> fetchSelectedUserIDData = new MutableLiveData<>();

        instance.getReference("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchSelectedUserIDData.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return fetchSelectedUserIDData;
    }

    public MutableLiveData<DatabaseReference> getTokenRef() {
        final MutableLiveData<DatabaseReference> getTokenReference = new MutableLiveData<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        getTokenReference.setValue(reference);


        return getTokenReference;
    }

    public MutableLiveData<DataSnapshot> fetchUserDataCurrent() {
        final MutableLiveData<DataSnapshot> fetchCurrentUserData = new MutableLiveData<>();

        instance.getReference("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchCurrentUserData.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return fetchCurrentUserData;
    }

    public MutableLiveData<DataSnapshot> fetchSearchUser(String searchString) {
        final MutableLiveData<DataSnapshot> fetchSearchUserData = new MutableLiveData<>();

        Query query = instance.getReference("Users").orderByChild("search")
                .startAt(searchString)
                .endAt(searchString + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchSearchUserData.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return fetchSearchUserData;
    }


    public MutableLiveData<DataSnapshot> fetchChatUser() {
        final MutableLiveData<DataSnapshot> fetchUserChat = new MutableLiveData<>();
        instance.getReference("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchUserChat.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return fetchUserChat;
    }

    public MutableLiveData<DataSnapshot> fetchRecentChat(String UID) {
        final MutableLiveData<DataSnapshot> fetchChatStatus = new MutableLiveData<>();

        instance.getReference("RecentChat").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchChatStatus.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return fetchChatStatus;
    }

    public MutableLiveData<DataSnapshot> fetchUserStatus(String UID) {
        final MutableLiveData<DataSnapshot> fetchUserStatus = new MutableLiveData<>();

        instance.getReference("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchUserStatus.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return fetchUserStatus;
    }

    public MutableLiveData<Boolean> addChatsInDatabase(String receiverId, String senderId, String message, String timestamp) {
        final MutableLiveData<Boolean> successAddChatsDb = new MutableLiveData<>();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("receiverId", receiverId);
        hashMap.put("senderId", senderId);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("seen", false);

        instance.getReference("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddChatsDb.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddChatsDb.setValue(false);
            }
        });

        //creating chatList in database for better performance in chatListFragment .

        DatabaseReference chatRef = instance.getReference("ChatList")
                .child(senderId)
                .child(receiverId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(receiverId);
                    chatRef.child("timestamp").setValue(timestamp);
                    chatRef.child("recentChat").setValue(message);
                    chatRef.child("ban").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference chatRef2 = instance.getReference("ChatList")
                .child(receiverId)
                .child(senderId);


        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef2.child("id").setValue(senderId);
                    chatRef2.child("timestamp").setValue(timestamp);
                    chatRef2.child("recentChat").setValue(message);
                    chatRef2.child("ban").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference chatRecentRef = instance.getReference("RecentChat")
                .child(senderId)
                .child(receiverId);

        chatRecentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRecentRef.child("id").setValue(receiverId);
                    chatRecentRef.child("recentChat").setValue(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference chatRecentRef2 = instance.getReference("RecentChat")
                .child(receiverId)
                .child(senderId);

        chatRecentRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRecentRef2.child("id").setValue(receiverId);
                    chatRecentRef2.child("recentChat").setValue(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return successAddChatsDb;
    }

    public MutableLiveData<DataSnapshot> getChatList(String currentUserId) {
        final MutableLiveData<DataSnapshot> getChatLists = new MutableLiveData<>();
        if (currentUserId != null) {
            user = currentUserId;
        }else{
            user = firebaseUser.getUid();
        }
        DatabaseReference chatRef = instance.getReference("ChatList")
                .child(user);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getChatLists.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return getChatLists;
    }

    public MutableLiveData<Boolean> addRecentChatInDatabase(String Chat, DataSnapshot dataSnapshot){
        final MutableLiveData<Boolean> successAddRecentChatInDatabase = new MutableLiveData<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("recentChat", Chat);
        dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                successAddRecentChatInDatabase.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddRecentChatInDatabase.setValue(false);
            }
        });


        return successAddRecentChatInDatabase;
    }

    public MutableLiveData<Boolean> addBanUser(DataSnapshot dataSnapshot){
        final MutableLiveData<Boolean> successAddBanUser = new MutableLiveData<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("ban", true);
        dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                successAddBanUser.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddBanUser.setValue(false);
            }
        });

        return successAddBanUser;
    }


    public MutableLiveData<Boolean> addIsSeenInDatabase(String isSeen, DataSnapshot dataSnapshot) {
        final MutableLiveData<Boolean> successAddIsSeen = new MutableLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put(isSeen, true);
        dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                successAddIsSeen.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddIsSeen.setValue(false);
            }
        });

        return successAddIsSeen;
    }


    public MutableLiveData<Boolean> addImageUrlInDatabase(String imageUrl, Object mUri) {
        final MutableLiveData<Boolean> successAddUriImage = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put(imageUrl, mUri);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddUriImage.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddUriImage.setValue(false);
            }
        });


        return successAddUriImage;
    }

    public MutableLiveData<Boolean> addUsernameInDatabase(String usernameUpdated, Object username) {
        final MutableLiveData<Boolean> successAddUserName = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put(usernameUpdated, username);
        String searchUserNam = username.toString().toLowerCase();       // changing search userName as well
        map.put("search", searchUserNam);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddUserName.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddUserName.setValue(false);
            }
        });

        return successAddUserName;
    }

    public MutableLiveData<Boolean> addBioInDatabase(String bioUpdated, Object bio) {
        final MutableLiveData<Boolean> successAddBio = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put(bioUpdated, bio);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddBio.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddBio.setValue(false);
            }
        });

        return successAddBio;
    }

    public MutableLiveData<Boolean> addStatusInDatabase(String statusUpdated, Object status, String UID) {
        final MutableLiveData<Boolean> successAddStatus = new MutableLiveData<>();
        String id=UID;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String, Object> map = new HashMap<>();
        map.put(statusUpdated, status);

        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ref.child(id).updateChildren(map);
                    successAddStatus.setValue(true);
                } else {

                    successAddStatus.setValue(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                successAddStatus.setValue(false);
            }
        });
        return successAddStatus;
    }


    public MutableLiveData<Boolean> addUserInDatabase(String userId, String userName, String emailId, String timestamp, String imageUrl) {
        final MutableLiveData<Boolean> successAddUserDb = new MutableLiveData<>();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", userId);
        hashMap.put("username", userName);
        hashMap.put("emailId", emailId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("imageUrl", imageUrl);
        hashMap.put("bio", "Hey there!");
        hashMap.put("status", "offline");
        hashMap.put("search", userName.toLowerCase());

        instance.getReference("Users").child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddUserDb.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddUserDb.setValue(false);
            }
        });

        return successAddUserDb;
    }
}

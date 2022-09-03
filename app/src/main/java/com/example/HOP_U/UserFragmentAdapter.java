package com.example.HOP_U;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.HOP_U.ChatUsersData;
import com.example.HOP_U.viewModel.DatabaseViewModel;
import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragmentAdapter extends RecyclerView.Adapter<UserFragmentAdapter.UserFragmentHolder> {

    private ArrayList<ChatUsersData> usersArrayList;
    private Context context;
    private Boolean isChat;
    private ArrayList<ChatList> recentChat;

    public UserFragmentAdapter(ArrayList<ChatUsersData> usersArrayList, Context context, Boolean isChat, ArrayList<ChatList> userChat) {
        this.usersArrayList = usersArrayList;
        this.context = context;
        this.isChat = isChat;
        this.recentChat = userChat;
    }

    @NonNull
    @Override
    public UserFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.user_list_item_view, parent, false);

        return new UserFragmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFragmentHolder holder, int position) {
        ChatUsersData users = usersArrayList.get(position);

        ChatList chat = recentChat.get(position);

        String imageUrl = users.getImageUrl();

        if (imageUrl.equals("default")) {
            holder.iv_profile_image.setImageResource(R.drawable.sample_img);
        } else {
            Glide.with(context).load(imageUrl).into(holder.iv_profile_image);
        }

        String timeStamp = chat.getTimestamp();
        long intTimeStamp = Long.parseLong(timeStamp);
        String time_msg_received = timeStampConversionToTime(intTimeStamp);

        holder.tv_name.setText("또래 상담");
        holder.tv_chat.setText(time_msg_received);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("matchingUID", users.getId());
                context.startActivity(intent);
            }
        });

    }

    public String timeStampConversionToTime(long timeStamp) {
        Date date = new Date(timeStamp);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat jdf = new SimpleDateFormat("YYYY-MM-dd a hh시 mm분");
        jdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return jdf.format(date);

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }


    public boolean isNetworkConnected() throws InterruptedException, IOException {
        final String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }

    public class UserFragmentHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_profile_image;
        CircleImageView chat_profile;
        TextView tv_name;
        ImageView iv_status_user_list;
        TextView tv_chat;
        ChatList userList;

        UserFragmentHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile_image = itemView.findViewById(R.id.profile_image);
            tv_name = itemView.findViewById(R.id.user_name_list);
            chat_profile = itemView.findViewById(R.id.profile_image_chat);
            tv_chat = itemView.findViewById(R.id.user_chat_list);
        }
    }

}

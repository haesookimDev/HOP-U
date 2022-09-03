package com.example.HOP_U;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class trialCommentAdapter extends RecyclerView.Adapter<trialCommentAdapter.ViewHolder> {

    private ArrayList<trialCommentData> mData = null ;

    public trialCommentAdapter(ArrayList<trialCommentData> data) {
        mData = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_trial_comments, parent, false);
        trialCommentAdapter.ViewHolder vh = new trialCommentAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        trialCommentData item = mData.get(position);


        if(item.getProsAndCons().equals("찬성")){
            holder.prosAndCons.setText("찬");
            holder.prosAndCons.setTextColor(Color.parseColor("#2D5BEF"));
        } else {
            holder.prosAndCons.setText("반");
            holder.prosAndCons.setTextColor(Color.parseColor("#FF5D49"));
        }
        holder.comment.setText(item.getComment());
        holder.name.setText(item.getName());
        holder.timestamp.setText(new SimpleDateFormat("YYYY.MM.dd HH:mm").format(item.getTimestamp().toDate()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView prosAndCons ;
        TextView comment;
        TextView name;
        TextView timestamp;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            prosAndCons = itemView.findViewById(R.id.prosAndCons);
            comment = itemView.findViewById(R.id.comment);
            name = itemView.findViewById(R.id.uName);
            timestamp = itemView.findViewById(R.id.trialCommentTime);
        }
    }

}

package com.example.HOP_U;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CheeringAdapter extends RecyclerView.Adapter<CheeringAdapter.ViewHolder> {

    private ArrayList<CheeringData> mData = null ;

    public CheeringAdapter(ArrayList<CheeringData> data){mData = data;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_cheering_recycler, parent, false);
        CheeringAdapter.ViewHolder vh = new CheeringAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheeringData item = mData.get(position);

        holder.cheer.setText(item.getCheer());
        holder.name.setText(item.getName()+ " 님");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cheer;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cheer = itemView.findViewById(R.id.cheeringText);
            name = itemView.findViewById(R.id.cheeringName);

        }
    }
}

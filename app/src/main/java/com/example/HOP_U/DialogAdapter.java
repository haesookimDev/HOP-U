package com.example.HOP_U;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {

    private ArrayList<DialogData> mData = null ;
    private DialogData item;

    public DialogAdapter(ArrayList<DialogData> data) {
        mData = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_dialog, parent, false);
        DialogAdapter.ViewHolder vh = new DialogAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        item = mData.get(position);

        holder.dialogNumber.setText(item.getNumber());
        holder.dialogName.setText(" "+item.getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dialogNumber;
        TextView dialogName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dialogNumber = itemView.findViewById(R.id.dialogNumber);
            dialogName = itemView.findViewById(R.id.dialogName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:"+dialogNumber.getText().toString()));
                        (MainActivity.context_main).startActivity(intent);
                    }
                }
            });

        }
    }
}

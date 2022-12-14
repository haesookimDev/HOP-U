package com.example.HOP_U;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<News_Data> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(News_Data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView subtitle;
        private TextView day;
        private ImageView imageView;

        ItemViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.whostalk_title);
            subtitle = itemView.findViewById(R.id.whostalk_subtitle);
            day = itemView.findViewById(R.id.whostalk_day);
            imageView = itemView.findViewById(R.id.whostalk_imageView);
        }

        void onBind(News_Data data) {
            title.setText(data.getTitle());
            subtitle.setText(data.getContent());
            day.setText(data.getDay());
            imageView.setImageResource(data.getResId());
        }
    }
}

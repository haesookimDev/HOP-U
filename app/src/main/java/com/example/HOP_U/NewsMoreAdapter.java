package com.example.HOP_U;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class NewsMoreAdapter extends RecyclerView.Adapter<NewsMoreAdapter.ViewHolder> {

    private ArrayList<NewsMoreData> mData = null ;

    public NewsMoreAdapter(ArrayList<NewsMoreData> data){mData = data;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.card_news_more_item, parent, false);
        NewsMoreAdapter.ViewHolder vh = new NewsMoreAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsMoreData item = mData.get(position);

        holder.title.setText(item.getTitle());


        holder.pagerAdapter = new MyNewsMoreAdapter(holder.newsMoreActivity, item.getListResId().size(), item.getListResId());

        holder.news_mPager.setAdapter(holder.pagerAdapter);
//Indicator
        holder.news_mIndicator.setViewPager(holder.news_mPager);
        holder.news_mIndicator.createIndicators(item.getListResId().size(), 0);
//ViewPager Setting
        holder.news_mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
/**
 * 이 부분 조정하여 처음 시작하는 이미지 설정.
 * 2000장 생성하였으니 현재위치 1002로 설정하여
 * 좌 우로 슬라이딩 할 수 있게 함. 거의 무한대로
 */
        holder.news_mPager.setCurrentItem(0); //시작 지점
        holder.news_mPager.setOffscreenPageLimit(10); //최대 이미지 수
        holder.news_mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    holder.news_mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                holder.news_mIndicator.animatePageSelected(position % item.getListResId().size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ViewPager2 news_mPager;
        private CircleIndicator3 news_mIndicator;
        private FragmentStateAdapter pagerAdapter;
        private NewsMoreActivity newsMoreActivity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newsMoreActivity = ((NewsMoreActivity) NewsMoreActivity.context_main);

            title = itemView.findViewById(R.id.news_more_title);
            news_mPager = itemView.findViewById(R.id.news_more_viewpager);
            news_mIndicator = itemView.findViewById(R.id.news_more_indicator);
        }
    }
}

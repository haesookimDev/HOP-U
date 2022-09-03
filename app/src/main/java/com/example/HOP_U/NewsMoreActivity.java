package com.example.HOP_U;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsMoreActivity extends AppCompatActivity {

    private ImageView backCardNews;

    private RecyclerView newsMoreRecycler = null;
    private NewsMoreAdapter newsMoreAdapter = null;
    private ArrayList<NewsMoreData> newsMoreDataArrayList;
    private NewsMoreRecyclerDeco newsMoreRecyclerDeco;

    public static Context context_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_more);

        context_main = this;

        backCardNews = findViewById(R.id.backCardNews);

        newsMoreRecycler = findViewById(R.id.news_more_recycler);
        newsMoreDataArrayList = new ArrayList<>();
        newsMoreAdapter = new NewsMoreAdapter(newsMoreDataArrayList);
        newsMoreRecycler.setAdapter(newsMoreAdapter);
        newsMoreRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        newsMoreRecyclerDeco = new NewsMoreRecyclerDeco(this);
        newsMoreRecycler.addItemDecoration(newsMoreRecyclerDeco);

        backCardNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getData();
    }


    @Override
    public void onBackPressed(){
        finish();
    }

    private void addItem(String name, List<Integer> resId) {
        NewsMoreData item = new NewsMoreData(name, resId);
        newsMoreDataArrayList.add(item);
    }

    private void getData(){
        List<Integer> listResId1 = Arrays.asList(
                R.drawable.cardnews1,
                R.drawable.card_news_1,
                R.drawable.card_news_2,
                R.drawable.card_news_3
        );

        List<Integer> listResId2 = Arrays.asList(
                R.drawable.cardnews2,
                R.drawable.card_news_1,
                R.drawable.card_news_2,
                R.drawable.card_news_3,
                R.drawable.card_news_4,
                R.drawable.card_news_5
        );
        List<Integer> listResId3 = Arrays.asList(
                R.drawable.cardnews3,
                R.drawable.card_news_1,
                R.drawable.card_news_2,
                R.drawable.card_news_3,
                R.drawable.card_news_4,
                R.drawable.card_news_5,
                R.drawable.card_news_6,
                R.drawable.card_news_7
        );

        addItem("우리가 잘 모르는 사이버폭력", listResId1);
        addItem("사이버폭력 STOP", listResId2);
        addItem("푸코와 함께 하는 사이버폭력 예방 ", listResId3);

        newsMoreAdapter.notifyDataSetChanged();

    }

}
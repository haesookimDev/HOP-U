package com.example.HOP_U;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class NewsFragment extends Fragment {

    private ViewPager2 news_mPager;
    private FragmentStateAdapter news_pagerAdapter;
    private int num_page = 3;
    private CircleIndicator3 news_mIndicator;
    private Button cardNewsMore;

    //
    private NewsRecyclerAdapter newsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_news, container, false);

        cardNewsMore = view.findViewById(R.id.cardnews_more);

        cardNewsMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsMoreActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 가로 슬라이드 뷰 Fragment
         */
//ViewPager2
        news_mPager = view.findViewById(R.id.news_viewpager);
//Adapter
        news_pagerAdapter = new MyNewsAdapter(getActivity(), num_page);
        news_mPager.setAdapter(news_pagerAdapter);
//Indicator
        news_mIndicator = view.findViewById(R.id.news_indicator);
        news_mIndicator.setViewPager(news_mPager);
        news_mIndicator.createIndicators(num_page, 0);
//ViewPager Setting
        news_mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
/**
 * 이 부분 조정하여 처음 시작하는 이미지 설정.
 * 2000장 생성하였으니 현재위치 1002로 설정하여
 * 좌 우로 슬라이딩 할 수 있게 함. 거의 무한대로
 */
        news_mPager.setCurrentItem(999); //시작 지점
        news_mPager.setOffscreenPageLimit(5); //최대 이미지 수
        news_mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    news_mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                news_mIndicator.animatePageSelected(position % num_page);
            }
        });


        //리사이클러뷰
        news_init(view);

        getData();

        // Inflate the layout for this fragment
        return view;
    }

    private void news_init(ViewGroup viewGroup) {
        RecyclerView recyclerView = viewGroup.findViewById(R.id.whostalk_recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        newsAdapter = new NewsRecyclerAdapter();
        recyclerView.setAdapter(newsAdapter);
    }

    private void getData() {
        // 임의의 데이터입니다.
        List<String> listTitle = Arrays.asList("사이버 폭력, 그 이후", "고등학생 때 있었던 일", "시간이 약이다?");
        List<String> listContent = Arrays.asList(
                "사이버폭력 신고와 그동안의 이야기를 정리한 이야기",
                "싸우면서 크는 거라지만 그땐 너무 힘들었어요",
                "정말 시간이 지나면 괜찮아지는 걸까?"
        );
        List<String> listDay = Arrays.asList(
                "2021.11.25",
                "2021.11.22",
                "2021.11.13"
        );
        List<Integer> listResId = Arrays.asList(
                R.drawable.whostalk1,
                R.drawable.whostalk2,
                R.drawable.whostalk3
        );
        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            News_Data data = new News_Data();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));
            data.setDay(listDay.get(i));
            data.setResId(listResId.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            newsAdapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        newsAdapter.notifyDataSetChanged();
    }

}
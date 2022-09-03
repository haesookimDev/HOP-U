package com.example.HOP_U;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

public class ConsultingFragment extends Fragment {

    private static final String TAG = "Main_Activity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ConsultingFragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_consulting, container, false);

        tabLayout= view.findViewById(R.id.tabs);
        viewPager=view.findViewById(R.id.view_pager);
        adapter=new ConsultingFragmentAdapter(getChildFragmentManager(),1);

        //FragmentAdapter에 컬렉션 담기
        adapter.addFragment(new Consulting_Fragment1());
        adapter.addFragment(new Consulting_Fragment2(getContext()));

        //ViewPager Fragment 연결
        viewPager.setAdapter(adapter);

        //ViewPager과 TabLayout 연결
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("전문가 상담");
        tabLayout.getTabAt(1).setText("또래상담");

        // Inflate the layout for this fragment
        return view;
    }
}
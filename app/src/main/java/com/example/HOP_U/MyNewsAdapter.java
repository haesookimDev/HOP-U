package com.example.HOP_U;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyNewsAdapter extends FragmentStateAdapter {
    public int mCount;
    public MyNewsAdapter(FragmentActivity fa, int count) {
        super(fa);
        mCount = count;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);
        if(index==0) return new News_frame1();
        else if(index==1) return new News_frame2();
        else return new News_frame3();
    }
    @Override
    public int getItemCount() {
        return 2000;
    }
    public int getRealPosition(int position) { return position % mCount; }
}

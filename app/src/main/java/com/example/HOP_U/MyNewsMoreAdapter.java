package com.example.HOP_U;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class MyNewsMoreAdapter extends FragmentStateAdapter {
    public int mCount;
    private List<Integer> resId;
    public MyNewsMoreAdapter(FragmentActivity fa, int count, List<Integer> resId) {
        super(fa);
        mCount = count;
        this.resId = resId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        return new NewsMoreFrame(this.resId.get(index));
    }

    @Override
    public int getItemCount() {
        return 2000;
    }
    public int getRealPosition(int position) { return position % mCount; }
}

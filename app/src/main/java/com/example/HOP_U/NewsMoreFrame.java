package com.example.HOP_U;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class NewsMoreFrame extends Fragment {

    private Integer drawable;
    private ImageView card;

    public NewsMoreFrame(Integer drawable) {
        this.drawable = drawable;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_news_more_frame, container, false);

        card = rootView.findViewById(R.id.cardNewsMoreContents);

        card.setBackgroundResource(drawable);

        return rootView;
    }
}
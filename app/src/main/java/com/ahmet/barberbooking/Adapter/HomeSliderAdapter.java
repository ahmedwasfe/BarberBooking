package com.ahmet.barberbooking.Adapter;


import android.content.Context;

import com.ahmet.barberbooking.Model.Banner;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class HomeSliderAdapter extends SliderAdapter {


    private List<Banner> mListBanner;

    public HomeSliderAdapter(List<Banner> mListBanner) {
        this.mListBanner = mListBanner;
    }

    @Override
    public int getItemCount() {
        return mListBanner.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(mListBanner.get(position).getImage());
    }
}

package com.ahmet.barberbooking.Interface;

import com.ahmet.barberbooking.Model.Banner;

import java.util.List;

public interface IBannerLoadListener {

    void onLoadBannerSuccess(List<Banner> mListBanner);
    void onLoadBannerFailed(String error);
}

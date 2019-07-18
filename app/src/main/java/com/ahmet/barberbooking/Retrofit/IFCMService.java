package com.ahmet.barberbooking.Retrofit;

import com.ahmet.barberbooking.Model.FCMResponse;
import com.ahmet.barberbooking.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAALFGxzTg:APA91bFkhs671vePcHRky_vBnoln7uTvf33Knros-LN2wrVoGhAb04lhfGrv_XqkdKCGKNRzIwIKh3b27y7JFJWRABIpM-HhNFc-5dRNRVUx5yAFG0HuCZBDfs6koO4PBT-RXsBCk8Qi"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNOtification(@Body FCMSendData body);
}

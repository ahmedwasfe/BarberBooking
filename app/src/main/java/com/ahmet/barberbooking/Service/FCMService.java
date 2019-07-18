package com.ahmet.barberbooking.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ahmet.barberbooking.Common.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Common.updateToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Common.showNotification(this,
                new Random().nextInt(),
                remoteMessage.getData().get(Common.KEY_TITLE),
                remoteMessage.getData().get(Common.KEY_CONTENT),
                null);

    }
}

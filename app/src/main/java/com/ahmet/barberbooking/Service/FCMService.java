package com.ahmet.barberbooking.Service;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ahmet.barberbooking.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Common.updateToken(this, token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // mMapSendData.put("updateDone", "true");
        if (remoteMessage.getData() != null){
            if (remoteMessage.getData().get("done") != null){

                updateLastBooking();

                Map<String, String> mMapRecivedData = remoteMessage.getData();
                Paper.init(this);
                Paper.book().write(Common.KEY_RATING_INFORMATION, new Gson().toJson(mMapRecivedData));
            }
//
            if (remoteMessage.getData().get(Common.KEY_TITLE) != null &&
                    remoteMessage.getData().get(Common.KEY_CONTENT) != null){

                Common.showNotification(this,
                        new Random().nextInt(),
                        remoteMessage.getData().get(Common.KEY_TITLE),
                        remoteMessage.getData().get(Common.KEY_CONTENT),
                        null);

            }
        }

    }

    private void updateLastBooking() {

        // Here we need get current salon_men login
        // Because app mayce run on background so we need get from Paper

        CollectionReference mCollectionRefUserBooking;
        // If app running
        if (Common.currentUser != null){

            mCollectionRefUserBooking = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking");
        } else {

            // If app not running
            Paper.init(this);
            String currentUser = Paper.book().read(Common.KEY_LOGGED);

            mCollectionRefUserBooking = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(currentUser)
                    .collection("Booking");
        }

        /* Check id exists by get current data
         * Why we only work for current date ? Because in my scenario, we only load
         * appoiment for current date and next 3 day
         */
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.MINUTE, 0);

        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        // Get Only booking info with time is today of next day    timestamp
        mCollectionRefUserBooking.whereGreaterThanOrEqualTo("timestamp", timestamp)
                // And done filed is false (not done service)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FCMService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().size() > 0){

                        // Update
                        DocumentReference mDocumentRefUserBooking = null;
                        for (DocumentSnapshot documentSnapshot : task.getResult()){
                            mDocumentRefUserBooking = mCollectionRefUserBooking.document(documentSnapshot.getId());
                        }

                        if (mDocumentRefUserBooking != null){

                            Map<String, Object> mMapUpdateData = new HashMap<>();
                            mMapUpdateData.put("done", true);
                            mDocumentRefUserBooking.update(mMapUpdateData)
                                    .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(FCMService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });

    }
}

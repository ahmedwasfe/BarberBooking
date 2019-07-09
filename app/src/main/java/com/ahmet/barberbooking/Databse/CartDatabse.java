package com.ahmet.barberbooking.Databse;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = CartItem.class, exportSchema = false)
public abstract class CartDatabse extends RoomDatabase {

    private static CartDatabse instance;

    public abstract CartDAO cartDAO();

    public static CartDatabse getInstance(Context mContext){
        if (instance == null){

            instance = Room
                    .databaseBuilder(mContext,CartDatabse.class, "BarBer")
                    .build();
        }

        return instance;
    }
}

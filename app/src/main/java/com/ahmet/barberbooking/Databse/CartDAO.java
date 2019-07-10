package com.ahmet.barberbooking.Databse;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDAO {

    // get all items by user phone from database
    @Query("SELECT * FROM Cart WHERE userPhone=:userPhone")
    List<CartItem> getAllItemFromCart(String userPhone);

    // get count items by user phone from databse
    @Query("SELECT count(*) FROM Cart WHERE userPhone=:userPhone")
    int countItemInCart(String userPhone);

    // get all items by product Id and user phone from databse
    @Query("SELECT * FROM Cart WHERE productId=:productId AND userPhone=:userPhone")
    CartItem getproductInCart(String productId, String userPhone);

    // Insert to databse
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(CartItem...carts);

    // Update item
    @Update(onConflict = OnConflictStrategy.ABORT)
    void update(CartItem cart);

    // Sum Price
    @Query("SELECT SUM(productPrice*productQuantity) FROM Cart WHERE userPhone=:userPhone")
    long sumPrice(String userPhone);

    // Delete from database
    @Delete
    void delete(CartItem cartItem);

    // clear all item from databse
    @Query("DELETE FROM Cart WHERE userPhone=:userPhone")
    void clearCart(String userPhone);
}

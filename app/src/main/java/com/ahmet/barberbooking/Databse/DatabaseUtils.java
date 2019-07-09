package com.ahmet.barberbooking.Databse;

import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Interface.ICountItemInCartListener;
import com.google.protobuf.StringValue;

import java.util.List;

public class DatabaseUtils {

    // Because all room handle need work on other thread

    // get all items by user phone from database
    public static void getAllItemFromCart(CartDatabse db){

        GetAllCartAsync task = new GetAllCartAsync(db);
        task.execute(Common.currentUser.getPhoneNumber());
    }

    // Insert to databse
    public static void insertToCart(CartDatabse db, CartItem...cartItems){

        InsertToCartAsync task = new InsertToCartAsync(db);
        task.execute(cartItems);
    }

    // Count Items in databse
    public static void countItemsInCart(CartDatabse db, ICountItemInCartListener iCountItemInCartListener){

        CountItemInCartAsync task = new CountItemInCartAsync(db, iCountItemInCartListener);
        task.execute();
    }


    /*
    * ===========================================================
    * ASYNC TASK DEFINE
    * */

    // Async Task To get All Items
    private static class GetAllCartAsync extends AsyncTask<String, Void, Void>{

        private CartDatabse databse;

        public GetAllCartAsync(CartDatabse cartDatabse){
                databse = cartDatabse;
        }

        @Override
        protected Void doInBackground(String... strings) {

            getAllItemFromCartByUserPhone(databse, strings[0]);

            return null;
        }

        private void getAllItemFromCartByUserPhone(CartDatabse databse, String userPhone) {

            List<CartItem> mListCartItem = databse.cartDAO().getAllItemFromCart(userPhone);
            Log.d("COUNT_CART", "" + mListCartItem.size());
        }
    }

    // Async Task To Insert Items to database
    private static class InsertToCartAsync extends AsyncTask<CartItem, Void, Void>{

        private CartDatabse databse;

        public InsertToCartAsync(CartDatabse cartDatabse){
            databse = cartDatabse;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {

            InsertToCartByUserPhone(databse, cartItems[0]);
            return null;
        }

        private void InsertToCartByUserPhone(CartDatabse databse, CartItem cartItem) {

            // If Item already availavble in cart just increse quantity

            try{
                databse.cartDAO().insert(cartItem);
            }catch (SQLiteConstraintException exception){
                CartItem updateCartItem = databse.cartDAO().getproductInCart(cartItem.getProductId(),
                                                    Common.currentUser.getPhoneNumber());
                updateCartItem.setProductQuantity(cartItem.getProductQuantity() + 1);
                databse.cartDAO().update(updateCartItem);
            }


        }


    }

    // Async Task To Count Items in Cart
    private static class CountItemInCartAsync extends AsyncTask<Void, Void, Integer>{

        private CartDatabse databse;
        private ICountItemInCartListener listener;

        public CountItemInCartAsync(CartDatabse cartDatabse, ICountItemInCartListener iCountItemInCartListener){
            databse = cartDatabse;
            listener = iCountItemInCartListener;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            return Integer.parseInt(String.valueOf(countItemsInCartRun(databse)));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            listener.onCountItemCartSuccess(integer.intValue());
        }

        private int countItemsInCartRun(CartDatabse databse) {

            return databse.cartDAO().countItemInCart(Common.currentUser.getPhoneNumber());
        }
    }
}

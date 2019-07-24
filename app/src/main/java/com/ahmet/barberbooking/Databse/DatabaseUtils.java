package com.ahmet.barberbooking.Databse;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Interface.ICartItemLoadListener;
import com.ahmet.barberbooking.Interface.ICountItemInCartListener;
import com.ahmet.barberbooking.Interface.ISumCartListener;

import java.util.List;

public class DatabaseUtils {

    // Because all room handle need work on other thread

    // get all items by user phone from database
    public static void getAllItemFromCart(CartDatabase db, ICartItemLoadListener iCartItemLoadListener){

        GetAllCartAsync task = new GetAllCartAsync(db, iCartItemLoadListener);
        task.execute();

    }

    // Insert to databse
    public static void insertToCart(CartDatabase db, CartItem...cartItems){

        InsertToCartAsync task = new InsertToCartAsync(db);
        task.execute(cartItems);
    }

    // Update Cart item
    public static void updateCart(CartDatabase db, CartItem cartItem){

        UpdateCartAsync task = new UpdateCartAsync(db);
        task.execute(cartItem);
    }

    // Sum Item In Cart
    public static void sumCart(CartDatabase db, ISumCartListener iSumCartListener){

        SumCartAsync task = new SumCartAsync(db, iSumCartListener);
        task.execute();
    }

    // Count Items in databse
    public static void countItemsInCart(CartDatabase db, ICountItemInCartListener iCountItemInCartListener){

        CountItemInCartAsync task = new CountItemInCartAsync(db, iCountItemInCartListener);
        task.execute();
    }

    // Delete item from cart
    public static void deleteItemFromCart(@NonNull CartDatabase db, CartItem cartItem){

        DeleteFromCartAsync task = new DeleteFromCartAsync(db);
        task.execute(cartItem);

    }

    public static void clearCart(CartDatabase db){

        ClearCartAsync task = new ClearCartAsync(db);
        task.execute();
    }

    /*
    * ===========================================================
    * ASYNC TASK DEFINE
    * */

    // Async Task To get All Items
    private static class GetAllCartAsync extends AsyncTask<String, Void, List<CartItem>>{

        private CartDatabase databse;
        private ICartItemLoadListener listener;

        public GetAllCartAsync(CartDatabase cartDatabase, ICartItemLoadListener iCartItemLoadListener){
                databse = cartDatabase;
                listener = iCartItemLoadListener;
        }

        @Override
        protected List<CartItem> doInBackground(String... strings) {
            return databse.cartDAO().getAllItemFromCart(Common.currentUser.getPhoneNumber());
        }

        @Override
        protected void onPostExecute(List<CartItem> mListCartItem) {
            super.onPostExecute(mListCartItem);
            listener.onLoadAllItemInCartSuccess(mListCartItem);
        }

        //        private void getAllItemFromCartByUserPhone(CartDatabase databse, String userPhone) {
//
//            List<CartItem> mListCartItem = databse.cartDAO().getAllItemFromCart(userPhone);
//            Log.d("COUNT_CART", "" + mListCartItem.size());
//        }
    }

    // Async Task To Sum Item Cart In Database
    private static class SumCartAsync extends AsyncTask<Void, Void, Long>{

        private final CartDatabase database;
        private final ISumCartListener listener;

        public SumCartAsync(CartDatabase database, ISumCartListener listener) {
            this.database = database;
            this.listener = listener;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return database.cartDAO().sumPrice(Common.currentUser.getPhoneNumber());
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            listener.onSumCartSuccess(aLong);
        }
    }

    // Async Task To Insert Items to database
    private static class InsertToCartAsync extends AsyncTask<CartItem, Void, Void>{

        private CartDatabase databse;

        public InsertToCartAsync(CartDatabase cartDatabase){
            databse = cartDatabase;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {

            InsertToCartByUserPhone(databse, cartItems[0]);
            return null;
        }

        private void InsertToCartByUserPhone(CartDatabase databse, CartItem cartItem) {

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

    // Async Task To Update Items to database
    private static class UpdateCartAsync extends AsyncTask<CartItem, Void, Void>{

        private final CartDatabase databse;

        public UpdateCartAsync(CartDatabase databse) {
            this.databse = databse;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            databse.cartDAO().update(cartItems[0]);
            return null;
        }
    }

    // Async Task To Count Items in Cart
    private static class CountItemInCartAsync extends AsyncTask<Void, Void, Integer>{

        private CartDatabase databse;
        private ICountItemInCartListener listener;

        public CountItemInCartAsync(CartDatabase cartDatabase, ICountItemInCartListener iCountItemInCartListener){
            databse = cartDatabase;
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

        private int countItemsInCartRun(CartDatabase databse) {

            return databse.cartDAO().countItemInCart(Common.currentUser.getPhoneNumber());
        }
    }

    // Async Task To Delete Item from database
    private static class DeleteFromCartAsync extends AsyncTask<CartItem, Void, Void>{

        private final CartDatabase databse;

        public DeleteFromCartAsync(CartDatabase databse) {
            this.databse = databse;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            databse.cartDAO().delete(cartItems[0]);
            return null;
        }
    }

    // Async Task To clear all Item from database
    private static class ClearCartAsync extends AsyncTask<Void, Void, Void>{

        private final CartDatabase databse;

        public ClearCartAsync(CartDatabase databse) {
            this.databse = databse;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            clearAllItemFromCart(databse);
            return null;
        }

        private void clearAllItemFromCart(CartDatabase databse){
            databse.cartDAO().clearCart(Common.currentUser.getPhoneNumber());
        }
    }
}

package com.ahmet.barberbooking.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Model.Shopping;
import com.ahmet.barberbooking.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingHolder> {

    private Context mContext;
    private List<Shopping> mListShopping;
   // private LayoutInflater inflater;

    public ShoppingAdapter(Context mContext, List<Shopping> mListShopping) {
        this.mContext = mContext;
        this.mListShopping = mListShopping;
       // inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ShoppingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(mContext)
                .inflate(R.layout.raw_shopping, parent, false);

        return new ShoppingHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingHolder holder, int position) {

        holder.mTxtShoppingName.setText(Common.formatShoppingName(mListShopping.get(position).getName()));
        holder.mTxtShoppingPrice.setText(new StringBuilder("$ ").append(mListShopping.get(position).getPrice()));
        Picasso.get()
                .load(mListShopping.get(position).getImage())
                .placeholder(R.drawable.default_item)
                .into(holder.mImageShoppingItem);

    }

    @Override
    public int getItemCount() {
        return mListShopping.size();
    }

    static class ShoppingHolder extends RecyclerView.ViewHolder{

        ImageView mImageShoppingItem;
        TextView mTxtShoppingName, mTxtShoppingPrice, mTxtShoppingAddToCart;
        CardView mCardShopping;

        public ShoppingHolder(@NonNull View itemView) {
            super(itemView);

            mImageShoppingItem = itemView.findViewById(R.id.img_shopping_item);
            mTxtShoppingName = itemView.findViewById(R.id.txt_name_shopping_item);
            mTxtShoppingPrice = itemView.findViewById(R.id.txt_price_shopping_item);
            mTxtShoppingAddToCart = itemView.findViewById(R.id.txt_shopping_add_cart);
            mCardShopping = itemView.findViewById(R.id.card_shopping);
        }
    }
}

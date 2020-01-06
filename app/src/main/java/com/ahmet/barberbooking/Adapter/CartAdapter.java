package com.ahmet.barberbooking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Databse.CartDatabase;
import com.ahmet.barberbooking.Databse.CartItem;
import com.ahmet.barberbooking.Databse.DatabaseUtils;
import com.ahmet.barberbooking.Interface.ICartItemUpdateListener;
import com.ahmet.barberbooking.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private Context mContext;
    private List<CartItem> mListCartItem;

    private LayoutInflater inflater;

    private CartDatabase mCartDatabase;

    private ICartItemUpdateListener iCartItemUpdateListener;

    public CartAdapter(Context mContext, List<CartItem> mListCartItem, ICartItemUpdateListener iCartItemUpdateListener) {
        this.mContext = mContext;
        this.mListCartItem = mListCartItem;
        this.iCartItemUpdateListener = iCartItemUpdateListener;
        this.mCartDatabase = CartDatabase.getInstance(mContext);
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = inflater.inflate(R.layout.raw_cart, parent, false);
        return new CartHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {

        holder.mTxtCartNameItem.setText(Common.formatName(mListCartItem.get(position).getProductName()));
        holder.mTxtCartPriceItem.setText(new StringBuilder("$ ").append(mListCartItem.get(position).getProductPrice()));
        holder.mTxtCartQuantityItem.setText(new StringBuilder(String.valueOf(mListCartItem.get(position).getProductQuantity())));
        Picasso.get()
                .load(mListCartItem.get(position).getProductImage())
                .placeholder(R.drawable.default_item)
                .into(holder.mImageCartItem);

        // Event
        holder.setListener(new IImageListener() {
            @Override
            public void onImageClickListener(View view, int position, boolean isDecrease) {

                if (isDecrease){

                    if (mListCartItem.get(position).getProductQuantity() > 0){
                        mListCartItem.get(position)
                                      .setProductQuantity(mListCartItem
                                              .get(position)
                                              .getProductQuantity() - 1);
                        DatabaseUtils.updateCart(mCartDatabase, mListCartItem.get(position));

                        holder.mTxtCartQuantityItem.setText(new StringBuilder(String.valueOf(mListCartItem.get(position).getProductQuantity())));

                    } else if (mListCartItem.get(position).getProductQuantity() == 0){

                        DatabaseUtils.deleteItemFromCart(mCartDatabase, mListCartItem.get(position));
                        mListCartItem.remove(position);
                        notifyItemRemoved(position);
                       // notifyDataSetChanged();

                    }

                } else {

                    if (mListCartItem.get(position).getProductQuantity() < 99) {
                        mListCartItem.get(position)
                                .setProductQuantity(mListCartItem
                                        .get(position)
                                        .getProductQuantity() + 1);
                        DatabaseUtils.updateCart(mCartDatabase, mListCartItem.get(position));

                        holder.mTxtCartQuantityItem.setText(new StringBuilder(String.valueOf(mListCartItem.get(position).getProductQuantity())));
                    }
                }

                iCartItemUpdateListener.onCartItemUpdateSuccess();

            }
        });

        holder.mImgDeleteFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseUtils.deleteItemFromCart(mCartDatabase, mListCartItem.get(position));
                mListCartItem.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListCartItem.size();
    }

    static class CartHolder extends RecyclerView.ViewHolder{

        ImageView mImageCartItem, mImgIncreaseQuantity, mImgDecreaseQuantity, mImgDeleteFromCart;
        TextView mTxtCartNameItem, mTxtCartPriceItem, mTxtCartQuantityItem;

        IImageListener listener;

        public CartHolder(@NonNull View itemView) {
            super(itemView);

            mImageCartItem = itemView.findViewById(R.id.img_item_cart);
            mImgIncreaseQuantity = itemView.findViewById(R.id.img_increase);
            mImgDecreaseQuantity = itemView.findViewById(R.id.img_decrease);
            mImgDeleteFromCart = itemView.findViewById(R.id.delete_from_cart);

            mTxtCartNameItem = itemView.findViewById(R.id.txt_name_item_cart);
            mTxtCartPriceItem = itemView.findViewById(R.id.txt_price_item_cart);
            mTxtCartQuantityItem = itemView.findViewById(R.id.txt_quantity_item_cart);

            // Event
            mImgDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onImageClickListener(v, getAdapterPosition(), true);
                }
            });

            mImgIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onImageClickListener(v, getAdapterPosition(), false);
                }
            });
        }

        public void setListener(IImageListener listener) {
            this.listener = listener;
        }
    }

    interface IImageListener{
        void onImageClickListener(View view, int position, boolean isDecrease);

    }
}

package com.ahmet.barberbooking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Databse.CartDatabase;
import com.ahmet.barberbooking.Databse.CartItem;
import com.ahmet.barberbooking.Databse.DatabaseUtils;
import com.ahmet.barberbooking.Interface.IRecyclerItemSelectedListener;
import com.ahmet.barberbooking.Model.Shopping;
import com.ahmet.barberbooking.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingHolder> {

    private Context mContext;
    private List<Shopping> mListShopping;
   // private LayoutInflater inflater;
    private CartDatabase cartDatabase;

    public ShoppingAdapter(Context mContext, List<Shopping> mListShopping) {
        this.mContext = mContext;
        this.mListShopping = mListShopping;
       // inflater = LayoutInflater.from(mContext);
        cartDatabase = CartDatabase.getInstance(mContext);
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

        // Add to cart
        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {

                CartItem cartItem = new CartItem();
                cartItem.setProductId(mListShopping.get(position).getId());
                cartItem.setProductName(mListShopping.get(position).getName());
                cartItem.setProductImage(mListShopping.get(position).getImage());
                cartItem.setProductQuantity(1);
                cartItem.setProductPrice(mListShopping.get(position).getPrice());
                cartItem.setUserPhone(Common.currentUser.getPhoneNumber());

                // Insert to database
                DatabaseUtils.insertToCart(cartDatabase, cartItem);
                Toast.makeText(mContext, "Added To Cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemNotSelected(int position) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mListShopping.size();
    }

    static class ShoppingHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageShoppingItem;
        TextView mTxtShoppingName, mTxtShoppingPrice, mTxtShoppingAddToCart;
        CardView mCardShopping;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public ShoppingHolder(@NonNull View itemView) {
            super(itemView);

            mImageShoppingItem = itemView.findViewById(R.id.img_shopping_item);
            mTxtShoppingName = itemView.findViewById(R.id.txt_name_shopping_item);
            mTxtShoppingPrice = itemView.findViewById(R.id.txt_price_shopping_item);
            mTxtShoppingAddToCart = itemView.findViewById(R.id.txt_shopping_add_cart);
            mCardShopping = itemView.findViewById(R.id.card_shopping);

            mTxtShoppingAddToCart.setOnClickListener(this);
        }

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}

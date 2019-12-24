package com.ahmet.barberbooking.SubActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.CartAdapter;
import com.ahmet.barberbooking.Databse.CartDatabase;
import com.ahmet.barberbooking.Databse.CartItem;
import com.ahmet.barberbooking.Databse.DatabaseUtils;
import com.ahmet.barberbooking.Interface.ICartItemLoadListener;
import com.ahmet.barberbooking.Interface.ICartItemUpdateListener;
import com.ahmet.barberbooking.Interface.ISumCartListener;
import com.ahmet.barberbooking.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartActivity extends AppCompatActivity implements ICartItemLoadListener, ICartItemUpdateListener, ISumCartListener {

    @BindView(R.id.recycler_cart)
    RecyclerView mRecyclerCart;
    @BindView(R.id.txt_total_price)
    TextView mTxtTotalPrice;
    @BindView(R.id.btn_clear_cart)
    Button mBtnClearCart;

    @OnClick(R.id.btn_clear_cart)
    void clearCart(){

        DatabaseUtils.clearCart(mCartDatabase);
        // Update adaoter
        DatabaseUtils.getAllItemFromCart(mCartDatabase, this);
        mTxtTotalPrice.setText("$ 0");
        Toast.makeText(this, "Cart empty", Toast.LENGTH_SHORT).show();
    }

    private CartDatabase mCartDatabase;

    private Long finalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart");


        ButterKnife.bind(this);

        mCartDatabase = CartDatabase.getInstance(this);

        DatabaseUtils.getAllItemFromCart(mCartDatabase, this);

        // init recyclerview
        mRecyclerCart.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL);
        mRecyclerCart.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerCart.addItemDecoration(new DividerItemDecoration(this, staggeredGridLayoutManager.getOrientation()));

        finalPrice = Long.valueOf(0);
        mTxtTotalPrice.setText(new StringBuilder("$ ").append(finalPrice));
    }

    @Override
    public void onLoadAllItemInCartSuccess(List<CartItem> mListCartItem) {
        // Here, after we get all cart item from Db
        // We will display by recycler view

        CartAdapter mCartAdapter = new CartAdapter(CartActivity.this, mListCartItem,this);
        mRecyclerCart.setAdapter(mCartAdapter);
    }

    @Override
    public void onCartItemUpdateSuccess() {

        DatabaseUtils.sumCart(mCartDatabase, this);

    }

    @Override
    public void onSumCartSuccess(Long value) {

        finalPrice = value;
        mTxtTotalPrice.setText(new StringBuilder("$ ").append(finalPrice));
    }
}

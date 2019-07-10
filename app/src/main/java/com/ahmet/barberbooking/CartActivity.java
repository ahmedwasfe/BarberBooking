package com.ahmet.barberbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmet.barberbooking.Adapter.CartAdapter;
import com.ahmet.barberbooking.Databse.CartDatabase;
import com.ahmet.barberbooking.Databse.CartItem;
import com.ahmet.barberbooking.Databse.DatabaseUtils;
import com.ahmet.barberbooking.Interface.ICartItemLoadListener;
import com.ahmet.barberbooking.Interface.ICartItemUpdateListener;
import com.ahmet.barberbooking.Interface.ISumCartListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements ICartItemLoadListener, ICartItemUpdateListener, ISumCartListener {

    @BindView(R.id.recycler_cart)
    RecyclerView mRecyclerCart;
    @BindView(R.id.txt_total_price)
    TextView mTxtTotalPrice;
    @BindView(R.id.btn_submit_cart)
    Button mBtnSubmitCart;

    private CartDatabase mCartDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ButterKnife.bind(this);

        mCartDatabase = CartDatabase.getInstance(this);

        DatabaseUtils.getAllItemFromCart(mCartDatabase, this);

        // init recyclerview
        mRecyclerCart.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL);
        mRecyclerCart.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerCart.addItemDecoration(new DividerItemDecoration(this, staggeredGridLayoutManager.getOrientation()));
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

        mTxtTotalPrice.setText(new StringBuilder("$ ").append(value));
    }
}

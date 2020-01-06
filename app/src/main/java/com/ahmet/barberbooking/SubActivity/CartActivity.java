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
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SaveSettings;
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

public class CartActivity extends AppCompatActivity implements ICartItemLoadListener,
        ICartItemUpdateListener, ISumCartListener {

    @BindView(R.id.recycler_cart)
    RecyclerView mRecyclerCart;
    @BindView(R.id.txt_total_price)
    TextView mTxtTotalPrice;
    @BindView(R.id.btn_clear_cart)
    Button mBtnClearCart;

    @OnClick(R.id.btn_clear_cart)
    void clearCart(){

        DatabaseUtils.clearCart(mCartDatabase);
        // Update adapter
        DatabaseUtils.getAllItemFromCart(mCartDatabase, this);
        mTxtTotalPrice.setText("$ 0");
        Toast.makeText(this, getString(R.string.cart_empty), Toast.LENGTH_SHORT).show();
    }

    private CartDatabase mCartDatabase;

    private Long finalPrice;

    private SaveSettings mSaveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mSaveSettings = new SaveSettings(this);

        if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_EN))
            Common.setLanguage(this, Common.KEY_LANGUAGE_EN);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_AR))
            Common.setLanguage(this, Common.KEY_LANGUAGE_AR);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_TR))
            Common.setLanguage(this, Common.KEY_LANGUAGE_TR);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_FR))
            Common.setLanguage(this,Common.KEY_LANGUAGE_FR);

        if (mSaveSettings.getNightModeState() == true)
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.AppTheme);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.cart);


        ButterKnife.bind(this);

        finalPrice = Long.valueOf(0);
        mTxtTotalPrice.setText(new StringBuilder("$ ").append(finalPrice));

        // init recyclerview and Database
         init();

    }

    @Override
    protected void onResume() {
        super.onResume();

        finalPrice = Long.valueOf(0);
        mTxtTotalPrice.setText(new StringBuilder("$ ").append(finalPrice));
    }

    @Override
    protected void onStart() {
        super.onStart();

        finalPrice = Long.valueOf(0);
        mTxtTotalPrice.setText(new StringBuilder("$ ").append(finalPrice));
    }

    private void init() {

        mCartDatabase = CartDatabase.getInstance(this);

        DatabaseUtils.getAllItemFromCart(mCartDatabase, this);


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

        finalPrice = value;
        mTxtTotalPrice.setText(new StringBuilder("$ ").append(finalPrice));
    }
}

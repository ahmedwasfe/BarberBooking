package com.ahmet.barberbooking.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.HomeSliderAdapter;
import com.ahmet.barberbooking.Adapter.LookBookAdapter;
import com.ahmet.barberbooking.BookingActivity;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Interface.IBannerLoadListener;
import com.ahmet.barberbooking.Interface.IBookingInfoLoadListener;
import com.ahmet.barberbooking.Interface.ILookBookLoadListener;
import com.ahmet.barberbooking.Model.Banner;
import com.ahmet.barberbooking.Model.BookingInformation;
import com.ahmet.barberbooking.Model.User;
import com.ahmet.barberbooking.R;
import com.ahmet.barberbooking.Service.LoadingImageService;
import com.facebook.accountkit.AccountKit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.LongFunction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ss.com.bannerslider.ImageLoadingService;
import ss.com.bannerslider.Slider;

public class HomeFragment extends Fragment implements IBannerLoadListener, ILookBookLoadListener, IBookingInfoLoadListener {

    private Unbinder mUnbinder;

    @BindView(R.id.linear_user_info)
    LinearLayout mLinearUserInfo;
    @BindView(R.id.txt_user_name)
    TextView mTxtUserName;
    @BindView(R.id.slider_banner)
    Slider mSliderBanner;
    @BindView(R.id.recycler_look_book)
    RecyclerView mRecyclerLookBook;

    @BindView(R.id.card_booking_information)
    CardView mCardBookingInfo;
    @BindView(R.id.txt_address_salon)
    TextView mTxtAddressSalon;
    @BindView(R.id.txt_time)
    TextView mTxtTime;
    @BindView(R.id.txt_salon_barber)
    TextView mTxtSalonBarber;
    @BindView(R.id.txt_time_remain)
    TextView mTxtTimeRemain;

    @OnClick(R.id.card_booking)
    void booking(){
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }


    // FireStore
    private CollectionReference mReferenceBanner, mReferenceLookBook;

    // Interface
    private IBannerLoadListener mIBannerLoadListener;
    private ILookBookLoadListener mILookBookLoadListener;
    private IBookingInfoLoadListener mIBookingInfoLoadListener;

    public HomeFragment() {

        mReferenceBanner = FirebaseFirestore.getInstance().collection("Banner");
        mReferenceLookBook = FirebaseFirestore.getInstance().collection("LookBook");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIBannerLoadListener = this;
        mILookBookLoadListener = this;
        mIBookingInfoLoadListener = this;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadUserBooking();
    }

    private void loadUserBooking() {

        // /User/+970592435704/Booking/H4InjDGyf4NsN6TPENGH
        CollectionReference mUserBookingReference = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking")
                .document("");

        // Get current data
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        // Select booking information from firebase database with done = false timestamp greater today
        mUserBookingReference.whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)  // Only take 1
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            if (!task.getResult().isEmpty()){

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){

                                    BookingInformation bookingInfo = documentSnapshot.toObject(BookingInformation.class);
                                    mIBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInfo);
                                    // Exit loop as soon as
                                }

                            } else {

                                mIBookingInfoLoadListener.onBookingInfoLoadEmpty();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                mIBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_home, container, false);

        mUnbinder = ButterKnife.bind(this, layoutView);

        // Init
        Slider.init(new LoadingImageService());

        return layoutView;
    }

    private void loadUserInfo() {

        mLinearUserInfo.setVisibility(View.VISIBLE);
        mTxtUserName.setText(Common.currentUser.getName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        User user = new User();

        // check if Logged ?
        if (AccountKit.getCurrentAccessToken() != null){
            //if (user.getName() != null){
                loadUserInfo();
               // loadBanner();
               // loadLookBook();
                loadUserBooking();
            //}

        }

    }

    private void loadLookBook() {

        mReferenceLookBook.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> mListLookBook = new ArrayList<>();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                Banner banner = snapshot.toObject(Banner.class);
                                mListLookBook.add(banner);
                            }
                            mILookBookLoadListener.onLoadLookBookSuccess(mListLookBook);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mILookBookLoadListener.onLoadLookBookFailed(e.getMessage());
            }
        });

    }

    private void loadBanner() {
        mReferenceBanner.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> mListBanner = new ArrayList<>();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                Banner banner = snapshot.toObject(Banner.class);
                                mListBanner.add(banner);
                            }
                            mIBannerLoadListener.onLoadBannerSuccess(mListBanner);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mIBannerLoadListener.onLoadBannerFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onLoadBannerSuccess(List<Banner> mListBanner) {
        mSliderBanner.setAdapter(new HomeSliderAdapter(mListBanner));
        mSliderBanner.setInterval(5000);
        mSliderBanner.setLoopSlides(true);
        mSliderBanner.setAnimateIndicators(true);

    }

    @Override
    public void onLoadBannerFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadLookBookSuccess(List<Banner> mListLookBook) {
        mRecyclerLookBook.setHasFixedSize(true);
        mRecyclerLookBook.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL));
        mRecyclerLookBook.setAdapter(new LookBookAdapter(getActivity(), mListLookBook));
    }

    @Override
    public void onLoadLookBookFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInfo) {

        mTxtAddressSalon.setText(bookingInfo.getSalonAddress());
        mTxtTime.setText(bookingInfo.getTime());
        mTxtSalonBarber.setText(bookingInfo.getBarberName());

        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInfo.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(), 0).toString();

        mTxtTimeRemain.setText(dateRemain);

        mCardBookingInfo.setVisibility(View.VISIBLE);


    }

    @Override
    public void onBookingInfoLoadFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        mCardBookingInfo.setVisibility(View.GONE);
    }
}

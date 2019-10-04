package com.ahmet.barberbooking.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.barberbooking.Model.BookingInformation;
import com.ahmet.barberbooking.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    private Context mContext;
    private List<BookingInformation> mListBookingInfo;
    private LayoutInflater inflater;

    public HistoryAdapter(Context mContext, List<BookingInformation> mListBookingInfo) {
        this.mContext = mContext;
        this.mListBookingInfo = mListBookingInfo;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = inflater.inflate(R.layout.raw_history, parent, false);
        return new HistoryHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {

        BookingInformation bookingInformation = mListBookingInfo.get(position);


        if (bookingInformation.getTime().contains("-")){
            String date = bookingInformation.getTime().substring(16,27);
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/DD/yyyy");

            holder.mTxtDate.setText(date);
        }


        holder.mTxtSalonName.setText(bookingInformation.getSalonName());
        holder.mTxtSalonAddress.setText(bookingInformation.getSalonAddress());
        holder.mTxtTimeBooking.setText(bookingInformation.getTime());
        holder.mTxtBarberName.setText(bookingInformation.getBarberName());
    }

//    private String getDate(Timestamp time) {
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(Long.parseLong(time.compareTo(time)));
//        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
//        return date;
//    }

    @Override
    public int getItemCount() {
        return mListBookingInfo.size();
    }

    static class HistoryHolder extends RecyclerView.ViewHolder{

        private Unbinder mUnbinder;

        @BindView(R.id.txt_booking_date_history)
        TextView mTxtDate;
        @BindView(R.id.txt_salon_name_history)
        TextView mTxtSalonName;
        @BindView(R.id.txt_salon_address_history)
        TextView mTxtSalonAddress;
        @BindView(R.id.txt_booking_time_history)
        TextView mTxtTimeBooking;
        @BindView(R.id.txt_barber_name_history)
        TextView mTxtBarberName;




        public HistoryHolder(@NonNull View itemView) {
            super(itemView);

            mUnbinder = ButterKnife.bind(this, itemView);


        }
    }
}

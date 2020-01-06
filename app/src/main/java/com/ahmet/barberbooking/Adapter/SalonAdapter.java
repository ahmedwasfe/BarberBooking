package com.ahmet.barberbooking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SaveSettings;
import com.ahmet.barberbooking.Interface.IRecyclerItemSelectedListener;
import com.ahmet.barberbooking.Model.EventBus.EnableNextButton;
import com.ahmet.barberbooking.Model.Salon;
import com.ahmet.barberbooking.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

public class SalonAdapter extends RecyclerView.Adapter<SalonAdapter.SalonHolder> {

    private Context mContext;

    private List<Salon> mListSalon;
    private List<CardView> mListCard;

    private LayoutInflater inflater;

    private SaveSettings mSaveSettings;

   // LocalBroadcastManager mLocalBroadcastManager;

    public SalonAdapter(Context mContext, List<Salon> mListSalon) {
        this.mContext = mContext;

        this.mListSalon = mListSalon;
        mListCard = new ArrayList<>();

        inflater = LayoutInflater.from(mContext);

        mSaveSettings = new SaveSettings(mContext);

     //   mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
    }

    @NonNull
    @Override
    public SalonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = inflater.inflate(R.layout.raw_salon, parent, false);

        return new SalonHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull SalonHolder holder, int position) {

        holder.mTxtSalonName.setText(Common.formatName(mListSalon.get(position).getName()));
        holder.mTxtSalonAddress.setText((mListSalon.get(position).getCity()));

        if (!mListCard.contains(holder.mCardSalon)){
            mListCard.add(holder.mCardSalon);
        }

            holder.mCardSalon.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
            holder.mTxtSalonName.setTextColor(mContext.getResources().getColor(R.color.colorButton));
            holder.mTxtSalonAddress.setTextColor(mContext.getResources().getColor(R.color.colorButton));

        holder.setItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {


                // Set White background color for all card not be selected
                for (CardView cardView : mListCard)
                        cardView.setCardBackgroundColor(mContext.getResources()
                                .getColor(R.color.colorWhite));

//                if (mSaveSettings.getNightModeState() == true){
                    // Set Selected BG for only selected item Dark Mode
                    holder.mCardSalon.setCardBackgroundColor(mContext.getResources()
                            .getColor(R.color.colorPrimary));

//                    holder.mTxtSalonName.setTextColor(mContext.getResources()
//                            .getColor(R.color.colorWhite));
//
//                    holder.mTxtSalonAddress.setTextColor(mContext.getResources()
//                            .getColor(R.color.colorWhite));
//                }else{
//                    // Set Selected BG for only selected item Light Mode
//                    holder.mCardSalon.setCardBackgroundColor(mContext.getResources()
//                            .getColor(R.color.colorPrimary));
//
//                    holder.mTxtSalonName.setTextColor(mContext.getResources()
//                            .getColor(R.color.colorWhite));
//
//                    holder.mTxtSalonAddress.setTextColor(mContext.getResources()
//                            .getColor(R.color.colorWhite));
//                }



//                if (!view.isSelected()){
//                    if (mSaveSettings.getNightModeState() == true){
//                        holder.mTxtSalonName.setTextColor(mContext.getResources()
//                                .getColor(R.color.colorWhite));
//
//                        holder.mTxtSalonAddress.setTextColor(mContext.getResources()
//                                .getColor(R.color.colorWhite));
//                    }else{
//                        holder.mTxtSalonName.setTextColor(mContext.getResources()
//                                .getColor(R.color.colorWhite));
//
//                        holder.mTxtSalonAddress.setTextColor(mContext.getResources()
//                                .getColor(R.color.colorWhite));
//                    }
//
//                }

                /*
                 * Send Broadcastto
                * Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                * intent.putExtra(Common.KEY_SALON_STORE, mListSalon.get(position));
                * intent.putExtra(Common.KEY_STEP, 1);
                * mLocalBroadcastManager.sendBroadcast(intent);
                */

                // Send Event Bus to tell Booking activity enable Button Next
                // Event Bus
                EventBus.getDefault().postSticky(new EnableNextButton(1, mListSalon.get(position)));
            }

            @Override
            public void onItemNotSelected(int position) {

                holder.mTxtSalonName.setTextColor(mContext.getResources()
                        .getColor(R.color.colorBlack));
                holder.mTxtSalonAddress.setTextColor(mContext.getResources()
                        .getColor(R.color.colorBlack));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mListSalon.size();
    }



    static class SalonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTxtSalonName, mTxtSalonAddress;
        CardView mCardSalon;

        IRecyclerItemSelectedListener mIRecyclerItemSelectedListener;

        public SalonHolder(@NonNull View itemView) {
            super(itemView);

            mTxtSalonName = itemView.findViewById(R.id.txt_salon_name);
            mTxtSalonAddress = itemView.findViewById(R.id.txt_salon_address);
            mCardSalon = itemView.findViewById(R.id.card_salon);

            itemView.setOnClickListener(this);
        }

        public void setItemSelectedListener(IRecyclerItemSelectedListener mIRecyclerItemSelectedListener) {
            this.mIRecyclerItemSelectedListener = mIRecyclerItemSelectedListener;
        }



        @Override
        public void onClick(View view) {
            mIRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
            mIRecyclerItemSelectedListener.onItemNotSelected(getAdapterPosition());
        }

    }
}

package com.ahmet.barberbooking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Interface.IRecyclerItemSelectedListener;
import com.ahmet.barberbooking.Model.Salon;
import com.ahmet.barberbooking.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class SalonAdapter extends RecyclerView.Adapter<SalonAdapter.SalonHolder> {

    private Context mContext;
    private List<Salon> mListSalon;
    private List<CardView> mListCard;
    private LayoutInflater inflater;

    LocalBroadcastManager mLocalBroadcastManager;

    public SalonAdapter(Context mContext, List<Salon> mListSalon) {
        this.mContext = mContext;
        this.mListSalon = mListSalon;
        inflater = LayoutInflater.from(mContext);
        mListCard = new ArrayList<>();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
    }

    @NonNull
    @Override
    public SalonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = inflater.inflate(R.layout.raw_salon, parent, false);

        return new SalonHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull SalonHolder holder, int position) {

        holder.mTxtSalonName.setText(mListSalon.get(position).getName());
        holder.mTxtSalonAddress.setText(mListSalon.get(position).getAddress());

        if (!mListCard.contains(holder.mCardSalon)){
            mListCard.add(holder.mCardSalon);
        }



        holder.setItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {


                // Set White background color for all card not be selected
                for (CardView cardView : mListCard){
                    cardView.setCardBackgroundColor(mContext.getResources()
                            .getColor(R.color.colorWhite));
                }

                // Set Selected BG for only selected item
                holder.mCardSalon.setCardBackgroundColor(mContext.getResources()
                        .getColor(R.color.colorPrimary));
                holder.mTxtSalonName.setTextColor(mContext.getResources()
                        .getColor(R.color.colorWhite));
                holder.mTxtSalonAddress.setTextColor(mContext.getResources()
                        .getColor(R.color.colorWhite));

                if (!view.isSelected()){
                    holder.mTxtSalonName.setTextColor(mContext.getResources()
                            .getColor(R.color.colorBlack));
                    holder.mTxtSalonAddress.setTextColor(mContext.getResources()
                            .getColor(R.color.colorBlack));
                }

                // Send Broadcastto to tell Booking activity enable Button Next
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_SALON_STORE, mListSalon.get(position));
                intent.putExtra(Common.KEY_STEP, 1);
                mLocalBroadcastManager.sendBroadcast(intent);
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

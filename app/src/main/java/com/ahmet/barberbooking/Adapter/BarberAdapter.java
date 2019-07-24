package com.ahmet.barberbooking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Interface.IRecyclerItemSelectedListener;
import com.ahmet.barberbooking.Model.Barber;
import com.ahmet.barberbooking.Model.EventBus.EnableNextButton;
import com.ahmet.barberbooking.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.BarberHolder> {


    private Context mContext;
    private List<Barber> mListBarbers;
    private LayoutInflater inflater;
    private List<CardView> mListCard;

  //  LocalBroadcastManager mLocalBroadcastManager;

    public BarberAdapter(Context mContext, List<Barber> mListBarbers) {
        this.mContext = mContext;
        this.mListBarbers = mListBarbers;
        inflater = LayoutInflater.from(mContext);

        mListCard = new ArrayList<>();

    //    mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
    }

    @NonNull
    @Override
    public BarberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = inflater.inflate(R.layout.raw_barber, parent, false);

        return new BarberHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberHolder holder, int position) {

        holder.mTxtBarberName.setText(mListBarbers.get(position).getName());

        if (mListBarbers.get(position).getRatingTimes() !=null)
                 holder.mRatingBarber.setRating( mListBarbers.get(position).getRating().floatValue() /
                                        mListBarbers.get(position).getRatingTimes());
        else
            holder.mRatingBarber.setRating(0);

        if (!mListCard.contains(holder.mCardBarber))
            mListCard.add(holder.mCardBarber);

        holder.setmIRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {

                // Set backgrounf for all items not selected
                for (CardView cardView : mListCard){
                    cardView.setCardBackgroundColor(
                            mContext.getResources().getColor(R.color.colorWhite));
                }

                // Set background for selected
                holder.mCardBarber.setCardBackgroundColor(
                        mContext.getResources().getColor(R.color.colorPrimary));

                /*
                * Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                * intent.putExtra(Common.KEY_BARBER_SELECTED, mListBarbers.get(position));
                * intent.putExtra(Common.KEY_STEP, 2);
                * mLocalBroadcastManager.sendBroadcast(intent);
                */

                // Send Event Bus to enable button next
                // Event Bus
                EventBus.getDefault()
                        .postSticky(new EnableNextButton(2, mListBarbers.get(position)));
            }

            @Override
            public void onItemNotSelected(int position) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mListBarbers.size();
    }

    static class BarberHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTxtBarberName;
        RatingBar mRatingBarber;
        CardView mCardBarber;

        private IRecyclerItemSelectedListener mIRecyclerItemSelectedListener;

        public BarberHolder(@NonNull View itemView) {
            super(itemView);

            mTxtBarberName = itemView.findViewById(R.id.txt_barber_name);
            mRatingBarber = itemView.findViewById(R.id.rating_barber);
            mCardBarber = itemView.findViewById(R.id.card_barber);

            itemView.setOnClickListener(this);
        }

        public void setmIRecyclerItemSelectedListener(IRecyclerItemSelectedListener mIRecyclerItemSelectedListener) {
            this.mIRecyclerItemSelectedListener = mIRecyclerItemSelectedListener;
        }

        @Override
        public void onClick(View v) {
            mIRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}

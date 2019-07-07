package com.ahmet.barberbooking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmet.barberbooking.Model.Banner;
import com.ahmet.barberbooking.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

public class LookBookAdapter extends RecyclerView.Adapter<LookBookAdapter.LookBookHolder> {

    private Context mContext;
    private List<Banner> mListLookBook;
    private LayoutInflater inflater;

    public LookBookAdapter(Context mContext, List<Banner> mListLookBook) {
        this.mContext = mContext;
        this.mListLookBook = mListLookBook;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public LookBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = inflater.inflate(R.layout.raw_look_book, parent, false);

        return new LookBookHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull LookBookHolder holder, int position) {

        Picasso.get()
                .load(mListLookBook.get(position).getImage())
                .placeholder(R.color.colorButton)
                .into(holder.mImageLookBook);

    }

    @Override
    public int getItemCount() {
        return mListLookBook.size();
    }

    static class LookBookHolder extends RecyclerView.ViewHolder{

        AppCompatImageView mImageLookBook;

        public LookBookHolder(@NonNull View itemView) {
            super(itemView);

            mImageLookBook = itemView.findViewById(R.id.image_look_book);
        }
    }
}

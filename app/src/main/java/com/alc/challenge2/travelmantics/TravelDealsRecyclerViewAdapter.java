package com.alc.challenge2.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.alc.challenge2.travelmantics.models.TravelDealModel;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TravelDealsRecyclerViewAdapter extends RecyclerView.Adapter<TravelDealsRecyclerViewAdapter.ViewHolder>{

    private final static String TAG = "TDRecyclerViewAdapter";
    private List<TravelDealModel> mTravelDealsList;
    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public TravelDealsRecyclerViewAdapter(List<TravelDealModel> list) {

        this.mTravelDealsList = list;

        //FirebaseUtil.openFbReference("traveldeals");
//        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
//        mDatabaseReference = FirebaseUtil.mDatabaseReference;
//        this.mTravelDealsList = FirebaseUtil.mDeals;
//        mChildListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                TravelDealModel td = dataSnapshot.getValue(TravelDealModel.class);
//                Log.d("Deal: ", td.getTitle());
//                td.setId(dataSnapshot.getKey());
//                mTravelDealsList.add(td);
//                notifyItemInserted(mTravelDealsList.size()-1);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        mDatabaseReference.addChildEventListener(mChildListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: creating view holder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_deals_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelDealModel deal = mTravelDealsList.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return mTravelDealsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private AppCompatImageView mImageView;
        private AppCompatTextView mTitle, mDescription, mPrice;

        public ViewHolder(View itemView){
            super(itemView);
            mImageView = itemView.findViewById(R.id.rv_image_view);
            mTitle = itemView.findViewById(R.id.rv_tv_title);
            mDescription = itemView.findViewById(R.id.rv_tv_description);
            mPrice = itemView.findViewById(R.id.rv_tv_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            TravelDealModel selectedDeal = mTravelDealsList.get(position);
            Intent intent = new Intent(view.getContext(), DealActivity.class);
            intent.putExtra("Travel_Deal", selectedDeal);
            view.getContext().startActivity(intent);
        }

        public void bind(TravelDealModel deal) {
            mTitle.setText(deal.getTitle());
            mDescription.setText(deal.getDescription());
            mPrice.setText(String.format("ZAR %s", deal.getPrice()));
            showImage(deal.getImageUrl());

        }

        private void showImage(String url) {
            if (url != null && !url.isEmpty()) {
                Glide.with(mImageView.getContext())
                        .load(url)
                        .override(750,750)
                        .centerCrop()
                        .into(mImageView);
            }
        }
    }
}


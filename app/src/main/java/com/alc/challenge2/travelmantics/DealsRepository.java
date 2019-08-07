package com.alc.challenge2.travelmantics;

import android.util.Log;

import com.alc.challenge2.travelmantics.models.TravelDealModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class DealsRepository {

    private static final String TAG = "DealsRepository";
    private FirebaseDatabase mFirebaseDatabase;

    public DealsRepository() {
        Log.d(TAG, "DealsRepository: constructor");
        mFirebaseDatabase = FirebaseDatabase.getInstance();

    }

    public RealtimeDatabaseLiveData<TravelDealModel> getTravelDeals(final String referencePath){

        final DatabaseReference reference = mFirebaseDatabase.getReference(referencePath);

        return new RealtimeDatabaseLiveData<>(reference, TravelDealModel.class);
    }

}


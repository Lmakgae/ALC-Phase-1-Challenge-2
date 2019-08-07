package com.alc.challenge2.travelmantics;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RealtimeDatabaseLiveData<T> extends LiveData<List<T>> implements ValueEventListener {

    private static final String TAG = "RealtimeDatabaseLiveDat";
    private final Class<T> type;
    private DatabaseReference mDatabaseReference;

    public RealtimeDatabaseLiveData(DatabaseReference databaseReference, Class<T> type) {
        this.mDatabaseReference = databaseReference;
        this.type = type;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.d(TAG, "onDataChange: datasnapshot is received");

        setValue(dataSnapshotToList(dataSnapshot));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        // Getting Deals failed, log a message
    }

    @Override
    protected void onActive() {
        super.onActive();
        Log.d(TAG, "onActive: ValueEventListener being removed");
        mDatabaseReference.addValueEventListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Log.d(TAG, "onInactive: ValueEventListener being removed");
        mDatabaseReference.removeEventListener(this);
    }

    @NonNull
    private List<T> dataSnapshotToList(DataSnapshot dataSnapshot) {
        final List<T> list = new ArrayList<>();
        if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
            Log.d(TAG, "dataSnapshotToList: snapshot is empty");
            return list;
        }

        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

            list.add(postSnapshot.getValue(type));

        }

        return list;
    }
}

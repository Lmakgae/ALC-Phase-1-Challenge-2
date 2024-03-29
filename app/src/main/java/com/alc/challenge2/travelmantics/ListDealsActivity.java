package com.alc.challenge2.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.alc.challenge2.travelmantics.models.TravelDealModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ListDealsActivity extends AppCompatActivity {

    private ListDealsViewModel viewModel;
    private TravelDealsRecyclerViewAdapter travelDealsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_deals);

        viewModel = ViewModelProviders.of(this).get(ListDealsViewModel.class);

        RecyclerView rvTravelDeals = findViewById(R.id.rv_traveldeals);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvTravelDeals.setLayoutManager(linearLayoutManager);

        viewModel.getTravelDealModelLiveData().observe(this, travelDealModels -> {
            if(!travelDealModels.isEmpty()){
                travelDealsRecyclerViewAdapter = new TravelDealsRecyclerViewAdapter(travelDealModels);
                rvTravelDeals.setAdapter(travelDealsRecyclerViewAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.isAdmin) {
            insertMenu.setVisible(true);
        }
        else {
            insertMenu.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User Logged Out");
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFbReference(getString(R.string.path_database_travel_deals), this);

//        RecyclerView rvTravelDeals = findViewById(R.id.rv_traveldeals);
//        final TravelDealsRecyclerViewAdapter travelDealsRecyclerViewAdapter = new TravelDealsRecyclerViewAdapter();
//
//        rvTravelDeals.setAdapter(travelDealsRecyclerViewAdapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
//        rvTravelDeals.setLayoutManager(linearLayoutManager);
        FirebaseUtil.attachListener();
    }

    public void showMenu() {
        invalidateOptionsMenu();
    }

}

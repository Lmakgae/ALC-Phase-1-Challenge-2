package com.alc.challenge2.travelmantics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.alc.challenge2.travelmantics.models.TravelDealModel;

import java.util.List;

public class ListDealsViewModel extends ViewModel {

    private MutableLiveData<String> referencePath = new MutableLiveData<>();
    private LiveData<List<TravelDealModel>> travelDealModelLiveData;
    private DealsRepository dealsRepository;

    public ListDealsViewModel(){
        this.dealsRepository = new DealsRepository();
        referencePath.setValue("travel_deals");

    }

    public LiveData<List<TravelDealModel>> getTravelDealModelLiveData() {
        if(travelDealModelLiveData == null){

            travelDealModelLiveData = Transformations.switchMap(referencePath, dealsRepository::getTravelDeals);
        }
        return travelDealModelLiveData;
    }

}

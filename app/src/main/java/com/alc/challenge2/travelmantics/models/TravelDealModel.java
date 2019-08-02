package com.alc.challenge2.travelmantics.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelDealModel implements Parcelable {

    private String id;
    private String title;
    private String description;
    private String price;
    private String imageUrl;
    private String imageName;

    public TravelDealModel(String id, String title, String description, String price, String imageUrl, String imageName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }

    public TravelDealModel() {
    }

    protected TravelDealModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        price = in.readString();
        imageUrl = in.readString();
        imageName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(imageUrl);
        dest.writeString(imageName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TravelDealModel> CREATOR = new Creator<TravelDealModel>() {
        @Override
        public TravelDealModel createFromParcel(Parcel in) {
            return new TravelDealModel(in);
        }

        @Override
        public TravelDealModel[] newArray(int size) {
            return new TravelDealModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

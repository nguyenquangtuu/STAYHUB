package com.stayhub.app.data.model.request;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * Request payload cho tạo mới hoặc cập nhật Homestay
 */
public class HomestayRequest implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("city")
    private String city;

    @SerializedName("address")
    private String address;

    @SerializedName("description")
    private String description;

    @SerializedName("amenities")
    private List<String> amenities;

    public HomestayRequest() {
    }

    public HomestayRequest(String name, String city, String address, String description, List<String> amenities) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.description = description;
        this.amenities = amenities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
}

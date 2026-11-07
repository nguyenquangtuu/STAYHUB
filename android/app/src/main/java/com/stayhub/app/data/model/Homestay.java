package com.stayhub.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model Homestay nghỉ dưỡng
 */
public class Homestay implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("city")
    private String city;

    @SerializedName("address")
    private String address;

    @SerializedName("amenities")
    private List<String> amenities = new ArrayList<>();

    @SerializedName("avg_rating")
    private double avgRating;

    @SerializedName("min_room_price")
    private Double minRoomPrice;

    @SerializedName("total_rooms")
    private int totalRooms;

    @SerializedName("rooms")
    private List<Room> rooms = new ArrayList<>();

    @SerializedName("owner_name")
    private String ownerName;

    @SerializedName("owner_phone")
    private String ownerPhone;

    // URL ảnh giả lập hoặc tải theo ID
    private String imageUrl;

    public Homestay() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getAmenities() {
        return amenities != null ? amenities : new ArrayList<>();
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public double getMinRoomPrice() {
        return minRoomPrice != null ? minRoomPrice : 0.0;
    }

    public void setMinRoomPrice(Double minRoomPrice) {
        this.minRoomPrice = minRoomPrice;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public List<Room> getRooms() {
        return rooms != null ? rooms : new ArrayList<>();
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    /**
     * Trả về URL ảnh chất lượng cao theo từng ID homestay để hiển thị qua Glide
     */
    public String getImageUrl() {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        // Ảnh homestay demo theo ID
        switch (id % 3) {
            case 1:
                return "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&auto=format&fit=crop&q=80";
            case 2:
                return "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&auto=format&fit=crop&q=80";
            default:
                return "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800&auto=format&fit=crop&q=80";
        }
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

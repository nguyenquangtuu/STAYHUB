package com.stayhub.app.data.api;

import com.stayhub.app.data.model.ApiResponse;
import com.stayhub.app.data.model.Booking;
import com.stayhub.app.data.model.Homestay;
import com.stayhub.app.data.model.Review;
import com.stayhub.app.data.model.Room;
import com.stayhub.app.data.model.User;
import com.stayhub.app.data.model.request.BookingRequest;
import com.stayhub.app.data.model.request.LoginRequest;
import com.stayhub.app.data.model.request.RegisterRequest;
import com.stayhub.app.data.model.request.ReviewRequest;
import com.stayhub.app.data.model.response.AuthResponse;
import com.stayhub.app.data.model.response.AvailabilityResponse;
import com.stayhub.app.data.model.response.BookingListResponse;
import com.stayhub.app.data.model.response.HomestayListResponse;
import com.stayhub.app.data.model.response.ReviewListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface định nghĩa toàn bộ endpoints RESTful kết nối Backend StayHub
 */
public interface ApiService {

    // 1. Authentication
    @POST("auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @GET("auth/me")
    Call<ApiResponse<User>> getMe();

    // 2. Homestay
    @GET("homestays")
    Call<ApiResponse<HomestayListResponse>> getHomestays(
            @Query("city") String city,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("guests") Integer guests,
            @Query("amenities") String amenities,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("homestays/{id}")
    Call<ApiResponse<Homestay>> getHomestayDetail(@Path("id") int id);

    @GET("homestays/{id}/rooms")
    Call<ApiResponse<List<Room>>> getHomestayRooms(@Path("id") int id);

    // 3. Room & Availability
    @GET("rooms/{id}")
    Call<ApiResponse<Room>> getRoomDetail(@Path("id") int id);

    @GET("rooms/{id}/availability")
    Call<ApiResponse<AvailabilityResponse>> checkRoomAvailability(
            @Path("id") int id,
            @Query("checkIn") String checkIn,
            @Query("checkOut") String checkOut
    );

    // 4. Booking
    @POST("bookings")
    Call<ApiResponse<Booking>> createBooking(@Body BookingRequest request);

    @GET("bookings/my")
    Call<ApiResponse<BookingListResponse>> getMyBookings(
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("bookings/{id}")
    Call<ApiResponse<Booking>> getBookingDetail(@Path("id") int id);

    @PATCH("bookings/{id}/cancel")
    Call<ApiResponse<Booking>> cancelBooking(@Path("id") int id);

    // 5. Review
    @POST("reviews")
    Call<ApiResponse<Review>> createReview(@Body ReviewRequest request);

    @GET("homestays/{id}/reviews")
    Call<ApiResponse<ReviewListResponse>> getHomestayReviews(
            @Path("id") int id,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );
}


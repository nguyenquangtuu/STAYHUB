package com.stayhub.app.data.api;

import com.stayhub.app.data.model.ApiResponse;
import com.stayhub.app.data.model.Booking;
import com.stayhub.app.data.model.Homestay;
import com.stayhub.app.data.model.Review;
import com.stayhub.app.data.model.Room;
import com.stayhub.app.data.model.User;
import com.stayhub.app.data.model.request.BookingRequest;
import com.stayhub.app.data.model.request.CompleteRegistrationRequest;
import com.stayhub.app.data.model.request.HomestayRequest;
import com.stayhub.app.data.model.request.LoginRequest;
import com.stayhub.app.data.model.request.RegisterRequest;
import com.stayhub.app.data.model.request.ReviewRequest;
import com.stayhub.app.data.model.request.RoomRequest;
import com.stayhub.app.data.model.request.SendOtpRequest;
import com.stayhub.app.data.model.request.UpdateBookingStatusRequest;
import com.stayhub.app.data.model.request.UpdateProfileRequest;
import com.stayhub.app.data.model.request.VerifyOtpRequest;
import com.stayhub.app.data.model.response.AuthResponse;
import com.stayhub.app.data.model.response.AvailabilityResponse;
import com.stayhub.app.data.model.response.BookingListResponse;
import com.stayhub.app.data.model.response.HomestayListResponse;
import com.stayhub.app.data.model.response.ReviewListResponse;
import com.stayhub.app.data.model.response.SendOtpResponse;
import com.stayhub.app.data.model.response.VerifyOtpResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface định nghĩa toàn bộ endpoints RESTful kết nối Backend StayHub
 */
public interface ApiService {

    // 1. Authentication (Xác thực một chạm OTP + Truyền thống)
    @POST("auth/send-otp")
    Call<ApiResponse<SendOtpResponse>> sendOtp(@Body SendOtpRequest request);

    @POST("auth/verify-otp")
    Call<ApiResponse<VerifyOtpResponse>> verifyOtp(@Body VerifyOtpRequest request);

    @POST("auth/complete-registration")
    Call<ApiResponse<AuthResponse>> completeRegistration(@Body CompleteRegistrationRequest request);

    @POST("auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @GET("auth/me")
    Call<ApiResponse<User>> getMe();

    @PUT("auth/profile")
    Call<ApiResponse<User>> updateProfile(@Body UpdateProfileRequest request);

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

    @POST("homestays")
    Call<ApiResponse<Homestay>> createHomestay(@Body HomestayRequest request);

    @PUT("homestays/{id}")
    Call<ApiResponse<Homestay>> updateHomestay(@Path("id") int id, @Body HomestayRequest request);

    @DELETE("homestays/{id}")
    Call<ApiResponse<Void>> deleteHomestay(@Path("id") int id);

    // 3. Room & Availability
    @GET("rooms/{id}")
    Call<ApiResponse<Room>> getRoomDetail(@Path("id") int id);

    @POST("homestays/{id}/rooms")
    Call<ApiResponse<Room>> createRoom(@Path("id") int homestayId, @Body RoomRequest request);

    @PUT("rooms/{id}")
    Call<ApiResponse<Room>> updateRoom(@Path("id") int id, @Body RoomRequest request);

    @DELETE("rooms/{id}")
    Call<ApiResponse<Void>> deleteRoom(@Path("id") int id);

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

    @DELETE("reviews/{id}")
    Call<ApiResponse<Void>> deleteReview(@Path("id") int id);

    // 6. Admin Endpoints
    @GET("admin/bookings")
    Call<ApiResponse<BookingListResponse>> getAdminBookings(
            @Query("status") String status,
            @Query("homestayId") Integer homestayId,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @PATCH("bookings/{id}/status")
    Call<ApiResponse<Booking>> updateBookingStatus(
            @Path("id") int id,
            @Body UpdateBookingStatusRequest request
    );
}


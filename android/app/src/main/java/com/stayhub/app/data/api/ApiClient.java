package com.stayhub.app.data.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stayhub.app.BuildConfig;
import com.stayhub.app.StayHubApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton cấu hình Retrofit kết nối tới Backend StayHub
 */
public class ApiClient {

    private static final int TIMEOUT_SECONDS = 30;
    private static ApiClient instance;
    private final ApiService apiService;

    private ApiClient() {
        // Logging Interceptor ở chế độ Debug
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        // Khởi tạo OkHttpClient với AuthInterceptor và Timeout 30 giây
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor);

        if (StayHubApplication.getInstance() != null) {
            httpClientBuilder.addInterceptor(
                    new AuthInterceptor(StayHubApplication.getInstance().getSessionManager())
            );
        }

        OkHttpClient client = httpClientBuilder.build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}

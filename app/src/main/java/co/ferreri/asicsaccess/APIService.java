package co.ferreri.asicsaccess;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface APIService {

    @POST("guests/since")
    Call<ArrayList<Guest>> loadAllGuestsSince(@Body LastUpdated lastUpdatedAt);

    @POST("logs/since")
    Call<ArrayList<GuestLog>> loadAllGuestLogsSince(@Body LastCreated lastCreated);

    @POST("logs/other")
    Call<ArrayList<GuestLog>> loadOtherGuestLogs(@Body LastCreated lastCreated);

    @POST("logs/log")
    Call<Void> sendLogs(@Body LogList logs);
}

interface APISendName{
    @POST("logs/log")
    Call<Void> sendLogs(@Body LogList logs);
}

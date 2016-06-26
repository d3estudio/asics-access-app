package co.ferreri.asicsaccess;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface APIService {

    @POST("guests/since")
    Call<ArrayList<Guest>> loadAllGuestsSince(@Body LastUpdated lastUpdatedAt);

    @POST("logs/log")
    Call<Void> sendLogs(@Body LogList logs);
}

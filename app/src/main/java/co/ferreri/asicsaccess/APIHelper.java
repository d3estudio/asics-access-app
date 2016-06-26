package co.ferreri.asicsaccess;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.DateTime;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHelper {
    private static String LAST_SENT_KEY = "LAST_SENT_KEY";
    private static String BASE_URL = "http://10.0.0.105:8080";
    private static String API_URL = BASE_URL + "/api/gateway/";

    private DatabaseHelper db;

    private Context context;

    private APIService apiService;

    public APIHelper(Context context) {
        this.context = context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(APIService.class);

        db = new DatabaseHelper(context);
    }

    public void loadAllGuestsSince() {
        LastUpdated lastUpdated = new LastUpdated(db.getLastUpdatedDate());
        apiService.loadAllGuestsSince(lastUpdated).enqueue(new Callback<ArrayList<Guest>>() {
            @Override
            public void onResponse(Call<ArrayList<Guest>> call, retrofit2.Response<ArrayList<Guest>> response) {
                if (response.body() != null)
                    db.addOrUpdateGuests(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Guest>> call, Throwable t) {
                Log.e("API","LOAD ALL GUESTS FAILURE "+t.getLocalizedMessage());
            }
        });
    }

    public void sendGuestLogsApi() {

        final String date = new DateTime().toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String lastSent = PreferenceManager.getDefaultSharedPreferences(context).getString(LAST_SENT_KEY, "empty");

        if (lastSent.equals("empty"))
            lastSent = new DateTime().withYear(2000).toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        ArrayList<GuestLog> list = db.getAllGuestLogsSince(lastSent);
        LogList logs = new LogList(list);


        if (!list.isEmpty())
            apiService.sendLogs(logs).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    response.code();
                    if (response.code() == 200)
                        PreferenceManager.getDefaultSharedPreferences(context)
                                .edit()
                                .putString(LAST_SENT_KEY, date)
                                .commit();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("API","SEND ALL GUESTS LOGS FAILURE "+t.getLocalizedMessage());
                }
            });
        else
            Log.i("API","LOG LIST IS EMPTY "+list.size());

    }

}

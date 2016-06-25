package co.ferreri.asicsaccess;

import android.content.Context;
import android.preference.PreferenceManager;

import org.joda.time.DateTime;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHelper2 {
    private static String LAST_SENT_KEY = "LAST_SENT_KEY";
    private static String BASE_URL = "http://10.0.1.59:8080";
    private static String API_URL = BASE_URL + "/api/gateway";

    private DatabaseHelper db;

    private Context context;

    private APIService apiService;

    public APIHelper2(Context context) {
        this.context = context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.1.59:8080/api/gateway/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(APIService.class);

        db = new DatabaseHelper(context);
    }

    public void loadAllGuestsSince() {
        System.out.println("ALO TMPO ______________##########_______ " + db.getLastUpdatedDate());
        LastUpdated lastUpdated = new LastUpdated(db.getLastUpdatedDate());
        apiService.loadAllGuestsSince(lastUpdated).enqueue(new Callback<ArrayList<Guest>>() {
            @Override
            public void onResponse(Call<ArrayList<Guest>> call, retrofit2.Response<ArrayList<Guest>> response) {
                System.out.println("SUCCESS RESPONSE SINCE ________________________ " + response.body());
                if (response.body() != null)
                    db.addOrUpdateGuests(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Guest>> call, Throwable t) {
                System.out.println("FAILURE THROW ________________________ " + t.getLocalizedMessage());
            }
        });
    }

    public void sendGuestLogsApi() {

        final DateTime date = new DateTime();

        String lastSent = PreferenceManager.getDefaultSharedPreferences(context).getString(LAST_SENT_KEY, "empty");

        if (lastSent.equals("empty"))
            lastSent = new DateTime().withYear(2000).toString("yyyy-MM-dd");

        ArrayList<GuestLog> list = db.getAllGuestLogSince(lastSent);


        System.out.println("LAST UPDATED FROM STORE ************ " + lastSent);


        apiService.sendLogs(list).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                response.code();
                System.out.println("CODE ******************** " + response.code());
                if (response.code() == 200)
                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putString(LAST_SENT_KEY, date.toString())
                            .commit();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("FAILURE ******************** " + t.getLocalizedMessage());
            }
        });
    }

}

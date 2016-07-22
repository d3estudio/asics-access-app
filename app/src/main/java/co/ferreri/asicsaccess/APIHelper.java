package co.ferreri.asicsaccess;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHelper {
    private static String LAST_SENT_KEY = "LAST_SENT_KEY";
//    private static String BASE_URL = "http://10.0.1.64:8080";
    private static String BASE_URL = "http://asicshub.com.br";
    private static String API_URL = BASE_URL + "/api/gateway/";

    private DatabaseHelper db;

    private Context context;

    private APIService apiService;

    public APIHelper(Context context) {
        this.context = context;

        this.db = new DatabaseHelper(context);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(APIService.class);

    }

    public void loadAllGuestsSince() {
        LastUpdated lastUpdated = new LastUpdated(Utils.getCellPhoneId(context), db.getLastUpdatedGuest());
        apiService.loadAllGuestsSince(lastUpdated).enqueue(new Callback<ArrayList<Guest>>() {
            @Override
            public void onResponse(Call<ArrayList<Guest>> call, retrofit2.Response<ArrayList<Guest>> response) {
                Log.e("API", "LOAD ALL GUESTS SUCCESS " + response.raw());
                if (response.body() != null)
                    db.addOrUpdateGuests(response.body());

                if (response.code() == 400)
                    Utils.showCenteredToast(context, "Acesso negado\nEste dispositivo não possui permissão", 1);

            }

            @Override
            public void onFailure(Call<ArrayList<Guest>> call, Throwable t) {
                Log.e("API", "LOAD ALL GUESTS FAILURE " + t.getLocalizedMessage());
                Utils.showCenteredToast(context, "Falha na conexão, impossível se conectar com o servidor", 1);
            }
        });
    }

    public void loadAllGuestLogsSince() {
        LastCreated lastCreated = new LastCreated(Utils.getCellPhoneId(context), db.getLastCreatedExternalLog());
        apiService.loadAllGuestLogsSince(lastCreated).enqueue(new Callback<ArrayList<GuestLog>>() {
            @Override
            public void onResponse(Call<ArrayList<GuestLog>> call, Response<ArrayList<GuestLog>> response) {
                Log.e("API", "LOAD ALL GUESTS LOGS SINCE SUCCESS " + response.raw());
                if (response.body() != null) {
                    db.addOrUpdateGuestLogs(response.body());
                    Utils.storePreferenceDate(context, LAST_SENT_KEY, Utils.getCurrentFormatedDate());
                    Utils.storeIsInitial(context);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GuestLog>> call, Throwable t) {
                Log.e("API", "LOAD ALL GUESTS LOGS SINCE FAILURE " + t.getLocalizedMessage());
            }
        });
    }

    public void loadAllOtherGuestLogs() {
        LastCreated lastCreated = new LastCreated(Utils.getCellPhoneId(context), Utils.getMidnightFormatedDate());
        apiService.loadOtherGuestLogs(lastCreated).enqueue(new Callback<ArrayList<GuestLog>>() {
            @Override
            public void onResponse(Call<ArrayList<GuestLog>> call, Response<ArrayList<GuestLog>> response) {
                Log.e("API", "LOAD OTHER GUESTS LOGS SUCCESS " + response.raw());
                if (response.body() != null)
                    db.addOrUpdateGuestLogs(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<GuestLog>> call, Throwable t) {
                Log.e("API", "LOAD OTHER GUESTS LOGS FAILURE " + t.getLocalizedMessage());
            }
        });
    }

    public void sendLocalGuestLogs() {

        final String date = Utils.getCurrentFormatedDate();

        String lastSent = Utils.getStoredDate(context, LAST_SENT_KEY);

        ArrayList<GuestLog> list = db.getAllLocalLogsSince(lastSent);
        LogList logs = new LogList(Utils.getCellPhoneId(context), list);


        if (!list.isEmpty())
            apiService.sendLogs(logs).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    response.code();
                    Log.e("API", "SEND ALL GUESTS LOGS SUCCESS " + response.raw());
                    if (response.code() == 200) {
                        Utils.storePreferenceDate(context, LAST_SENT_KEY, date);
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("API", "SEND ALL GUESTS LOGS FAILURE " + t.getLocalizedMessage());
                }
            });
        else
            Log.e("API", "LOG LIST IS EMPTY " + list.size());

    }

}

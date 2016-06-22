package co.ferreri.asicsaccess;

import android.content.Context;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class APIHelper {
    private static String BASEURL = "http://demo6882708.mockable.io/guests";

    private DatabaseHelper db;

    public APIHelper(Context context) {
        db = new DatabaseHelper(context);
    }


    public void getGuestApi(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Date date = new Date();
        System.out.println("CALLLLLING API FUNCTION ______________ " + dateFormat.format(date));
        //get
        Fuel.get(BASEURL).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                //do something when it is failure
                System.out.println("Fail" + error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                //do something when it is successful
                JSONArray results;
                ArrayList<Guest> guestArray = new ArrayList<>();

                try{
                    results = new JSONArray(data);

                    for (int i=0; i<results.length(); i++){
                        Guest guest = new Guest();
                        JSONObject jsonObj;
                        jsonObj = results.getJSONObject(i);

                        guest.setId(jsonObj.getInt("id"));
                        guest.setName(jsonObj.getString("name"));
                        guest.setEmail(jsonObj.getString("email"));
                        guest.setQrCode(jsonObj.getString("qrcode"));
                        guest.setOccupation(jsonObj.getString("occupation"));
                        guest.setUpdatedAt(jsonObj.getString("updated_at"));

                        guestArray.add(guest);
                    }
                    db.addOrUpdateGuests(guestArray);

                }catch (JSONException e){
                    android.util.Log.e("Error", "JsonArray error", e);
                }

            }
        });
    }
}

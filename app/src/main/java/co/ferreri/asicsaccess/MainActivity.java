package co.ferreri.asicsaccess;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;


public class MainActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    EditText etSearch;
    Button btnSearch;
    QREader qrEader;

    boolean isOpen = false;

    DatabaseHelper db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.camera_view);
        etSearch = (EditText) findViewById(R.id.etSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = etSearch.getText().toString();
                etSearch.setText("");
                Guest foundGuest = db.getGuest(search);
                if (foundGuest != null){
                    showDialog(foundGuest.getName());
                }

            }
        });

        createQreader();
        getGuestApi();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call in onStart
        qrEader.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Call in onDestroy
        qrEader.stop();
        qrEader.releaseAndCleanup();
    }

    private void createQreader() {
        qrEader = new QREader.Builder(this, surfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                if (!isOpen) {
                    Log.d("QREader Open Dialog", "Value : " + data);
                    surfaceView.post(new Runnable() {
                        @Override
                        public void run() {
                            showDialog(data);
                        }
                    });
                }
            }
        }).build();

        qrEader.init();
    }

    private void showDialog(String qrcode) {
        isOpen = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmar convidado?");
        builder.setMessage(qrcode);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("OK");
                        isOpen = false;
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("CANCEL");
                        isOpen = false;
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void getGuestApi(){
        //get
        Fuel.get("http://demo7110949.mockable.io/guests").responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                //do something when it is failure
                System.out.println("Fail" + error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                //do something when it is successful
                JSONArray results;
                ArrayList<Guest> guestArray = new ArrayList<Guest>();

                try{
                    results = new JSONArray(data);
                    System.out.println(results);

                    for (int i=0; i<results.length(); i++){
                        Guest guest = new Guest();
                        JSONObject jsonObj;
                        jsonObj = results.getJSONObject(i);

                        System.out.println("json obj"+jsonObj);
                        System.out.println("json var"+jsonObj.getString("name"));

                        guest.setId(jsonObj.getInt("id"));
                        guest.setName(jsonObj.getString("name"));
                        guest.setEmail(jsonObj.getString("email"));
                        guest.setQrCode(jsonObj.getString("qrcode"));
                        guest.setUpdatedAt(jsonObj.getString("updated_at"));

                        guestArray.add(guest);
                    }

                    db.addOrUpdateGuests(guestArray);

//                    ArrayList<Guest> all = new ArrayList<>(db.getAllGuest());
//                    System.out.println(all);



                }catch (JSONException e){
                    Log.e("Error", "JsonArray error");
                }

            }
        });
    }
}
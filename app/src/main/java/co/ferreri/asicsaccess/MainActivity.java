package co.ferreri.asicsaccess;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    ImageView btnSearch;
    QREader qrEader;

    boolean isOpen = false;

    DatabaseHelper db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.camera_view);
        etSearch = (EditText) findViewById(R.id.etSearch);
        btnSearch = (ImageView) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGuestSearch();
            }
        });

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    onGuestSearch();
                    return true;
                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                if(s.length()>0)
                    btnSearch.setVisibility(View.VISIBLE);
                else
                    btnSearch.setVisibility(View.INVISIBLE);

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        createQreader();
        getGuestApi();
    }

    public void onGuestSearch(){
        String search = etSearch.getText().toString();
        if (search.length() < 1)
            return;

        Guest foundGuest = db.getGuest(search);
        String last = db.getLastUpdated();

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        if (foundGuest != null){
            etSearch.setText("");
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            System.out.println("Last updated "+last);
            showDialog(foundGuest);
        }else {
            //centered text on toast
            Toast toast = Toast.makeText(this,"Usuário não encontrado\nBusque novamente por nome ou email", Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
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
                            //showDialog(data);
                        }
                    });
                }
            }
        }).build();

        qrEader.init();
    }

    private void showDialog(Guest guest) {
        isOpen = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmar presença?");
        builder.setMessage(guest.getName()+"\n"+guest.getEmail());

        String positiveText = "CONFIRMAR";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("CONFIRMAR");
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("CANCEL");
                    }
                });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                System.out.println("OnDismiss");
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

                    for (int i=0; i<results.length(); i++){
                        Guest guest = new Guest();
                        JSONObject jsonObj;
                        jsonObj = results.getJSONObject(i);

                        guest.setId(jsonObj.getInt("id"));
                        guest.setName(jsonObj.getString("name"));
                        guest.setEmail(jsonObj.getString("email"));
                        guest.setQrCode(jsonObj.getString("qrcode"));
                        guest.setUpdatedAt(jsonObj.getString("updated_at"));

                        guestArray.add(guest);
                    }

                    db.addOrUpdateGuests(guestArray);

                    ArrayList<Guest> all = new ArrayList<>(db.getAllGuest());
                    System.out.println(all);



                }catch (JSONException e){
                    Log.e("Error", "JsonArray error");
                }

            }
        });
    }
}
package co.ferreri.asicsaccess;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;


public class MainActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private EditText etSearch;
    private ImageView btnSearch;
    private QREader qrEader;
    private Toast toast;

    private DatabaseHelper db = new DatabaseHelper(this);
    private APIHelper api = new APIHelper(this);

    private boolean isOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.hideKeyboardOnOutsideClick(findViewById(R.id.parent), MainActivity.this);

        surfaceView = (SurfaceView) findViewById(R.id.camera_view);
        etSearch = (EditText) findViewById(R.id.etSearch);
        btnSearch = (ImageView) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGuestSearchByName();
            }
        });

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    onGuestSearchByName();
                    return true;
                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    btnSearch.setVisibility(View.VISIBLE);
                else
                    btnSearch.setVisibility(View.INVISIBLE);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        callAPIsHourly();
        createQreader();
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
                            onGuestSearchByQrcode(data);
                        }
                    });
                }
            }
        }).build();

        qrEader.init();
    }

    private void createGuestLog(Guest guest) {
        String logId = UUID.randomUUID().toString();
        int guestId = guest.getId();
        String dateTime = Utils.getCurrentFormatedDate();

        GuestLog guestLog = new GuestLog(logId, dateTime, guestId, Utils.getCellPhoneId(this));

        db.addGuestLog(guestLog);
    }

    private void showDialog(final Guest guest) {
        isOpen = true;

        String warning = db.checkIfGuestHasLog(guest.getId()) ? "Convidado já realizou checkin" : "";

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmar presença?");
        builder.setMessage(guest.getName() + "\n" +
                guest.getEmail() + "\n" +
                guest.getOccupation() + "\n\n" +
                warning
        );

        String positiveText = "CONFIRMAR";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("CONFIRMAR");
                        createGuestLog(guest);
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
        dialog.getWindow().getAttributes().verticalMargin = 0.1F;


        dialog.show();
    }

    public void onGuestSearchByName() {
        ArrayList<Guest> list = db.getAllGuests();
        String search = etSearch.getText().toString();
        if (search.length() < 1)
            return;

        Guest guest = db.getGuestByName(search);

        if (guest != null) {
            etSearch.setText("");
            Utils.hideSoftKeyboard(MainActivity.this);
            showDialog(guest);
        } else {
            //centered text on toast
            Utils.showCenteredToast(this, "Usuário não encontrado\nBusque novamente por nome ou email", 0);
        }
    }

    public void onGuestSearchByQrcode(String qrcode) {
        Guest guest = db.getGuestByQrcode(qrcode);

        if (guest != null) {
            showDialog(guest);
        } else {
            isOpen = true;
            Utils.showCenteredToast(this, "QRCode inválido, tente novamente", 0);

            surfaceView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOpen = false;
                }
            }, 3000);
        }
    }

    private void callAPIs() {
        api.loadAllGuestsSince();

        if (Utils.getIsInitial(this)) {
            api.loadAllGuestLogsSince();
        } else {
            api.sendLocalGuestLogs();

            api.loadAllOtherGuestLogs();
        }
    }

    private void callAPIsHourly() {

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                DateTime dateTime = new DateTime();
                System.out.println("************* CALLING API ***************** " + dateTime);

                callAPIs();

            }
        }, 0, 1, TimeUnit.MINUTES);
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

        callAPIs();
        Log.e("MainActivity", "DESTROY");
        // Call in onDestroy
        qrEader.stop();
        qrEader.releaseAndCleanup();
    }

}
package co.ferreri.asicsaccess;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;

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


    private DatabaseHelper db = new DatabaseHelper(this);
    private APIHelper api = new APIHelper(this);
    private PrinterHelper printerHelper = new PrinterHelper(this);

    private boolean isOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.hideKeyboardOnOutsideClick(findViewById(R.id.parent), MainActivity.this);

        init();
        callAPIsHourly();
        createQreader();

    }

    private void init(){
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

    public void onGuestSearchByName() {
        String search = etSearch.getText().toString();
        if (search.length() < 1)
            return;

        Guest guest = db.getGuestByName(search);

        if (guest != null) {
            // Get instance of Vibrator from current Context
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(400);
            etSearch.setText("");
            Utils.hideSoftKeyboard(MainActivity.this);
            showDialog(guest);
        } else {
            //centered text on toast
            Utils.showCenteredToast(this, "Usuário não encontrado\nBusque novamente por nome ou email", 0);
        }
    }

    public void onGuestSearchByQrcode(String data)
    {
        String qrcode = data.replaceAll("\\s+","");
        Guest guest = db.getGuestByQrcode(qrcode);
        System.out.println("/"+data+"/");
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

    private void showDialog(final Guest guest) {
        isOpen = true;

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setTitle("Confirmar Presença");

        TextView dialogName = (TextView) dialog.findViewById(R.id.dialog_guest_name);
        TextView dialogEmail = (TextView) dialog.findViewById(R.id.dialog_guest_email);
        TextView dialogOccupation = (TextView) dialog.findViewById(R.id.dialog_guest_occupation);
        Button dialogPrint = (Button) dialog.findViewById(R.id.button_dialog_print);
        Button dialogWarning = (Button) dialog.findViewById(R.id.warning_dialog);
        Button dialogCancel = (Button) dialog.findViewById(R.id.button_dialog_cancel);
        Button dialogConfirm = (Button) dialog.findViewById(R.id.button_dialog_confirm);

        dialogName.setText(guest.getName());
        dialogEmail.setText(guest.getEmail());
        dialogOccupation.setText(guest.getOccupation());

        if (db.checkIfGuestHasLog(guest.getId()))
            dialogWarning.setVisibility(View.VISIBLE);

        dialogPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("PRINT");
                printerHelper.print(guest);
            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CANCEL");
                dialog.dismiss();
            }
        });

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CONFIRMAR");
                createGuestLog(guest);
                dialog.dismiss();
                Utils.showCenteredToast(MainActivity.this, "Convidado confirmado com sucesso", 0);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                System.out.println("OnDismiss");
                isOpen = false;
            }
        });

        dialog.getWindow().getAttributes().verticalMargin = 0.1F;

        dialog.show();


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
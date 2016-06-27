package co.ferreri.asicsaccess;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


class LastUpdated {

    @SerializedName("updated_since")
    @Expose
    private String updatedSince;

    public LastUpdated(String updatedSince) {
        this.updatedSince = updatedSince;
    }

}

class LogList {

    @SerializedName("logs")
    @Expose
    private ArrayList<GuestLog> logs;

    public LogList(ArrayList<GuestLog> logs) {
        this.logs = logs;
    }

}

class AccessToken {

    @SerializedName("access_token")
    @Expose
    private String access_token;

    public AccessToken(Context context) {
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        this.access_token = mngr.getDeviceId();
    }

}

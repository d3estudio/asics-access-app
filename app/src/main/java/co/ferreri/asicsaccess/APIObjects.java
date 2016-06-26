package co.ferreri.asicsaccess;

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

package co.ferreri.asicsaccess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


class LastUpdated {

    @SerializedName("access_token")
    @Expose
    private String accessToken;

    @SerializedName("updated_since")
    @Expose
    private String updatedSince;

    public LastUpdated(String accessToken, String updatedSince) {
        this.accessToken = accessToken;
        this.updatedSince = updatedSince;
    }

}

class LastCreated {

    @SerializedName("access_token")
    @Expose
    private String accessToken;

    @SerializedName("created_since")
    @Expose
    private String updatedCreated;

    public LastCreated(String accessToken, String updatedCreated) {
        this.accessToken = accessToken;
        this.updatedCreated = updatedCreated;
    }

}

class LogList {

    @SerializedName("access_token")
    @Expose
    private String accessToken;

    @SerializedName("logs")
    @Expose
    private ArrayList<GuestLog> logs;

    public LogList(String accessToken, ArrayList<GuestLog> logs) {
        this.accessToken = accessToken;
        this.logs = logs;
    }

}

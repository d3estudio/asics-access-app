package co.ferreri.asicsaccess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestLog {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("guest_id")
    @Expose
    private Integer guestId;

    /**
     * No args constructor for use in serialization
     */
    public GuestLog() {
    }

    /**
     * @param id
     * @param createdAt
     * @param action
     * @param guestId
     */
    public GuestLog(Integer id, String action, String createdAt, Integer guestId) {
        this.id = id;
        this.action = action;
        this.createdAt = createdAt;
        this.guestId = guestId;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action The action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The guestId
     */
    public Integer getGuestId() {
        return guestId;
    }

    /**
     * @param guestId The guest_id
     */
    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

}

class LastUpdated {

    @SerializedName("updated_since")
    @Expose
    private String updatedSince;

    public LastUpdated() {
    }

    /**
     *
     * @param updatedSince
     */
    public LastUpdated(String updatedSince) {
        this.updatedSince = updatedSince;
    }


}
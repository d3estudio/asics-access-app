package co.ferreri.asicsaccess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestLog {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("guest_id")
    @Expose
    private int guestId;


    /**
     * @param id
     * @param createdAt
     * @param action
     * @param guestId
     */
    public GuestLog(String id, String action, String createdAt, int guestId) {
        this.id = id;
        this.action = action;
        this.createdAt = createdAt;
        this.guestId = guestId;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
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
    public int getGuestId() {
        return guestId;
    }

    /**
     * @param guestId The guest_id
     */
    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

}
package co.ferreri.asicsaccess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestLog {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("guest_id")
    @Expose
    private int guestId;
    @SerializedName("access_token")
    @Expose
    private String accessToken;


    /**
     * @param id
     * @param createdAt
     * @param guestId
     */
    public GuestLog(String id, String createdAt, int guestId, String accessToken) {
        this.id = id;
        this.createdAt = createdAt;
        this.guestId = guestId;
        this.accessToken = accessToken;
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

    /**
     * @return The access_token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken The created_at
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
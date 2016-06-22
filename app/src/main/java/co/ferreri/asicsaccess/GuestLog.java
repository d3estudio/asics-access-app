package co.ferreri.asicsaccess;

public class GuestLog {
    private int id;
    private String action;
    private String createdAt;
    private int guestId;

    public GuestLog() {
    }

    public GuestLog(int id, String action, String createdAt, int guestId) {
        this.id = id;
        this.action = action;
        this.createdAt = createdAt;
        this.guestId = guestId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }
}

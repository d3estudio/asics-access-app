package co.ferreri.asicsaccess;


public class Guest {

    private int id;
    private String name;
    private String email;
    private String qrCode;
    private String updatedAt;

    public Guest() {

    }

    public Guest(int id, String name, String email, String qrCode, String updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.qrCode = qrCode;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

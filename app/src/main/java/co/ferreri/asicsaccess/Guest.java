package co.ferreri.asicsaccess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Guest {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("qr_code")
    @Expose
    private String qrCode;
    @SerializedName("occupation")
    @Expose
    private String occupation;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Guest() {
    }

    /**
     * @param updatedAt
     * @param id
     * @param occupation
     * @param email
     * @param name
     * @param qrCode
     */
    public Guest(int id, String name, String email, String qrCode, String occupation, String updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.qrCode = qrCode;
        this.occupation = occupation;
        this.updatedAt = updatedAt;
    }

    /**
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The qrCode
     */
    public String getQrCode() {
        return qrCode;
    }

    /**
     * @param qrCode The qr_code
     */
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    /**
     * @return The occupation
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * @param occupation The occupation
     */
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    /**
     * @return The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
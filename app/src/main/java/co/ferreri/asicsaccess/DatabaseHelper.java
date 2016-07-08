package co.ferreri.asicsaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    static final String DB_NAME = "ASICS.TEST2";

    // database version
    static final int DB_VERSION = 1;

    // Table Names
    public static final String TABLE_GUESTS = "GUESTS";
    public static final String TABLE_LOGS = "LOGS";

    // Table columns
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String NAME_CLEAN = "name_clean";
    public static final String EMAIL = "email";
    public static final String QR_CODE = "qr_code";
    public static final String OCCUPATION = "occupation";
    public static final String UPDATED_AT = "updated_at";
    public static final String REMOVED_AT = "removed_at";

    public static final String GUEST_ID = "guest_id";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String CREATED_AT = "created_at";

    // Creating guest table query
    private static final String CREATE_TABLE_GUESTS = "create table "
            + TABLE_GUESTS + "("
            + ID + " INTEGER PRIMARY KEY, "
            + NAME + " TEXT NOT NULL, "
            + NAME_CLEAN + " TEXT NOT NULL, "
            + EMAIL + " TEXT NOT NULL, "
            + QR_CODE + " TEXT, "
            + OCCUPATION + " TEXT NOT NULL, "
            + UPDATED_AT + " DATETIME,"
            + REMOVED_AT + " DATETIME);";

    // Creating log table query
    private static final String CREATE_TABLE_LOGS = "create table "
            + TABLE_LOGS + "("
            + ID + " STRING PRIMARY KEY, "
            + CREATED_AT + " DATETIME NOT NULL,"
            + GUEST_ID + " INTEGER NOT NULL,"
            + ACCESS_TOKEN + " TEXT);";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GUESTS);
        db.execSQL(CREATE_TABLE_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        onCreate(db);
    }

    public Guest getGuestByName(String str) {
        SQLiteDatabase db = this.getReadableDatabase();

        Guest guest = null;
        try {
            Cursor cursor = db.query(
                    TABLE_GUESTS,
                    new String[]{ID, NAME, EMAIL, QR_CODE, OCCUPATION, UPDATED_AT, REMOVED_AT},
                    NAME_CLEAN + " LIKE ? OR " + EMAIL + " LIKE ? ",
                    new String[]{str + "%", str + "%"},
                    null, null, null, null
            );

            if (cursor.moveToFirst())
                guest = cursorToGuest(cursor);

            cursor.close();
        }catch (IllegalStateException e){
            Log.e("DB", "GET GUEST BY NAME ERROR " + e);
        }

        return guest;
    }

    public Guest getGuestByQrcode(String str) {
        SQLiteDatabase db = this.getReadableDatabase();

        Guest guest = null;

        try {
            Cursor cursor = db.query(
                    TABLE_GUESTS,
                    new String[]{ID, NAME, EMAIL, QR_CODE, OCCUPATION, UPDATED_AT, REMOVED_AT},
                    QR_CODE + "=? ",
                    new String[]{str},
                    null, null, null, null
            );

            if (cursor.moveToFirst())
                guest = cursorToGuest(cursor);

            cursor.close();
        }catch (IllegalStateException e){
            Log.e("DB", "GET GUEST BY QRCODE ERROR " + e);
        }
        return guest;
    }

    public String getLastUpdatedGuest() {
        SQLiteDatabase db = this.getReadableDatabase();

        String lastUpdated = Utils.getOldFormattedDate();

        try {
            Cursor cursor = db.query(
                    TABLE_GUESTS,
                    new String[]{"max(" + UPDATED_AT + ")"},
                    null,
                    null,
                    null, null, null, null
            );

            if (cursor.moveToFirst() && cursor.getString(0) != null)
                lastUpdated = cursor.getString(0);

            cursor.close();
        }catch (IllegalStateException e){
            Log.e("DB", "GET LAST UPDATED ERROR " + e);
        }

        return lastUpdated;
    }

    public void addOrUpdateGuests(ArrayList<Guest> guestList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Guest guest : guestList) {
                values.put(ID, guest.getId());
                values.put(NAME, guest.getName());
                values.put(NAME_CLEAN, Utils.removeSpecialCharacters(guest.getName()));
                values.put(EMAIL, guest.getEmail());
                values.put(QR_CODE, guest.getQrCode());
                values.put(OCCUPATION, guest.getOccupation());
                values.put(UPDATED_AT, guest.getUpdatedAt());
                values.put(REMOVED_AT, guest.getRemovedAt());
                db.insertWithOnConflict(TABLE_GUESTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addGuestLog(GuestLog guestLog) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(ID, guestLog.getId());
            values.put(CREATED_AT, guestLog.getCreatedAt());
            values.put(GUEST_ID, guestLog.getGuestId());
            values.put(ACCESS_TOKEN, guestLog.getAccessToken());

            db.insertWithOnConflict(TABLE_LOGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e("DB", "INSERT GUEST LOG ERROR " + e);
        }
    }

    public void addOrUpdateGuestLogs(ArrayList<GuestLog> guestLogList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (GuestLog guestLog : guestLogList) {
                values.put(ID, guestLog.getId());
                values.put(CREATED_AT, guestLog.getCreatedAt());
                values.put(GUEST_ID, guestLog.getGuestId());
                values.put(ACCESS_TOKEN, guestLog.getAccessToken());
                db.insertWithOnConflict(TABLE_LOGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public String getLastCreatedExternalLog() {
        SQLiteDatabase db = this.getReadableDatabase();

        String lastUpdated = Utils.getOldFormattedDate();

        try {
            Cursor cursor = db.query(
                    TABLE_LOGS,
                    new String[]{"max(" + CREATED_AT + ")"},
                    ACCESS_TOKEN + " <> ?",
                    new String[]{Utils.getCellPhoneId(context)},
                    null, null, null, null
            );


            if (cursor.moveToFirst() && cursor.getString(0) != null)
                lastUpdated = cursor.getString(0);

            cursor.close();
        }catch (IllegalStateException e){
            Log.e("DB", "GET LAST CREATED LOG EXTERNAL ERROR " + e);
        }

        return lastUpdated;
    }

    public boolean checkIfGuestHasLog(int guestId) {
        SQLiteDatabase db = this.getReadableDatabase();

        boolean hasLog = false;

        try {
            Cursor cursor = db.query(
                    TABLE_LOGS,
                    new String[]{ID, GUEST_ID},
                    GUEST_ID + "=?",
                    new String[]{Integer.toString(guestId)},
                    null, null, null, null
            );

            if (cursor.moveToFirst())
                hasLog = true;

            cursor.close();
        }catch (IllegalStateException e){
            Log.e("DB", "CHECK IF GUEST HAS LOG ERROR " + e);
        }

        return hasLog;
    }

    public ArrayList<GuestLog> getAllLocalLogsSince(String lastSent) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GuestLog> guestList = new ArrayList<>();
        try {
            String QUERY = "SELECT * FROM " + TABLE_LOGS + " WHERE " + ACCESS_TOKEN + " = '" + Utils.getCellPhoneId(context) + "' AND " + CREATED_AT + " >= '" + lastSent + "'";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    GuestLog guest = cursorToGuestLog(cursor);
                    guestList.add(guest);
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e("DB", "GET ALL GUESTS LOGS ERROR " + e);
        }
        return guestList;
    }

    /****************
     * HELPERS
     *****************/

    private Guest cursorToGuest(Cursor cursor) {
        return new Guest(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6)
        );
    }

    private GuestLog cursorToGuestLog(Cursor cursor) {
        return new GuestLog(
                cursor.getString(0),
                cursor.getString(1),
                Integer.parseInt(cursor.getString(2)),
                cursor.getString(3)

        );
    }
}

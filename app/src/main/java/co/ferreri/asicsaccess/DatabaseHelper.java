package co.ferreri.asicsaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    public static final String NAME = "nome";
    public static final String EMAIL = "email";
    public static final String QRCODE = "qrcode";
    public static final String OCCUPATION = "occupation";
    public static final String UPDATED_AT = "updated_at";

    public static final String GUEST_ID = "guestId";
    public static final String ACTION = "action";
    public static final String CREATED_AT = "created_at";

    // Creating guest table query
    private static final String CREATE_TABLE_GUESTS = "create table "
            + TABLE_GUESTS + "("
            + ID + " INTEGER PRIMARY KEY, "
            + NAME + " TEXT NOT NULL, "
            + EMAIL + " TEXT, "
            + QRCODE + " TEXT, "
            + OCCUPATION + " TEXT, "
            + UPDATED_AT + " DATETIME);";

    // Creating log table query
    private static final String CREATE_TABLE_LOGS = "create table "
            + TABLE_LOGS + "("
            + ID + " INTEGER PRIMARY KEY, "
            + ACTION + " TEXT, "
            + CREATED_AT + " DATETIME,"
            + GUEST_ID + " INTEGER, "
            + " FOREIGN KEY (" + GUEST_ID + ") REFERENCES " + TABLE_GUESTS + "(" + ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
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

        Cursor cursor = db.query(
                TABLE_GUESTS,
                new String[]{ID, NAME, EMAIL, QRCODE, OCCUPATION, UPDATED_AT},
                NAME + " LIKE ? OR " + EMAIL + " LIKE ? ",
                new String[]{str + "%", str + "%"},
                null, null, null, null
        );

        Guest guest = null;

        if (cursor.moveToFirst())
            guest = cursorToGuest(cursor);

        cursor.close();

        return guest;
    }

    public Guest getGuestByQrcode(String str) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_GUESTS,
                new String[]{ID, NAME, EMAIL, QRCODE, OCCUPATION, UPDATED_AT},
                QRCODE + "=?",
                new String[]{str},
                null, null, null, null
        );


        Guest guest = null;

        if (cursor.moveToFirst())
            guest = cursorToGuest(cursor);


        return guest;
    }

    public String getLastUpdated() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_GUESTS,
                new String[]{"max(" + UPDATED_AT + ")"},
                null,
                null,
                null, null, null, null
        );


        cursor.moveToFirst();

        String lastUpdated = cursor.getString(0);

        cursor.close();

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
                values.put(EMAIL, guest.getEmail());
                values.put(QRCODE, guest.getQrCode());
                values.put(OCCUPATION, guest.getOccupation());
                values.put(UPDATED_AT, guest.getUpdatedAt());
                db.insertWithOnConflict(TABLE_GUESTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addOrUpdateGuestLogs(ArrayList<GuestLog> guestLogList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (GuestLog guestLog : guestLogList) {
                values.put(ID, guestLog.getId());
                values.put(ACTION, guestLog.getAction());
                values.put(CREATED_AT, guestLog.getCreatedAt());
                values.put(GUEST_ID, guestLog.getGuestId());
                db.insertWithOnConflict(TABLE_LOGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addGuestLog(GuestLog guestLog) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, guestLog.getId());
        values.put(ACTION, guestLog.getAction());
        values.put(CREATED_AT, guestLog.getCreatedAt());
        values.put(GUEST_ID, guestLog.getGuestId());

        long newRowId = db.insert(TABLE_LOGS, null, values);

    }

    private Guest cursorToGuest(Cursor cursor) {
        return new Guest(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
        );
    }

    private GuestLog cursorToGuestLog(Cursor cursor) {
        return new GuestLog(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(2),
                cursor.getString(3),
                Integer.parseInt(cursor.getString(1))
        );
    }


//    public ArrayList<Guest> getAllGuest() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        ArrayList<Guest> guestList = null;
//        try {
//            guestList = new ArrayList<Guest>();
//            String QUERY = "SELECT * FROM " + TABLE_GUESTS;
//            Cursor cursor = db.rawQuery(QUERY, null);
//            if (!cursor.isLast()) {
//                while (cursor.moveToNext()) {
//                    Guest guest = cursorToGuest(cursor);
//                    guestList.add(guest);
//                }
//            }
//            db.close();
//        } catch (Exception e) {
//            Log.e("error", e + "");
//        }
//        return guestList;
//    }
}

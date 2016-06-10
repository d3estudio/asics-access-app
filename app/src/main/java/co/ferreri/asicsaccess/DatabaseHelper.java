package co.ferreri.asicsaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "GUESTS";

    // Table columns
    public static final String ID = "id";
    public static final String NAME = "nome";
    public static final String EMAIL = "email";
    public static final String QRCODE = "qrcode";
    public static final String UPDATED_AT = "updated_at";

    // Database Information
    static final String DB_NAME = "ASICS.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + ID
            + " INTEGER PRIMARY KEY, " + NAME + " TEXT NOT NULL, " + EMAIL + " TEXT, " + QRCODE + " TEXT, " + UPDATED_AT + " DATE);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Guest getGuest(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { ID,
                        NAME, EMAIL, QRCODE, UPDATED_AT }, NAME + " LIKE ? ",
                new String[] { name+"%" }, null, null, null, null);


        if (cursor != null)
            cursor.moveToFirst();

        if (!cursor.moveToFirst())
            return null;


        Guest guest = new Guest(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
        );
        // return guest
        return guest;
    }


    public ArrayList<Guest> getAllGuest() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Guest> guestList = null;
        try{
            guestList = new ArrayList<Guest>();
            String QUERY = "SELECT * FROM "+TABLE_NAME;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    Guest guest = new Guest();
                    guest.setId(cursor.getInt(0));
                    guest.setName(cursor.getString(1));
                    guest.setEmail(cursor.getString(2));
                    guest.setQrCode(cursor.getString(3));
                    guest.setUpdatedAt(cursor.getString(4));
                    guestList.add(guest);
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return guestList;
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
                values.put(UPDATED_AT, guest.getUpdatedAt());
                db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}

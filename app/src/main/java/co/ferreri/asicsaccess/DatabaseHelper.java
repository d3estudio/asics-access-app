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
    public static final String QRCODE = "email";
    public static final String UPDATED_AT = "email";

    // Database Information
    static final String DB_NAME = "JOURNALDEV_GUESTS.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + ID
            + " INTEGER PRIMARY KEY, " + NAME + " TEXT NOT NULL, " + EMAIL + " TEXT, " + QRCODE + " TEXT, " + UPDATED_AT + " DATETIME);";

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


    public void addGuest(Guest guest) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(ID, guest.getName());
           // values.put(KEY_STATE, guest.getState());
            //values.put(KEY_DESCRIPTION, guest.getDescription());
            db.insert(TABLE_NAME, null, values);
            db.close();
        }catch (Exception e){
            Log.e("problem",e+"");
        }
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
                    guestList.add(guest);
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return guestList;
    }
}

package co.ferreri.asicsaccess;

/**
 * Created by portillo on 6/9/16.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DatabaseManager(Context c) {
        context = c;
    }

    public DatabaseManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(Guest guest) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ID, guest.getId());
        contentValue.put(DatabaseHelper.NAME, guest.getName());
        contentValue.put(DatabaseHelper.EMAIL, guest.getEmail());
        contentValue.put(DatabaseHelper.QRCODE, guest.getQrCode());
        //contentValue.put(DatabaseHelper.UPDATED_AT, guest.getUpdatedAt());
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.NAME, DatabaseHelper.EMAIL, DatabaseHelper.QRCODE, DatabaseHelper.UPDATED_AT };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(Guest guest) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.ID, guest.getId());
        contentValues.put(DatabaseHelper.NAME, guest.getName());
        contentValues.put(DatabaseHelper.EMAIL, guest.getEmail());
        contentValues.put(DatabaseHelper.QRCODE, guest.getQrCode());
        //contentValues.put(DatabaseHelper.UPDATED_AT, guest.getUpdatedAt());
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.ID + " = " + guest.getId(), null);
        return i;
    }

    public void delete(int id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
    }

}
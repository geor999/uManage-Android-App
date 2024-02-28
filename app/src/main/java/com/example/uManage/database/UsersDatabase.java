package com.example.uManage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class UsersDatabase extends SQLiteOpenHelper {
    private final Context context;
    private static final String NAME = "uManageUsers.db";
    private static final int VERSION = 2;

    /*TABLE VARIABLES*/
    private static final String TABLE_NAME = "users_list";
    private static final String ID = "id";
    private static final String USERNAME = "user_name";
    private static final String PASSWORD = "user_password";
    private static final String EMAIL = "user_email";

    public UsersDatabase(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
    }


    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USERNAME + " TEXT, " + PASSWORD + " TEXT, " + EMAIL + " TEXT);";
        db.execSQL(query);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addEntry(String name, String num, String priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USERNAME, name);
        contentValues.put(PASSWORD, num);
        contentValues.put(EMAIL, priority);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "User addition failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "User addition is successful!", Toast.LENGTH_SHORT).show();
        }
    }


    public Cursor allEntries() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }

    public Boolean checkusername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        if (MyDB != null) {
            Cursor cursor = MyDB.rawQuery("Select * from "+ TABLE_NAME +" where " + USERNAME + "= ?", new String[]{username});
            return cursor.getCount() > 0;
        }
return false;
    }

    public Boolean checkusernamepassword(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from "+TABLE_NAME+" where "+ USERNAME +" = ? and "+PASSWORD +" = ?", new String[] {username,password});
        return cursor.getCount() > 0;
    }
}
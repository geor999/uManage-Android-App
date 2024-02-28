package com.example.uManage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class PreferencesDatabase extends SQLiteOpenHelper {


    private final Context context;
    private static final String NAME = "Preferences.db";
    private static final int VERSION = 4;


    private static final String TABLE_NAME="preferences_list";
    private static final String ID ="id";
    private static final String THEME = "app_theme";
    private static final String USER = "user_name";
    private static final String SORT = "app_sort";

    public PreferencesDatabase(Context context) {
        super(context, NAME,null,VERSION);
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
        String query="CREATE TABLE "+TABLE_NAME +" ("+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+THEME+" TEXT, "+SORT+" TEXT, "+USER+" TEXT);";
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
    public void addEntry(String app_theme,String username,String sort){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();

        contentValues.put(THEME, app_theme);
        contentValues.put(USER,username);
        contentValues.put(SORT,sort);
        long result= db.insert(TABLE_NAME,null,contentValues);
        if(result == -1 )
        {
            Toast.makeText(context, "Preference initialization failed!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context,"Preference initialization is successful!",Toast.LENGTH_SHORT).show();
        }
    }
    public Cursor allEntries()
    {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db!= null)
        {
            cursor = db.rawQuery(query,null);

        }
        else
        {
            Toast.makeText(context,  "Failed!", Toast.LENGTH_SHORT).show();
        }
        return cursor;
    }
    public void update(int id,String app_theme,String username,String sort)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(THEME, app_theme);
        contentValues.put(USER,username);
        contentValues.put(SORT,sort);
        long result = db.update(TABLE_NAME,contentValues,"id=?",new String[]{String.valueOf(id)});
        if(result!= -1)
        {
            Toast.makeText(context, "Update done!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context,  "Update not done!", Toast.LENGTH_SHORT).show();
        }
    }
}

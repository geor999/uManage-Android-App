package com.example.uManage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class WorkersDatabase extends SQLiteOpenHelper {

    private final Context context;
    private static final String NAME = "WorkersList.db";
    private static final int VERSION = 13;

    /*TABLE VARIABLES*/
    private static final String TABLE_NAME="workers_list";
    private static final String ID ="id";
    private static final String WORKER_NAME = "worker_name"; //το όνομα του εργαζόμενου
    private static final String WORKER_SALARY = "worker_salary";//ο μισθός του εργαζόμενου
    private static final String WORKER_IMAGE = "worker_image";//Η εικόνα που ανέβασε ο χρήστης για τον εργαζόμενο αυτό(blob)
    private static final String WORKER_AGE = "worker_age";//η ηλικία του εργαζόμενου
    private static final String IMAGE_NAME = "image_name";//Το όνομα της φωτογραφίας που χρησιμοποιήσαμε
    private static final String USER = "user_name";//η εταιρία που δημιούργησε αυτό το προιόν


    public WorkersDatabase(Context context)
    {
        super(context, NAME,null,VERSION);
        this.context=context;
    }
    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE "+TABLE_NAME +" ("+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ WORKER_NAME +" TEXT, "+ WORKER_AGE +" INTEGER, "+ WORKER_SALARY +" INTEGER, "+IMAGE_NAME+" TEXT, "+ WORKER_IMAGE +" BLOB, "+USER+" TEXT);";
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

    public void addEntry(String name,int age, int salary,String imagename,byte[] image,String username){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();

        contentValues.put(WORKER_NAME, name);
        contentValues.put(WORKER_AGE, age);
        contentValues.put(WORKER_SALARY, salary);
        contentValues.put(WORKER_IMAGE,image);
        contentValues.put(IMAGE_NAME,imagename);
        contentValues.put(USER,username);
        long result= db.insert(TABLE_NAME,null,contentValues);
        if(result == -1 )
        {
            Toast.makeText(context, "Product addition failed!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context,"Product addition is successful!",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context,  "loustike", Toast.LENGTH_SHORT).show();
        }
        return cursor;
    }
    public void update(int id,String name,int age, int salary,String imagename,byte[] image,String username)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORKER_NAME, name);
        contentValues.put(WORKER_AGE, age);
        contentValues.put(WORKER_SALARY, salary);
        contentValues.put(WORKER_IMAGE,image);
        contentValues.put(IMAGE_NAME,imagename);
        contentValues.put(USER,username);
        long result = db.update(TABLE_NAME,contentValues,"id=?",new String[]{String.valueOf(id)});
        if(result!= -1)
        {
            Toast.makeText(context,  "Update ok", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(context,  "Update not ok", Toast.LENGTH_SHORT).show();
        }
    }
    public void delete(int id )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereargs[] ={""+id};
        db.delete(TABLE_NAME,ID+"=?", whereargs);

    }
}

package com.example.pedro.leitorimagem;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pedro on 18/09/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "contactsManager";

    private static final String TABLE_TEXTS = "textos";
    private static final String ID_TEXT = "id_texto";
    private static final String TEXT_CONTENT = "texto";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableTextos = "CREATE TABLE " + TABLE_TEXTS + "( "
                + ID_TEXT
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TEXT_CONTENT + " TEXT"
                + ")";
        db.execSQL(createTableTextos);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop current tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXTS);
        //create new tables
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop current tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXTS);
        //create new tables
        onCreate(db);
    }






    public int addText(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEXT_CONTENT, item);


        long result = db.insert(TABLE_TEXTS, null, contentValues);

        //-1 is returned when it was inserted correctly
        if (result == -1) {
            return (int) result;
        } else {
            return (int) result;
        }
    }

    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getTextContent(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TEXTS;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns only the ID that matches the name passed in
     * @param name
     * @return
     */
    public Cursor getTextID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID_TEXT + " FROM " + TABLE_TEXTS +
                " WHERE " + TEXT_CONTENT + " = ? ";
        Cursor data = db.rawQuery(query, new String[] { name});
        return data;
    }

    /**
     * Updates the name field
     * @param newName
     * @param id
     * @param oldName
     */
    public void updateText(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_TEXTS + " SET " + TEXT_CONTENT +
                " = ? WHERE " + ID_TEXT + " = '" + id + "'" +
                " AND " + TEXT_CONTENT + " = ?";
        Log.d(TAG, "updateText: query: " + query);
        Log.d(TAG, "updateText: Setting name to " + newName);
        db.execSQL(query,new String[] { newName, oldName});
    }

    /**
     * Delete from database
     * @param id
     * @param name
     */
    public void deleteText(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_TEXTS + " WHERE "
                + ID_TEXT + " = '" + id + "'" +
                " AND " + TEXT_CONTENT + " = ?";
        Log.d(TAG, "deleteText: query: " + query);
        Log.d(TAG, "deleteText: Deleting " + name + " from database.");
        db.execSQL(query, new String[] { name});
    }

}


package com.example.pedro.leitorimagem;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pedro on 18/09/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "contactsManager";

    private static final String TABLE_TEXTOS = "textos";
    private static final String ID_TEXTO = "id_texto";
    private static final String CONTEUDO_TEXTO = "texto";
    private static final String CADERNO_TEXTO = "caderno";

    private static final String TABLE_CADERNOS = "cadernos";
    private static final String ID_CADERNO = "ID_CADERNO";
    private static final String NOME_CADERNO = "NOME_CADERNO";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableTextos = "CREATE TABLE " + TABLE_TEXTOS + "( "
                + ID_TEXTO
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CONTEUDO_TEXTO + " TEXT"
                + CADERNO_TEXTO + " INTEGER"
                + ")";
        String createTableCadernos  =  "CREATE TABLE " + TABLE_CADERNOS + "( "
                + ID_CADERNO
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NOME_CADERNO +" TEXT"
                + ")";
        db.execSQL(createTableTextos);
        db.execSQL(createTableCadernos);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop current tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CADERNOS);
        //create new tables
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop current tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CADERNOS);
        //create new tables
        onCreate(db);
    }

    public boolean addNotebookData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOME_CADERNO, item);

        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_CADERNOS);

        long result = db.insert(TABLE_CADERNOS, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the data from the notebooks table
     * @return
     */
    public Cursor getNotebookData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CADERNOS;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns only the ID that matches the name passed in
     * @param notebookName
     * @return
     */
    public Cursor getNotebookID(String notebookName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID_CADERNO + " FROM " + TABLE_CADERNOS +
                " WHERE " + NOME_CADERNO + " = ? ";
        Cursor data = db.rawQuery(query, new String[] { notebookName});
        return data;
    }

    /**
     * Updates the name field
     * @param newNotebookName
     * @param id
     * @param oldNotebookName
     */
    public void updateNotebookName(String newNotebookName, int id, String oldNotebookName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_CADERNOS + " SET " + NOME_CADERNO +
                " = ? WHERE " + ID_CADERNO + " = '" + id + "'" +
                " AND " + NOME_CADERNO + " = ?";
        Log.d(TAG, "updateNotebookName: query: " + query);
        Log.d(TAG, "updateNotebookName: Setting name to " + newNotebookName);
        db.execSQL(query,new String[] { newNotebookName, oldNotebookName});
    }

    /**
     * Delete from database
     * @param id
     * @param notebookName
     */
    public void deleteNotebookName(int id, String notebookName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_CADERNOS + " WHERE "
                + ID_CADERNO + " = '" + id + "'" +
                " AND " + NOME_CADERNO + " = ?";
        Log.d(TAG, "deleteNotebookName: query: " + query);
        Log.d(TAG, "deleteNotebookName: Deleting " + notebookName + " from database.");
        db.execSQL(query, new String[] { notebookName});
    }

    public int addData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTEUDO_TEXTO, item);

        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_TEXTOS);

        long result = db.insert(TABLE_TEXTOS, null, contentValues);

        //if date as inserted incorrectly it will return -1
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
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TEXTOS;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns only the ID that matches the name passed in
     * @param name
     * @return
     */
    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID_TEXTO + " FROM " + TABLE_TEXTOS +
                " WHERE " + CONTEUDO_TEXTO + " = ? ";
        Cursor data = db.rawQuery(query, new String[] { name});
        return data;
    }

    /**
     * Updates the name field
     * @param newName
     * @param id
     * @param oldName
     */
    public void updateName(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_TEXTOS + " SET " + CONTEUDO_TEXTO +
                " = ? WHERE " + ID_TEXTO + " = '" + id + "'" +
                " AND " + CONTEUDO_TEXTO + " = ?";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query,new String[] { newName, oldName});
    }

    /**
     * Delete from database
     * @param id
     * @param name
     */
    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_TEXTOS + " WHERE "
                + ID_TEXTO + " = '" + id + "'" +
                " AND " + CONTEUDO_TEXTO + " = ?";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query, new String[] { name});
    }

}


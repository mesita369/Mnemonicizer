package com.mnemonicizer.mnemonicizer.utils;

/**
 * Created by Raghuram on 07-06-2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DataBaseHelper extends SQLiteAssetHelper {
    //DB info
    private static final String DB_NAME = "mnemonicizer.db";
    private static final int DB_VERSION = 1;

    //Table REMEDIE_TYPE
    public static final String WORDS_TABLE = "complete500";

    //Table  MNEMONICIZER
    public static final String WORD_ID = "wrd_id";
    public static final String WORD_NAME = "wrd_nm";
    public static final String MNEMONIC = "mnemonic";
    public static final String MEANING = "meaning";
    public static final String SYNONYM = "synonyms";
    public static final String ANTONYM = "antonyms";
    public static final String EX1 = "ex1";
    public static final String EX2 = "ex2";
    public static final String FAV_IN = "fav_in";
    public static final String CMPLT_IN = "cmplt_in";



    private static final String orderBy = DataBaseHelper.WORD_ID + " ASC";

    private Context mContext;
    private SQLiteDatabase mDB;

    public DataBaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
        mContext = ctx;
        setForcedUpgrade();
    }

    public Cursor getWordById(int id) {
        mDB = getReadableDatabase();
        Cursor cursor = mDB.query(
                DataBaseHelper.WORDS_TABLE, null, DataBaseHelper.WORD_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, orderBy);

        if (cursor != null) {
            return cursor;
        }

        return null;
    }

    public Cursor getAllWords() {
        String query = "SELECT  * FROM " + DataBaseHelper.WORDS_TABLE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }


    public Cursor getDescByIds(int typeId, int subId){
        mDB = getReadableDatabase();
        Cursor cur = mDB.rawQuery("select ID,NAME,DESCRIPTION,IMAGE from REMEDIES where TYPE_ID=? and SUB_TYPE_ID=?",
                new String[] {String.valueOf(typeId), String.valueOf(subId)});
        if (cur != null) {
            return cur;
        }
        return null;
    }
    // Adding Bookmark
    public int addFav(int id) {
        mDB = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FAV_IN, 1);
        // updating row
        return mDB.update(WORDS_TABLE, values, WORD_ID + " = ?",
                new String[] { String.valueOf(id) });
    }
    // Setting Read
    public int setComplete(int id) {
        mDB = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CMPLT_IN, 1);
        // updating row
        return mDB.update(WORDS_TABLE, values, WORD_ID + " = ?",
                new String[] { String.valueOf(id) });
    }
    public Cursor getAllFavs(){
        mDB = getReadableDatabase();
//        Cursor cursor = mDB.query(DataBaseHelper.WORDS_TABLE, new String[] { DataBaseHelper.WORD_ID }, DataBaseHelper.FAV_IN + " = ?", new String[] { String.valueOf(1) }, null, null, null);
       Cursor cur = mDB.rawQuery("select * from complete500 where fav_in=?",
                new String [] {String.valueOf(1)});
        if (cur != null) {
            return cur;
        }
        return null;
    }

    public int isFav(int id) {
        mDB = getReadableDatabase();
        Cursor cur = mDB.rawQuery("select * from complete500 where wrd_id=? and fav_in=?",
                new String[] {String.valueOf(id),  String.valueOf(1)});
        if (cur.getCount() != 0) {
            return 1;
        }
        return 0;
    }

    public int removeFav(int id) {
        mDB = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FAV_IN, 0);
        // updating row
        return mDB.update(WORDS_TABLE, values, WORD_ID + " = ?",
                new String[] { String.valueOf(id) });
    }
    public Cursor getAllCmplt(){
        mDB = getReadableDatabase();
        Cursor cur = mDB.rawQuery("select * from complete500 where cmplt_in=?",
                new String[] {String.valueOf(1)});

        if (cur != null) {
            Log.d("Killa",cur.toString());
            return cur;
        }
        return null;
    }

    public int isCmplt(int id) {
        mDB = getReadableDatabase();
        Cursor cur = mDB.rawQuery("select * from complete500 where wrd_id=? and cmplt_in=?",
                new String[] {String.valueOf(id), String.valueOf(1)});
        if (cur.getCount() != 0) {
            return 1;
        }
        return 0;
    }


}

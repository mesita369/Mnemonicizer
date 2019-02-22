package com.mnemonicizer.mnemonicizer.Model;

import android.database.Cursor;

import com.mnemonicizer.mnemonicizer.utils.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class Word {
    private int id;
    private String name;
    private String meaning;
    private String mnemonic;
    private int fav_in;
    private int cmplt_in;

    public Word() {
    }

    public Word(int id, String name, String meaning, String mnemonic, int fav_in, int cmplt_in) {
        this.id = id;
        this.name = name;
        this.meaning = meaning;
        this.mnemonic = mnemonic;
        this.fav_in = fav_in;
        this.cmplt_in = cmplt_in;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public int getFav_in() {
        return fav_in;
    }

    public void setFav_in(int fav_in) {
        this.fav_in = fav_in;
    }

    public int getCmplt_in() {
        return cmplt_in;
    }

    public void setCmplt_in(int cmplt_in) {
        this.cmplt_in = cmplt_in;
    }

    public static List<Word> fromCursor(Cursor cursor) {
        List<Word> words = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int index = cursor.getColumnIndex(DataBaseHelper.WORD_ID);
                int index2 = cursor.getColumnIndex(DataBaseHelper.WORD_NAME);
                int index3 = cursor.getColumnIndex(DataBaseHelper.MEANING);
                int index4 = cursor.getColumnIndex(DataBaseHelper.FAV_IN);
                int index5 = cursor.getColumnIndex(DataBaseHelper.CMPLT_IN);
                int index6 = cursor.getColumnIndex(DataBaseHelper.MNEMONIC);
                int id = cursor.getInt(index);

                int fav_in = cursor.getInt(index4);
                int cmplt_in = cursor.getInt(index5);
                String name = cursor.getString(index2);
                String meaning = cursor.getString(index3);
                String mnemonic = cursor.getString(index6);
                words.add(new Word(id,name,meaning,mnemonic,fav_in,cmplt_in));
            } while (cursor.moveToNext()); //move to next row in the query result
            return words;
        } else {
            return null;
        }

    }
}

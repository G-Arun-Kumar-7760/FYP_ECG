package com.experiment.chickenjohn.materialdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class HealthDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ECG.db";

    public HealthDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS ecg" + "(_id INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, value INTEGER, time REAL)");
        //empty the ecg table
        db.execSQL("DELETE FROM ecg");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

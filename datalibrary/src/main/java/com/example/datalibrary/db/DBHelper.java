/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.example.datalibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库创建工具
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_START_SQL = "CREATE TABLE IF NOT EXISTS ";
    private static final String CREATE_TABLE_PRIMIRY_SQL = " integer primary key autoincrement,";

    /**
     * 数据库名称
     */
    private static final String DB_NAME = "face.db";
    /**
     * 数据库版本
     */
    private static final int VERSION = 1;
    /**
     * 用户表
     */
    public static final String TABLE_USER = "user";
    public static SQLiteDatabase sd;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            onCreate(db);
        }
    }
//    @Override
//    public SQLiteDatabase getReadableDatabase() {
//        if (sd != null){
//            return sd;
//        }
//        return getDatabase();
//    }
//
//    @Override
//    public SQLiteDatabase getWritableDatabase() {
//        if (sd != null){
//            return sd;
//        }
//        return getDatabase();
//    }
//
//    private SQLiteDatabase getDatabase() {
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() , DB_NAME);
//        sd = SQLiteDatabase.openDatabase(file.getAbsolutePath(),
//                null, SQLiteDatabase.CREATE_IF_NECESSARY);
//
//        sd.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
//        createTables(sd);
//        return sd;
//    }

    public synchronized void createTables(SQLiteDatabase db) {
        if (db == null || db.isReadOnly()) {
            db = getWritableDatabase();
        }


        // 创建用户表的SQL语句
        StringBuffer userSql = new StringBuffer();
        userSql.append(CREATE_TABLE_START_SQL).append(TABLE_USER).append(" ( ");
        userSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
        userSql.append(" user_id").append(" varchar(32) default \"\"   ,");
        userSql.append(" user_name").append(" varchar(32) default \"\"   ,");
        userSql.append(" user_info").append(" varchar(32) default \"\"   ,");
        userSql.append(" group_id").append(" varchar(32) default \"\"   ,");
        userSql.append(" face_token").append(" varchar(128) default \"\" ,");
        userSql.append(" feature").append(" blob   ,");
        userSql.append(" image_name").append(" varchar(64) default \"\"  ,");
        userSql.append(" ctime").append(" long ,");
        userSql.append(" update_time").append(" long )");
        try {
            db.execSQL(userSql.toString());
            // db.execSQL(featureSql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

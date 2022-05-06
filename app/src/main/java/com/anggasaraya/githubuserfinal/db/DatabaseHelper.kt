package com.anggasaraya.githubuserfinal.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.anggasaraya.githubuserfinal.db.DatabaseContract.UserFavColumns.Companion.TABLE_NAME

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "dbuserfavapp"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseContract.UserFavColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${DatabaseContract.UserFavColumns.COLUMN_NAME_USERNAME} TEXT NOT NULL," +
                " ${DatabaseContract.UserFavColumns.COLUMN_NAME_NAME} TEXT," +
                " ${DatabaseContract.UserFavColumns.COLUMN_NAME_AVATAR_URL} TEXT," +
                " ${DatabaseContract.UserFavColumns.COLUMN_NAME_LOCATION} TEXT," +
                " ${DatabaseContract.UserFavColumns.COLUMN_NAME_COMPANY} TEXT," +
                " ${DatabaseContract.UserFavColumns.COLUMN_NAME_REPOSITORY} TEXT NOT NULL," +
                " ${DatabaseContract.UserFavColumns.COLUMN_NAME_FOLLOWER} TEXT NOT NULL," +
                " ${DatabaseContract.UserFavColumns.COLUMN_NAME_FOLLOWING} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
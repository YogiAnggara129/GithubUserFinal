package com.anggasaraya.githubuserfinal.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.anggasaraya.githubuserfinal.db.DatabaseContract.UserFavColumns.Companion.COLUMN_NAME_USERNAME
import com.anggasaraya.githubuserfinal.db.DatabaseContract.UserFavColumns.Companion.TABLE_NAME
import com.anggasaraya.githubuserfinal.db.DatabaseContract.UserFavColumns.Companion._ID
import java.sql.SQLException

class UserFavHelper(context: Context) {
    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private lateinit var dataBaseHelper: DatabaseHelper
        private lateinit var database: SQLiteDatabase
        private var INSTANCE: UserFavHelper? = null

        fun getInstance(context: Context): UserFavHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserFavHelper(context)
            }
    }

    init {
        dataBaseHelper = DatabaseHelper(context)
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$_ID DESC",
            null)
    }

    fun queryById(id: String): Cursor {
        return database.query(DATABASE_TABLE, null, "$_ID = ?", arrayOf(id), null, null, null, null)
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun update(id: String, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "$_ID = ?", arrayOf(id))
    }

    fun deleteById(id: String): Int {
        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
    }

    fun deleteByUsername(username: String): Int {
        return database.delete(DATABASE_TABLE, "$COLUMN_NAME_USERNAME = '$username'", null)
    }
}
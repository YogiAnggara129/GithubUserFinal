package com.anggasaraya.githubuserfinal.helper

import android.database.Cursor
import com.anggasaraya.githubuserfinal.db.DatabaseContract
import com.anggasaraya.githubuserfinal.entity.UserFav

object MappingHelper {

    fun mapCursorToArrayList(userFavCursor: Cursor?): ArrayList<UserFav> {
        val userFavList = ArrayList<UserFav>()
        userFavCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.UserFavColumns._ID))
                val username = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_USERNAME))
                val name = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_NAME))
                val avatarURL = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_AVATAR_URL))
                val location = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_LOCATION))
                val company = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_COMPANY))
                val repository = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_REPOSITORY))
                val follower = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_FOLLOWER))
                val following = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_FOLLOWING))
                userFavList.add(UserFav(id, username, name, avatarURL, location, company, repository, follower, following))
            }
        }
        return userFavList
    }

    fun mapCursorToObject(notesCursor: Cursor?): UserFav {
        var userFav = UserFav()
        notesCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(DatabaseContract.UserFavColumns._ID))
            val username = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_USERNAME))
            val name = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_NAME))
            val avatarURL = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_AVATAR_URL))
            val location = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_LOCATION))
            val company = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_COMPANY))
            val repository = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_REPOSITORY))
            val follower = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_FOLLOWER))
            val following = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.COLUMN_NAME_FOLLOWING))
            userFav = UserFav(id, username, name, avatarURL, location, company, repository, follower, following)
        }
        return userFav
    }
}
package com.anggasaraya.consumerapp.db

import android.net.Uri
import android.provider.BaseColumns

internal class DatabaseContract {

    companion object {
        const val AUTHORITY = "com.anggasaraya.githubuserfinal"
        const val SCHEME = "content"
    }

    internal class UserFavColumns : BaseColumns {
        companion object {
            const val _ID = "id"
            const val TABLE_NAME = "favorite_user"
            const val COLUMN_NAME_USERNAME = "username"
            const val COLUMN_NAME_NAME = "name"
            const val COLUMN_NAME_AVATAR_URL = "avatar_url"
            const val COLUMN_NAME_COMPANY = "company"
            const val COLUMN_NAME_LOCATION = "location"
            const val COLUMN_NAME_REPOSITORY = "repository"
            const val COLUMN_NAME_FOLLOWER = "follower"
            const val COLUMN_NAME_FOLLOWING = "following"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}
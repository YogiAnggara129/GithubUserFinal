package com.anggasaraya.githubuserfinal.entity

import android.database.Cursor
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserFav(
    var id: Int = 0,
    var username: String? = null,
    var name: String? = null,
    var avatarURL: String? = null,
    var location: String? = null,
    var company: String? = null,
    var repositroy: String? = null,
    var follower: String? = null,
    var following: String? = null
) : Parcelable
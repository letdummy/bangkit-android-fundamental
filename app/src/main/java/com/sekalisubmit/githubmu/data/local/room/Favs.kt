package com.sekalisubmit.githubmu.data.local.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "favUser")
@Parcelize
class Favs (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "avatarUrl")
    var avatarUrl: String? = null,

    @ColumnInfo(name = "publicRepos")
    var publicRepos: Int? = null,

    @ColumnInfo(name = "followers")
    var followers: Int? = null,
) : Parcelable
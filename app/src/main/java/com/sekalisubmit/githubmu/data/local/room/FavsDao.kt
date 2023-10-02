package com.sekalisubmit.githubmu.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favs: Favs)

    @Update
    fun update(favs: Favs)

    @Delete
    fun delete(favs: Favs)

    @Query("SELECT * FROM favUser ORDER BY login ASC")
    fun getAllFavUsers(): LiveData<List<Favs>>

    @Query("SELECT * FROM favUser WHERE login = :username")
    fun isFavorited(username: String): LiveData<Favs>
}

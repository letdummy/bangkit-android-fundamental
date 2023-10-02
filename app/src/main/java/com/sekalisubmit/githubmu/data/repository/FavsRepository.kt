package com.sekalisubmit.githubmu.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.sekalisubmit.githubmu.data.local.room.Favs
import com.sekalisubmit.githubmu.data.local.room.FavsDao
import com.sekalisubmit.githubmu.data.local.room.FavsRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavsRepository(application: Application) {
    private val favsDao: FavsDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavsRoomDatabase.getDatabase(application)
        favsDao = db.favsDao()
    }

    fun getAllFavUsers() : LiveData<List<Favs>> = favsDao.getAllFavUsers()

    fun isFavorited(username: String) : LiveData<Favs> = favsDao.isFavorited(username)

    fun insert(favs: Favs) {
        executorService.execute { favsDao.insert(favs) }
    }

    fun delete(favs: Favs) {
        executorService.execute { favsDao.delete(favs) }
    }

    fun update(favs: Favs) {
        executorService.execute { favsDao.update(favs) }
    }
}
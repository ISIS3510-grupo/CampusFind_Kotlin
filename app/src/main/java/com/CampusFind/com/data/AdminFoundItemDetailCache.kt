package com.CampusFind.com.data

import android.os.SystemClock
import android.util.Log
import android.util.LruCache
import com.CampusFind.com.model.FoundItemDetail
import com.google.firebase.auth.FirebaseAuth

object AdminFoundItemDetailCache {

    private const val CAPACITY = 10
    private const val TTL = 5 * 60 * 1000L

    data class Entry(
        val item: FoundItemDetail,
        val photoPath: String?,
        val savedAt: Long
    )

    private val cache = LruCache<String, Entry>(CAPACITY)
    private val auth = FirebaseAuth.getInstance()
    private var currentUid: String? = auth.currentUser?.uid
    private var generation = 0L

    init {
        auth.addAuthStateListener {
            synchronizeUser()
        }
    }

    @Synchronized
    private fun synchronizeUser() {
        val uid = auth.currentUser?.uid

        if (currentUid != uid) {
            clear()
            currentUid = uid
        }
    }

    @Synchronized
    fun version(): Long {
        synchronizeUser()
        return generation
    }

    @Synchronized
    fun get(id: String): Entry? {
        synchronizeUser()

        if (currentUid == null) return null

        val entry = cache.get(id)

        if (entry == null) {
            Log.d("InventoryCache", "CACHE MISS")
            return null
        }

        if (SystemClock.elapsedRealtime() - entry.savedAt >= TTL) {
            cache.remove(id)
            Log.d("InventoryCache", "CACHE EXPIRED")
            return null
        }

        Log.d("InventoryCache", "CACHE HIT")
        return entry
    }

    @Synchronized
    fun put(
        item: FoundItemDetail,
        photoPath: String?,
        version: Long
    ) {
        synchronizeUser()

        if (currentUid == null || version != generation) return

        cache.put(
            item.id,
            Entry(item, photoPath, SystemClock.elapsedRealtime())
        )

        Log.d("InventoryCache", "CACHE STORED")
    }

    @Synchronized
    fun invalidate(id: String) {
        generation++
        cache.remove(id)
        Log.d("InventoryCache", "CACHE INVALIDATION")
    }

    @Synchronized
    fun clear() {
        generation++
        cache.evictAll()
        Log.d("InventoryCache", "CACHE CLEARED")
    }
}
package com.CampusFind.com.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.CampusFind.com.model.FoundItemDraft
import kotlinx.coroutines.flow.first

private val Context.foundItemDrafts by preferencesDataStore(name = "found_item_drafts")

class FoundItemDraftStore(context: Context, uid: String) {

    private val store = context.applicationContext.foundItemDrafts
    private val categoryKey = stringPreferencesKey("${uid}_category")
    private val descriptionKey = stringPreferencesKey("${uid}_description")
    private val locationKey = stringPreferencesKey("${uid}_location")
    private val timeKey = stringPreferencesKey("${uid}_time")

    suspend fun save(category: String, description: String, location: String, time: String) {
        if (FoundItemDraft(category, description, location, time).isEmpty()) {
            clear()
            return
        }

        store.edit { preferences ->
            preferences[categoryKey] = category
            preferences[descriptionKey] = description
            preferences[locationKey] = location
            preferences[timeKey] = time
        }
    }

    suspend fun read(): FoundItemDraft? {
        val preferences = store.data.first()
        val draft = FoundItemDraft(
            category = preferences[categoryKey].orEmpty(),
            description = preferences[descriptionKey].orEmpty(),
            location = preferences[locationKey].orEmpty(),
            time = preferences[timeKey].orEmpty()
        )
        return draft.takeUnless { it.isEmpty() }
    }

    suspend fun clear() {
        store.edit { preferences ->
            preferences.remove(categoryKey)
            preferences.remove(descriptionKey)
            preferences.remove(locationKey)
            preferences.remove(timeKey)
        }
    }
}

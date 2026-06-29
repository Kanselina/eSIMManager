package com.esim.manager.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class ESimRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "esim_manager_prefs"
        private const val KEY_ESIMS = "key_esims"
    }

    fun getAllESims(): List<ESimModel> {
        val jsonStr = prefs.getString(KEY_ESIMS, null) ?: return emptyList()
        val list = mutableListOf<ESimModel>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                list.add(ESimModel.fromJsonObject(jsonObject))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveESim(esim: ESimModel) {
        val currentList = getAllESims().toMutableList()
        val index = currentList.indexOfFirst { it.id == esim.id }
        if (index != -1) {
            currentList[index] = esim
        } else {
            currentList.add(esim)
        }
        saveAll(currentList)
    }

    fun deleteESim(id: String) {
        val currentList = getAllESims().toMutableList()
        currentList.removeAll { it.id == id }
        saveAll(currentList)
    }

    private fun saveAll(list: List<ESimModel>) {
        val jsonArray = JSONArray()
        for (esim in list) {
            jsonArray.put(esim.toJsonObject())
        }
        prefs.edit().putString(KEY_ESIMS, jsonArray.toString()).apply()
    }
}

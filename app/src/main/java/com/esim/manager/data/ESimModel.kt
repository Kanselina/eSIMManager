package com.esim.manager.data

import org.json.JSONObject
import java.util.UUID

data class ESimModel(
    val id: String = UUID.randomUUID().toString(),
    val provider: String,
    val country: String,
    val balance: Double,
    val currency: String,
    val expiryDate: String, // YYYY-MM-DD
    val note: String = "",
    val isActive: Boolean = true,
    val reminderDays: Int = 3, // 提前几天提醒 (0表示当天，-1表示不提醒)
    val totalData: String = "", // 总流量, e.g., "10 GB"
    val remainingData: String = "", // 剩余流量, e.g., "4.2 GB"
    val phoneNumber: String = "" // 电话号码 (选填)
) {
    fun toJsonObject(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("provider", provider)
        json.put("country", country)
        json.put("balance", balance)
        json.put("currency", currency)
        json.put("expiryDate", expiryDate)
        json.put("note", note)
        json.put("isActive", isActive)
        json.put("reminderDays", reminderDays)
        json.put("totalData", totalData)
        json.put("remainingData", remainingData)
        json.put("phoneNumber", phoneNumber)
        return json
    }

    companion object {
        fun fromJsonObject(json: JSONObject): ESimModel {
            return ESimModel(
                id = json.optString("id", UUID.randomUUID().toString()),
                provider = json.optString("provider", ""),
                country = json.optString("country", ""),
                balance = json.optDouble("balance", 0.0),
                currency = json.optString("currency", "USD"),
                expiryDate = json.optString("expiryDate", ""),
                note = json.optString("note", ""),
                isActive = json.optBoolean("isActive", true),
                reminderDays = json.optInt("reminderDays", 3),
                totalData = json.optString("totalData", ""),
                remainingData = json.optString("remainingData", ""),
                phoneNumber = json.optString("phoneNumber", "")
            )
        }
    }
}

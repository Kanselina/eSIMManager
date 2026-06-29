package com.esim.manager.widget

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.esim.manager.R
import com.esim.manager.data.ESimModel
import org.json.JSONArray
import java.util.Locale

class ESimWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ESimWidgetFactory(applicationContext)
    }
}

class ESimWidgetFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("esim_manager_prefs", Context.MODE_PRIVATE)
    }
    private var esimsList: List<ESimModel> = emptyList()

    override fun onCreate() {
        loadData()
    }

    override fun onDataSetChanged() {
        loadData()
    }

    override fun onDestroy() {
        esimsList = emptyList()
    }

    private fun loadData() {
        val jsonStr = prefs.getString("key_esims", null)
        if (jsonStr.isNullOrEmpty()) {
            esimsList = emptyList()
            return
        }
        val list = mutableListOf<ESimModel>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                list.add(ESimModel.fromJsonObject(jsonArray.getJSONObject(i)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        esimsList = list
    }

    override fun getCount(): Int = esimsList.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position < 0 || position >= esimsList.size) {
            return RemoteViews(context.packageName, R.layout.widget_item_esim)
        }
        val esim = esimsList[position]
        val views = RemoteViews(context.packageName, R.layout.widget_item_esim)

        // ── 1. Read Theme Style for Customizable Item Backgrounds ───────
        val bgStyle = prefs.getString("widget_bg_style", "blue") ?: "blue"

        // Map colors & card backgrounds based on chosen style
        val cardBgResId: Int
        val primaryTextColor: Int
        val secondaryTextColor: Int
        val balanceColor: Int

        when (bgStyle) {
            "dark" -> {
                cardBgResId = R.drawable.bg_card_esim_dark
                primaryTextColor = 0xFFFFFFFF.toInt() // White
                secondaryTextColor = 0xFFB0B0B0.toInt() // Light Grey
                balanceColor = 0xFF64B5F6.toInt() // Light Blue Accent
            }
            "white" -> {
                cardBgResId = R.drawable.bg_card_esim_blue // Soft blue cards on white container
                primaryTextColor = 0xFF212121.toInt() // Dark Charcoal
                secondaryTextColor = 0xFF757575.toInt() // Medium Grey
                balanceColor = 0xFF1A73E8.toInt() // Google Blue
            }
            "green" -> {
                cardBgResId = R.drawable.bg_card_esim_green
                primaryTextColor = 0xFF1B5E20.toInt() // Deep Green
                secondaryTextColor = 0xFF4E704E.toInt() // Moss Green
                balanceColor = 0xFF2E7D32.toInt() // Rich Green
            }
            "orange" -> {
                cardBgResId = R.drawable.bg_card_esim_orange
                primaryTextColor = 0xFF5D4037.toInt() // Deep Warm Brown
                secondaryTextColor = 0xFF795548.toInt() // Brown
                balanceColor = 0xFFE65100.toInt() // Dark Orange
            }
            "purple" -> {
                cardBgResId = R.drawable.bg_card_esim_purple
                primaryTextColor = 0xFF4A148C.toInt() // Deep Purple
                secondaryTextColor = 0xFF7B1FA2.toInt() // Purple
                balanceColor = 0xFF8E24AA.toInt() // Medium Purple
            }
            "pink" -> {
                cardBgResId = R.drawable.bg_card_esim_pink
                primaryTextColor = 0xFF880E4F.toInt() // Deep Pink
                secondaryTextColor = 0xFFC2185B.toInt() // Pink
                balanceColor = 0xFFE91E63.toInt() // Hot Pink
            }
            "yellow" -> {
                cardBgResId = R.drawable.bg_card_esim_yellow
                primaryTextColor = 0xFF5D4037.toInt() // Dark Amber Brown
                secondaryTextColor = 0xFF795548.toInt()
                balanceColor = 0xFFF57F17.toInt() // Golden Yellow
            }
            "teal" -> {
                cardBgResId = R.drawable.bg_card_esim_teal
                primaryTextColor = 0xFF004D40.toInt() // Deep Teal
                secondaryTextColor = 0xFF00796B.toInt()
                balanceColor = 0xFF009688.toInt() // Teal
            }
            "grey" -> {
                cardBgResId = R.drawable.bg_card_esim_grey
                primaryTextColor = 0xFF212121.toInt() // Charcoal
                secondaryTextColor = 0xFF616161.toInt()
                balanceColor = 0xFF455A64.toInt() // Blue Grey
            }
            else -> { // "blue" style
                cardBgResId = R.drawable.bg_card_esim_white // Elegant white card over light blue container
                primaryTextColor = 0xFF212121.toInt()
                secondaryTextColor = 0xFF757575.toInt()
                balanceColor = 0xFF1A73E8.toInt()
            }
        }

        // Apply item backgrounds & colors
        views.setInt(R.id.widgetItemRoot, "setBackgroundResource", cardBgResId)
        views.setTextColor(R.id.tvWidgetProvider, primaryTextColor)
        views.setTextColor(R.id.tvWidgetCountry, secondaryTextColor)
        views.setTextColor(R.id.tvWidgetBalance, balanceColor)
        views.setTextColor(R.id.tvWidgetExpiry, secondaryTextColor)

        // Set content
        views.setTextViewText(R.id.tvWidgetProvider, esim.provider)
        views.setTextViewText(R.id.tvWidgetCountry, "📍 ${esim.country}")
        views.setTextViewText(
            R.id.tvWidgetBalance,
            String.format(Locale.getDefault(), "%.2f %s", esim.balance, esim.currency)
        )
        views.setTextViewText(R.id.tvWidgetExpiry, "到期: ${esim.expiryDate}")

        // ── 2. Phone Number display (NEW!) ───────────────────────────────
        if (esim.phoneNumber.isNotEmpty()) {
            views.setViewVisibility(R.id.tvWidgetPhoneNumber, View.VISIBLE)
            views.setTextViewText(R.id.tvWidgetPhoneNumber, "📞 号码: ${esim.phoneNumber}")
            views.setTextColor(R.id.tvWidgetPhoneNumber, balanceColor)
        } else {
            views.setViewVisibility(R.id.tvWidgetPhoneNumber, View.GONE)
        }

        // ── 3. Bind Total and Remaining Data ─────────────────────────────
        if (esim.totalData.isNotEmpty() || esim.remainingData.isNotEmpty()) {
            views.setViewVisibility(R.id.widgetDataLayout, View.VISIBLE)
            views.setTextViewText(R.id.tvWidgetData, esim.totalData.ifEmpty { "--" })
            views.setTextViewText(R.id.tvWidgetDataRemaining, esim.remainingData.ifEmpty { "--" })
            
            // Adjust label text colors
            views.setTextColor(R.id.tvWidgetDataLabel, secondaryTextColor)
            views.setTextColor(R.id.tvWidgetDataRemainingLabel, secondaryTextColor)
            views.setTextColor(R.id.tvWidgetDataSep, secondaryTextColor)
            
            val remainingColor = when (bgStyle) {
                "dark" -> 0xFF81C784.toInt() // Soft green
                "orange" -> 0xFFE65100.toInt()
                "purple" -> 0xFF8E24AA.toInt()
                "pink" -> 0xFFE91E63.toInt()
                "yellow" -> 0xFFF57F17.toInt()
                "teal" -> 0xFF009688.toInt()
                else -> 0xFF2E7D32.toInt()
            }
            views.setTextColor(R.id.tvWidgetDataRemaining, remainingColor)
            views.setTextColor(R.id.tvWidgetData, primaryTextColor)
        } else {
            views.setViewVisibility(R.id.widgetDataLayout, View.GONE)
        }

        // Status Colors & Texts
        if (esim.isActive) {
            views.setTextViewText(R.id.tvWidgetStatus, "使用中")
            val statusColor = when (bgStyle) {
                "dark" -> 0xFF81C784.toInt()
                "green" -> 0xFF1B5E20.toInt()
                "orange" -> 0xFFE65100.toInt()
                "purple" -> 0xFF4A148C.toInt()
                "pink" -> 0xFF880E4F.toInt()
                "yellow" -> 0xFFF57F17.toInt()
                "teal" -> 0xFF004D40.toInt()
                else -> 0xFF137333.toInt()
            }
            views.setTextColor(R.id.tvWidgetStatus, statusColor)
        } else {
            views.setTextViewText(R.id.tvWidgetStatus, "已禁用")
            val statusColor = if (bgStyle == "dark") 0xFFB0B0B0.toInt() else 0xFF5F6368.toInt()
            views.setTextColor(R.id.tvWidgetStatus, statusColor)
        }

        // ── 4. Note display (NEW!) ───────────────────────────────────────
        if (esim.note.isNotEmpty()) {
            views.setViewVisibility(R.id.tvWidgetNote, View.VISIBLE)
            views.setTextViewText(R.id.tvWidgetNote, "备注: ${esim.note}")
            if (bgStyle == "dark") {
                views.setInt(R.id.tvWidgetNote, "setBackgroundColor", 0xFF3E2723.toInt()) // Dark brown/amber background
                views.setTextColor(R.id.tvWidgetNote, 0xFFFFCC80.toInt()) // Soft orange text
            } else {
                views.setInt(R.id.tvWidgetNote, "setBackgroundColor", 0xFFFFF9C4.toInt()) // Soft yellow background
                views.setTextColor(R.id.tvWidgetNote, 0xFF5D4037.toInt()) // Dark brown text
            }
        } else {
            views.setViewVisibility(R.id.tvWidgetNote, View.GONE)
        }

        // Fill-in intent for item click
        val fillInIntent = Intent().apply {
            putExtra(com.esim.manager.ui.AddEditActivity.EXTRA_ESIM_ID, esim.id)
        }
        views.setOnClickFillInIntent(R.id.widgetItemRoot, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
}

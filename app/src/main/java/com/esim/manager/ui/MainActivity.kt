package com.esim.manager.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.esim.manager.R
import com.esim.manager.data.ESimModel
import com.esim.manager.data.ESimRepository
import com.esim.manager.databinding.ActivityMainBinding
import com.esim.manager.widget.ESimWidgetProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: ESimRepository
    private lateinit var adapter: ESimAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        repository = ESimRepository(this)

        setupRecyclerView()
        setupWidgetThemeListeners()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }

        // Refresh widget on start
        sendWidgetUpdateBroadcast()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ESimAdapter(
            esims = emptyList(),
            onEditClick = { esim ->
                val intent = Intent(this, AddEditActivity::class.java).apply {
                    putExtra(AddEditActivity.EXTRA_ESIM_ID, esim.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { esim ->
                repository.deleteESim(esim.id)
                Toast.makeText(this, "eSIM 已删除", Toast.LENGTH_SHORT).show()
                refreshData()
                sendWidgetUpdateBroadcast()
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun setupWidgetThemeListeners() {
        val prefs = getSharedPreferences("esim_manager_prefs", Context.MODE_PRIVATE)

        binding.btnBgBlue.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "blue").apply()
            Toast.makeText(this, "组件背景已设为 [极光蓝]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }

        binding.btnBgWhite.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "white").apply()
            Toast.makeText(this, "组件背景已设为 [雅致白]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }

        binding.btnBgDark.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "dark").apply()
            Toast.makeText(this, "组件背景已设为 [深邃黑]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }

        binding.btnBgGreen.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "green").apply()
            Toast.makeText(this, "组件背景已设为 [新绿]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }

        binding.btnBgOrange.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "orange").apply()
            Toast.makeText(this, "组件背景已设为 [活力橙]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }

        binding.btnBgPurple.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "purple").apply()
            Toast.makeText(this, "组件背景已设为 [浪漫紫]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }

        binding.btnBgPink.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "pink").apply()
            Toast.makeText(this, "组件背景已设为 [娇嫩粉]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }

        binding.btnBgYellow.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "yellow").apply()
            Toast.makeText(this, "组件背景已设为 [温暖黄]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }

        binding.btnBgTeal.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "teal").apply()
            Toast.makeText(this, "组件背景已设为 [翡翠黛]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }

        binding.btnBgGrey.setOnClickListener {
            prefs.edit().putString("widget_bg_style", "grey").apply()
            Toast.makeText(this, "组件背景已设为 [高级灰]", Toast.LENGTH_SHORT).show()
            sendWidgetUpdateBroadcast()
        }
    }

    private fun refreshData() {
        val list = repository.getAllESims()
        if (list.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            adapter.updateData(list)
        }
    }

    private fun sendWidgetUpdateBroadcast() {
        val intent = Intent(this, ESimWidgetProvider::class.java).apply {
            action = ESimWidgetProvider.ACTION_REFRESH
        }
        sendBroadcast(intent)
    }
}

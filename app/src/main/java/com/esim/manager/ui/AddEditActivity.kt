package com.esim.manager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esim.manager.R
import com.esim.manager.data.ESimModel
import com.esim.manager.data.ESimRepository
import com.esim.manager.databinding.ActivityAddEditBinding
import com.esim.manager.utils.ReminderManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBinding
    private lateinit var repository: ESimRepository
    private var esimId: String? = null
    private val calendar = Calendar.getInstance()

    // 31 Preset eSIM Providers + Custom
    private val commonProviders = listOf(
        "Airalo", "Banana", "AlmondSIM", "AT&T", "au", "Club Sim", 
        "CTM（中国电信澳门）", "Deutsche Telekom（德国电信）", "Docomo", "EE", 
        "Giffgaff", "Holafly", "Mint Mobile", "Nomad", "O2", "Optus", 
        "Orange（橙子电信）", "Rakuten Mobile（乐天）", "Roamless", "Saily", 
        "SmarTone", "SoftBank（软银）", "Telstra", "Three", "T-Mobile", 
        "Trifa", "Ubigi", "Verizon", "Visible", "Vodafone（沃达丰）", "3HK", 
        "自定义..."
    )
    
    private val commonCountries = listOf("全球 (Global)", "中国大陆 (China)", "香港 (Hong Kong)", "澳门 (Macau)", "台湾 (Taiwan)", "日本 (Japan)", "韩国 (South Korea)", "美国 (United States)", "新加坡 (Singapore)", "泰国 (Thailand)", "欧洲多国 (Europe)", "自定义...")
    private val commonCurrencies = listOf("CNY", "USD", "EUR", "HKD", "JPY", "SGD", "GBP")
    private val reminderOptions = listOf("不提醒", "当天提醒", "提前 1 天", "提前 3 天", "提前 7 天", "提前 15 天")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        repository = ESimRepository(this)
        esimId = intent.getStringExtra(EXTRA_ESIM_ID)

        setupDropdowns()
        setupDatePicker()

        if (esimId != null) {
            title = getString(R.string.edit_esim)
            loadESimData(esimId!!)
        } else {
            title = getString(R.string.add_esim)
            calendar.add(Calendar.DAY_OF_MONTH, 30)
            updateDateLabel()
        }

        binding.btnSave.setOnClickListener { saveESim() }
    }

    private fun setupDropdowns() {
        // Material Dropdown for Providers
        val providerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, commonProviders)
        binding.actvProvider.setAdapter(providerAdapter)
        binding.actvProvider.setOnItemClickListener { parent, _, position, _ ->
            val selection = parent.getItemAtPosition(position) as String
            if (selection == "自定义...") {
                binding.tilCustomProvider.visibility = View.VISIBLE
            } else {
                binding.tilCustomProvider.visibility = View.GONE
            }
        }

        // Material Dropdown for Countries
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, commonCountries)
        binding.actvCountry.setAdapter(countryAdapter)
        binding.actvCountry.setOnItemClickListener { parent, _, position, _ ->
            val selection = parent.getItemAtPosition(position) as String
            if (selection == "自定义...") {
                binding.tilCustomCountry.visibility = View.VISIBLE
            } else {
                binding.tilCustomCountry.visibility = View.GONE
            }
        }

        // Material Dropdown for Currency
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, commonCurrencies)
        binding.actvCurrency.setAdapter(currencyAdapter)

        // Material Dropdown for Reminder days
        val reminderAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reminderOptions)
        binding.actvReminder.setAdapter(reminderAdapter)
    }

    private fun setupDatePicker() {
        binding.etExpiryDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateLabel()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateLabel() {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.etExpiryDate.setText(format.format(calendar.time))
    }

    private fun loadESimData(id: String) {
        val esim = repository.getAllESims().find { it.id == id } ?: return

        // Provider select
        if (commonProviders.contains(esim.provider)) {
            binding.actvProvider.setText(esim.provider, false)
            binding.tilCustomProvider.visibility = View.GONE
        } else {
            binding.actvProvider.setText("自定义...", false)
            binding.tilCustomProvider.visibility = View.VISIBLE
            binding.etCustomProvider.setText(esim.provider)
        }

        // Country select
        if (commonCountries.contains(esim.country)) {
            binding.actvCountry.setText(esim.country, false)
            binding.tilCustomCountry.visibility = View.GONE
        } else {
            binding.actvCountry.setText("自定义...", false)
            binding.tilCustomCountry.visibility = View.VISIBLE
            binding.etCustomCountry.setText(esim.country)
        }

        // Currency select
        binding.actvCurrency.setText(esim.currency, false)

        // Balance, Expiry, Notes, Status
        binding.etBalance.setText(esim.balance.toString())
        binding.etExpiryDate.setText(esim.expiryDate)
        binding.etNotes.setText(esim.note)
        binding.switchActive.isChecked = esim.isActive

        // Total and Remaining Data
        binding.etTotalData.setText(esim.totalData)
        binding.etRemainingData.setText(esim.remainingData)

        // Phone Number (NEW!)
        binding.etPhoneNumber.setText(esim.phoneNumber)

        // Reminder text mapping
        val rDays = esim.reminderDays
        val reminderText = when (rDays) {
            -1 -> "不提醒"
            0 -> "当天提醒"
            1 -> "提前 1 天"
            3 -> "提前 3 天"
            7 -> "提前 7 天"
            15 -> "提前 15 天"
            else -> "提前 $rDays 天"
        }
        binding.actvReminder.setText(reminderText, false)

        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(esim.expiryDate)
            if (date != null) {
                calendar.time = date
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveESim() {
        val selectedProvider = binding.actvProvider.text.toString().trim()
        val provider = if (selectedProvider == "自定义...") {
            binding.etCustomProvider.text.toString().trim()
        } else {
            selectedProvider
        }

        if (provider.isEmpty()) {
            binding.etCustomProvider.error = "请输入服务商名称"
            return
        }

        val selectedCountry = binding.actvCountry.text.toString().trim()
        val country = if (selectedCountry == "自定义...") {
            binding.etCustomCountry.text.toString().trim()
        } else {
            selectedCountry
        }

        if (country.isEmpty()) {
            binding.etCustomCountry.error = "请输入国家/地区"
            return
        }

        val balanceStr = binding.etBalance.text.toString().trim()
        val balance = balanceStr.toDoubleOrNull() ?: 0.0

        val currency = binding.actvCurrency.text.toString().trim()
        if (currency.isEmpty()) {
            binding.actvCurrency.error = "请选择币种"
            return
        }

        val expiryDate = binding.etExpiryDate.text.toString().trim()
        if (expiryDate.isEmpty()) {
            binding.etExpiryDate.error = "请选择到期日期"
            return
        }

        val totalData = binding.etTotalData.text.toString().trim()
        val remainingData = binding.etRemainingData.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()

        // Remind days parsing
        val reminderSelection = binding.actvReminder.text.toString().trim()
        val reminderDays = when (reminderSelection) {
            "不提醒" -> -1
            "当天提醒" -> 0
            "提前 1 天" -> 1
            "提前 3 天" -> 3
            "提前 7 天" -> 7
            "提前 15 天" -> 15
            else -> 3
        }

        val note = binding.etNotes.text.toString().trim()
        val isActive = binding.switchActive.isChecked

        val esim = ESimModel(
            id = esimId ?: java.util.UUID.randomUUID().toString(),
            provider = provider,
            country = country,
            balance = balance,
            currency = currency,
            expiryDate = expiryDate,
            note = note,
            isActive = isActive,
            reminderDays = reminderDays,
            totalData = totalData,
            remainingData = remainingData,
            phoneNumber = phoneNumber
        )

        repository.saveESim(esim)
        
        // Handle alarms
        if (isActive) {
            ReminderManager.scheduleReminder(this, esim)
        } else {
            ReminderManager.cancelReminder(this, esim.id)
        }

        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val EXTRA_ESIM_ID = "extra_esim_id"
    }
}

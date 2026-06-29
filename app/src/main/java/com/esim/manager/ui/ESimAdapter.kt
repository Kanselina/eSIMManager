package com.esim.manager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esim.manager.R
import com.esim.manager.data.ESimModel
import java.util.Locale

class ESimAdapter(
    private var esims: List<ESimModel>,
    private val onEditClick: (ESimModel) -> Unit,
    private val onDeleteClick: (ESimModel) -> Unit
) : RecyclerView.Adapter<ESimAdapter.ESimViewHolder>() {

    fun updateData(newList: List<ESimModel>) {
        esims = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ESimViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_esim, parent, false)
        return ESimViewHolder(view)
    }

    override fun onBindViewHolder(holder: ESimViewHolder, position: Int) {
        holder.bind(esims[position], onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = esims.size

    class ESimViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProvider: TextView = itemView.findViewById(R.id.tvProvider)
        private val tvCountry: TextView = itemView.findViewById(R.id.tvCountry)
        private val tvBalance: TextView = itemView.findViewById(R.id.tvBalance)
        private val tvExpiryDate: TextView = itemView.findViewById(R.id.tvExpiryDate)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        private val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)
        
        // Data flow views
        private val dataLayout: View = itemView.findViewById(R.id.dataLayout)
        private val tvDataUsage: TextView = itemView.findViewById(R.id.tvDataUsage)
        private val tvDataRemaining: TextView = itemView.findViewById(R.id.tvDataRemaining)
        
        private val btnEdit: View = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: View = itemView.findViewById(R.id.btnDelete)

        fun bind(
            esim: ESimModel,
            onEditClick: (ESimModel) -> Unit,
            onDeleteClick: (ESimModel) -> Unit
        ) {
            tvProvider.text = esim.provider
            tvCountry.text = esim.country
            tvBalance.text = String.format(Locale.getDefault(), "%.2f %s", esim.balance, esim.currency)
            tvExpiryDate.text = "到期日期: ${esim.expiryDate}"
            
            // Set up phone number (NEW!)
            if (esim.phoneNumber.isNotEmpty()) {
                tvPhoneNumber.visibility = View.VISIBLE
                tvPhoneNumber.text = "📞 电话号码: ${esim.phoneNumber}"
            } else {
                tvPhoneNumber.visibility = View.GONE
            }

            // Set up total and remaining data
            if (esim.totalData.isNotEmpty() || esim.remainingData.isNotEmpty()) {
                dataLayout.visibility = View.VISIBLE
                tvDataUsage.text = esim.totalData.ifEmpty { "--" }
                tvDataRemaining.text = esim.remainingData.ifEmpty { "--" }
            } else {
                dataLayout.visibility = View.GONE
            }

            if (esim.note.isNotEmpty()) {
                tvNote.visibility = View.VISIBLE
                tvNote.text = esim.note
            } else {
                tvNote.visibility = View.GONE
            }

            if (esim.isActive) {
                tvStatus.text = itemView.context.getString(R.string.active)
                tvStatus.setBackgroundResource(R.drawable.bg_status_active)
                tvStatus.setTextColor(itemView.context.getColor(R.color.status_active_text))
            } else {
                tvStatus.text = itemView.context.getString(R.string.inactive)
                tvStatus.setBackgroundResource(R.drawable.bg_status_inactive)
                tvStatus.setTextColor(itemView.context.getColor(R.color.status_inactive_text))
            }

            btnEdit.setOnClickListener { onEditClick(esim) }
            btnDelete.setOnClickListener { onDeleteClick(esim) }
        }
    }
}

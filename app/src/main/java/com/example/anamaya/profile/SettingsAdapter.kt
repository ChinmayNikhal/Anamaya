package com.yourapp.anamaya.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anamaya.R
import com.example.anamaya.profile.SettingItem
import com.example.anamaya.profile.SettingType

class SettingsAdapter(
    private val items: List<SettingItem>,
    private val onItemClicked: (SettingItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    inner class SettingsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val themeToggle: Switch = view.findViewById(R.id.themeToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_item_setting, parent, false)
        return SettingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title

        when (item.type) {
            SettingType.TOGGLE -> {
                holder.themeToggle.visibility = View.VISIBLE
                holder.themeToggle.isChecked = item.isToggled
                holder.themeToggle.setOnCheckedChangeListener { _, isChecked ->
                    item.isToggled = isChecked
                    // Handle theme change here (dark/light mode)
                }
                holder.itemView.setOnClickListener(null) // No click needed
            }
            else -> {
                holder.themeToggle.visibility = View.GONE
                holder.itemView.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

package com.example.anamaya.profile

enum class SettingType {
    NAVIGATE, TOGGLE, DIALOG
}

data class SettingItem(
    val title: String,
    val type: SettingType,
    var isToggled: Boolean = false // used only for TOGGLE type
)

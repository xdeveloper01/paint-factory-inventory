package com.paintfactory.inventory.domain.util

import android.content.Context
import java.text.NumberFormat
import java.util.*

class LocalizationManager {
    private val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    private val standardDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    
    fun formatNumberForDisplay(context: Context, value: Number): String {
        val isArabic = context.resources.configuration.locale.language == "ar"
        val formatted = String.format(Locale("ar"), "%,.2f", value)
        
        return if (isArabic) {
            standardToEasternArabic(formatted)
        } else {
            String.format(Locale.US, "%,.2f", value)  // FIXED: removed extra )
        }
    }
    
    private fun standardToEasternArabic(text: String): String {
        return text.map { char ->
            when (char) {
                in '0'..'9' -> arabicDigits[char - '0']
                ',' -> '،'
                '.' -> '٫'
                else -> char
            }
        }.joinToString("")
    }
    
    fun getLocalizedString(context: Context, enText: String, arText: String?): String {
        return if (context.resources.configuration.locale.language == "ar" && !arText.isNullOrEmpty()) {
            arText
        } else {
            enText
        }
    }
}

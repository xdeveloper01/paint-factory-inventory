package com.paintfactory.inventory.domain.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.paintfactory.inventory.data.local.entities.InventoryLot
import com.paintfactory.inventory.data.local.entities.RawMaterial
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ExportUtil(private val context: Context) {
    
    fun exportInventoryToCsv(materials: List<RawMaterial>, lots: List<InventoryLot>): File {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fileName = "Inventory_${dateFormat.format(Date())}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        
        FileWriter(file).use { writer ->
            // Header
            writer.append("SKU,Material Name (EN),Material Name (AR),Category," +
                         "Lot Number,Quantity,Unit,Expiry Date,Status\n")
            
            // Data
            lots.forEach { lot ->
                val material = materials.find { it.id == lot.materialId }
                material?.let {
                    writer.append("${it.sku},")
                    writer.append("\"${it.nameEn}\",")
                    writer.append("\"${it.nameAr ?: ""}\",")
                    writer.append("${it.category},")
                    writer.append("${lot.lotNumber},")
                    writer.append("${lot.quantityCurrent},")
                    writer.append("${it.defaultUnit},")
                    writer.append("${dateFormat.format(Date(lot.expiryDate))},")
                    writer.append("${lot.status}\n")
                }
            }
        }
        
        return file
    }
    
    fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Inventory Report"))
    }
}

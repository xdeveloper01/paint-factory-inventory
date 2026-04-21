package com.paintfactory.inventory.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintfactory.inventory.data.local.dao.MaterialDao
import com.paintfactory.inventory.data.local.entities.RawMaterial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialDetailViewModel @Inject constructor(
    private val materialDao: MaterialDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val materialId: String = savedStateHandle.get<String>("materialId").orEmpty()

    private val _material = MutableStateFlow<RawMaterial?>(null)
    val material: StateFlow<RawMaterial?> = _material.asStateFlow()

    init {
        if (materialId.isNotBlank()) {
            viewModelScope.launch {
                _material.value = materialDao.getById(materialId)
            }
        }
    }
}

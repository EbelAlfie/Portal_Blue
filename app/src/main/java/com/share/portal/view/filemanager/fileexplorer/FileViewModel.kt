package com.share.portal.view.filemanager.fileexplorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.share.portal.domain.FileUseCaseImpl
import com.share.portal.domain.models.FileParam
import com.share.portal.view.filemanager.fileexplorer.FileUiState.FileExplore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class FileViewModel @Inject constructor(
  private val fileUseCase: FileUseCaseImpl
) : ViewModel() {

  private val _fileUiState = MutableStateFlow<FileUiState>(FileUiState.Loading)
  val fileUiState: StateFlow<FileUiState> = _fileUiState.asStateFlow()

  init {
    getAllChildrenFiles()
  }

  fun getAllChildrenFiles(rootFile: String = FileParam.EXTERNAL.pathName) {
    val rootPath = rootFile.ifBlank { FileParam.EXTERNAL.pathName }
    viewModelScope.launch {
      try {
        val data = fileUseCase.getAllExternalFiles(rootPath)
        _fileUiState.update { oldState ->
          if (oldState is FileUiState.FileExplore) {
            oldState.copy(
              allFiles = oldState.allFiles + data
            )
          } else {
            FileUiState.FileExplore(
              allFiles = mutableListOf(data)
            )
          }
        }
      } catch (error: Exception) {
        _fileUiState.value = FileUiState.Error(
          cause = error
        )
      }
    }
  }

  fun canGoBack(): Boolean {
    return when (_fileUiState.value) {
      is FileUiState.FileExplore ->
        (_fileUiState.value as FileExplore).allFiles.size > 1
      is FileUiState.FileSelect -> true
      else -> false
    }
  }

  fun goBack() {
    _fileUiState.update { oldState ->
      when (oldState) {
        is FileUiState.FileExplore -> {
          val popedFile = oldState.allFiles.toMutableList()
          popedFile.removeLastOrNull()
          oldState.copy(
            allFiles = popedFile
          )
        }
        is FileUiState.FileSelect ->
          FileUiState.FileExplore(
            allFiles = oldState.allFiles
          )
        else -> oldState
      }
    }
  }

  fun onFileClicked(filePath: String) {
    if (_fileUiState.value is FileUiState.FileExplore)
      getAllChildrenFiles(rootFile = filePath)
  }

  fun switchOperationMode(filePosition: Int) {
    _fileUiState.update { oldState ->
      if (oldState is FileUiState.FileExplore) {
        FileUiState.FileSelect(
          allFiles = oldState.allFiles.toMutableList(),
          selectedIndices = mutableListOf(filePosition)
        )
      } else
        oldState
    }
  }

  fun selectFile(filePosition: Int) {
    _fileUiState.update { oldState ->
      (oldState as FileUiState.FileSelect).let {
        val selectedIndex = it.selectedIndices + filePosition
        it.copy(
          selectedIndices = selectedIndex.toMutableList()
        )
      }
    }
  }

}
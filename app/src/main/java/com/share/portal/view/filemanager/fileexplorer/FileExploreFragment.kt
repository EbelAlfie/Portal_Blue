package com.share.portal.view.filemanager.fileexplorer

import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.share.portal.databinding.FragmentFileExplorerBinding
import com.share.portal.domain.models.FileTreeEntity
import com.share.portal.view.filemanager.fileexplorer.adapter.FileAdapter
import com.share.portal.view.filemanager.fileexplorer.adapter.ParentAdapter
import com.share.portal.view.filemanager.fileexplorer.model.FileData
import com.share.portal.view.filemanager.fileexplorer.model.FileExtension
import com.share.portal.view.general.ProgenitorFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class FileExploreFragment : ProgenitorFragment<FragmentFileExplorerBinding>() {

  @Inject
  lateinit var viewModel: FileViewModel

//  @Inject
//  lateinit var fileProcessor: FileProcessor

  private val fileAdapter: FileAdapter by lazy { FileAdapter() }
  private val parentAdapter: ParentAdapter by lazy { ParentAdapter() }

  override fun initBinding(layoutInflater: LayoutInflater) =
    FragmentFileExplorerBinding.inflate(layoutInflater)

  override fun initFragment() {
    fragmentComponent.inject(this)
    registerObservers()
    setupView()
  }

  override fun onBackPressed() {
    if (viewModel.canGoBack()) viewModel.goBack()
    else requireActivity().finish()
  }

  private fun registerObservers() {
    lifecycleScope.launch {
      viewModel.fileUiState.collect { updateUiState(it) }
    }
  }

  private fun updateUiState(uiState: FileUiState) {
    when (uiState) {
      is FileUiState.Loading -> {} //Display loading screen
      is FileUiState.FileExplore ->
        loadData(uiState.allFiles.last())

      is FileUiState.Error ->
        showErrorDialog(uiState.cause)

      is FileUiState.FileSelect -> {}
        //fileAdapter.notifySelectedFile(uiState.selectedIndices)//notify adapter
    }
  }

  private fun setupView() {
    binding.run {
      rvParent.layoutManager =
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      rvParent.adapter = parentAdapter

      setupFileAdapter()
      rvFiles.layoutManager =
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      rvFiles.adapter = fileAdapter
    }
  }

  private fun setupFileAdapter() {
    fileAdapter.setFileListener(
      object : FileAdapter.FileListener() {
        override fun onFileClicked(filePath: String, filePosition: Int, extension: FileExtension) {
          super.onFileClicked(filePath, filePosition, extension)
          if (viewModel.fileUiState.value is FileUiState.FileExplore)
            viewModel.onFileClicked(filePath)
          if (viewModel.fileUiState.value is FileUiState.FileSelect)
            viewModel.selectFile(filePosition)
        }

        override fun onFileHold(filePosition: Int) {
          super.onFileHold(filePosition)
          if (viewModel.fileUiState.value is FileUiState.FileExplore)
            viewModel.switchOperationMode(filePosition)
          if (viewModel.fileUiState.value is FileUiState.FileSelect)
            viewModel.selectFile(filePosition)
        }
      }
    )
  }

  private fun loadData(data: FileTreeEntity) {
    parentAdapter.update(data.current)
    fileAdapter.updateList(FileData.store(data))
  }

  private fun showErrorDialog(throwable: Throwable?) {
    showToast(throwable?.message)
  }
}
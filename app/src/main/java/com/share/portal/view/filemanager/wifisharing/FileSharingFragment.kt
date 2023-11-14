package com.share.portal.view.filemanager.wifisharing

import android.util.Log
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.share.portal.databinding.FragmentFileSharingBinding
import com.share.portal.view.filemanager.MainActivity
import com.share.portal.view.filemanager.WifiPerantara
import com.share.portal.view.filemanager.wifisharing.adapter.PeerAdapter
import com.share.portal.view.general.ProgenitorFragment

class FileSharingFragment: ProgenitorFragment<FragmentFileSharingBinding>() {

  private val peerAdapter: PeerAdapter by lazy {
    PeerAdapter()
  }
  override fun initBinding(layoutInflater: LayoutInflater): FragmentFileSharingBinding =
    FragmentFileSharingBinding.inflate(layoutInflater)

  override fun initFragment() = setupFragment()

  private fun setupFragment() {
    fragmentComponent.inject(this)
    registerBackPress()
    setupView()
    getPeers()
  }

  private fun getPeers() {
    (requireActivity() as MainActivity).provideP2pService().setPeerListener {
      Log.d("BRITNEY PEERS", it.deviceList.toString())
      peerAdapter.submitPeers(it)
    }
  }

  private fun registerBackPress() {
    requireActivity().apply {
      onBackPressedDispatcher.addCallback(this@FileSharingFragment,
        object: OnBackPressedCallback(true) {
          override fun handleOnBackPressed() = finish()
        }
      )
    }
  }

  private fun setupView() {
    binding.apply {
      rvPeers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      rvPeers.adapter = peerAdapter
    }
  }

  override fun onResume() {
    super.onResume()
    getWifiPerantara()?.registerWifi()
  }

  override fun onPause() {
    super.onPause()
    getWifiPerantara()?.unregisterWifi()
  }

  private fun getWifiPerantara(): WifiPerantara? =
    requireActivity() as? WifiPerantara

}
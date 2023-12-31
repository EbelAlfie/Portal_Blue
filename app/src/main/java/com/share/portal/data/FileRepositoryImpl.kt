package com.share.portal.data

import com.share.portal.data.datasource.OfflineDataSourceImpl
import com.share.portal.data.datasource.OnlineDataSourceImpl
import com.share.portal.data.repository.FileRepository
import com.share.portal.domain.models.FileTreeEntity
import java.io.File
import java.net.InetAddress
import java.net.InetSocketAddress
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
  private val offlineSource: OfflineDataSourceImpl,
  private val onlineSource: OnlineDataSourceImpl
  )
  : FileRepository {

  override suspend fun getAllExternalFiles(rootPath: String): FileTreeEntity {
    val response = offlineSource.getAllExternalFiles(rootPath)
    if (response.data != null) return response.data
    else throw Throwable(response.error)
  }

  fun onConnectedWithClient() {

  }

  fun onReceiveFileRequest() {

  }

  override fun sendFile(file: File) {

  }

  override fun receiveFile() {

  }

  override suspend fun connectWithClient(address: InetSocketAddress): Boolean {
    val response = onlineSource.requestClientConnection(address)
    if (response.data != null) return response.data
    else throw Throwable(response.error)
  }

  override suspend fun establishAsServer(): InetAddress {
    return onlineSource.establishWSServer()
  }

}
package com.share.portal.domain.models

import java.io.File

data class ParentFile (
  val path: String
) {
  companion object {
    fun createParent(parentFile: File): ParentFile {
      return ParentFile(
        path = parentFile.path
      )
    }
  }
}

data class FileTreeEntity(
  val current: ParentFile,
  val child: List<File>
) {
  companion object {
    fun createFileTree(
      current: File,
      child: List<File>
    ): FileTreeEntity {
      return FileTreeEntity(
        current = ParentFile.createParent(current),
        child = child
      )
    }
  }
}

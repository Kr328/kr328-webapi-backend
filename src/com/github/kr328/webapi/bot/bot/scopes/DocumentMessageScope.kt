package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.network.updates.Document
import com.github.kr328.webapi.bot.bot.network.updates.File

interface DocumentMessageScope : MessageScope {
    val document: Document

    suspend fun getDocumentFile(): File {
        return getFile(document.fileId)
    }

    suspend fun downloadDocument(): ByteArray {
        return downloadFile(document.fileId)
    }
}
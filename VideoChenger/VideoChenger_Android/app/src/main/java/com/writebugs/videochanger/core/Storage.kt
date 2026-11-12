package com.writebugs.videochanger.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import java.io.File

object Storage {

    /** 默认输出到 Movies/VideoChanger，方便相册/播放器直接看到。 */
    val defaultOutputDir: String
        get() = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            "VideoChanger",
        ).absolutePath

    fun hasAllFiles(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }

    fun requestAllFiles(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
        val appIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val result = runCatching { context.startActivity(appIntent) }
        if (result.isFailure) {
            runCatching {
                context.startActivity(
                    Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                )
            }
        }
    }

    fun ensureDir(path: String): File = File(path).apply { mkdirs() }

    /**
     * 在系统文件管理器里打开某个目录。
     *
     * Android 没有「打开文件夹」的标准 Intent，实践中最可靠的是用 DocumentsUI 的
     * document URI（primary:Download/xxx）；个别 ROM 不认，再退回 file:// + folder MIME。
     */
    fun openFolder(context: Context, dir: File): Boolean {
        if (!dir.exists()) return false
        val root = Environment.getExternalStorageDirectory().absolutePath
        val relative = dir.absolutePath.removePrefix("$root/")
        val docUri = Uri.parse(
            "content://com.android.externalstorage.documents/document/" +
                Uri.encode("primary:$relative"),
        )

        val attempts = listOf(
            // 1) 文档树选择器，直接定位到该目录（Android 8+ 支持 EXTRA_INITIAL_URI）
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, docUri)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            // 2) 直接以目录类型 VIEW
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(docUri, "vnd.android.document/directory")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            // 3) 只打开系统文件管理器 App 主界面
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_FILES)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
        for (intent in attempts) {
            val handled = runCatching {
                context.startActivity(intent)
            }.isSuccess
            if (handled) return true
        }
        return false
    }

    fun humanSize(bytes: Long): String = when {
        bytes >= 1L shl 30 -> String.format("%.2f GB", bytes / 1073741824.0)
        bytes >= 1L shl 20 -> String.format("%.1f MB", bytes / 1048576.0)
        bytes >= 1L shl 10 -> String.format("%.0f KB", bytes / 1024.0)
        else -> "$bytes B"
    }
}

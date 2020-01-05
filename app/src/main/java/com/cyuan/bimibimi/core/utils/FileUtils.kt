package com.cyuan.bimibimi.core.utils

import android.os.Environment
import android.util.Log
import com.cyuan.bimibimi.core.App
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object FileUtils {

    fun convertFileSize(size: Long): String {
        val kb: Long = 1024
        val mb = kb * 1024
        val gb = mb * 1024
        return if (size >= gb) {
            String.format("%.1f GB", size.toFloat() / gb)
        } else if (size >= mb) {
            val f = size.toFloat() / mb
            String.format(if (f > 100) "%.0f M" else "%.1f M", f)
        } else if (size >= kb) {
            val f = size.toFloat() / kb
            String.format(if (f > 100) "%.0f K" else "%.1f K", f)
        } else String.format("%d B", size)
    }//获取文件目录对象剩余空间//得到sdcard的目录作为一个文件对象

    /**
     * 获取机身内存占用情况，第一个为可用，第二个为总共
     * @return
     */
    val spaceSize: Array<String>
        get() {
            val sdcardFileDir =
                Environment.getExternalStorageDirectory() //得到sdcard的目录作为一个文件对象
            val usableSpace = sdcardFileDir.usableSpace //获取文件目录对象剩余空间
            val totalSpace = sdcardFileDir.totalSpace
            val usable =
                convertFileSize(usableSpace)
            val total =
                convertFileSize(totalSpace)
            return arrayOf(usable, total)
        }

    val cachePath: String
        get() = SharedUtil.read("VIDEO_CACHE_PATH", App.getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!.path)

    /**
     * 获取下载目录文件大小
     * @return
     */
    val cacheSize: String
        get() {
            try {
                val localPath = cachePath
                val path = isExistDir(localPath)
                val file = File(path)
                return convertFileSize(
                    getFileSizes(
                        file
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "0"
        }

    @Throws(Exception::class)
    private fun getFileSizes(f: File): Long {
        var size: Long = 0
        val flist = f.listFiles()
        for (i in flist.indices) {
            size = if (flist[i].isDirectory) {
                size + getFileSizes(flist[i])
            } else {
                size + getFileSize(flist[i])
            }
        }
        return size
    }

    @Throws(Exception::class)
    private fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            var fis: FileInputStream? = null
            fis = FileInputStream(file)
            size = fis.available().toLong()
        } else {
            file.createNewFile()
        }
        return size
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在，不存在就创建
     */
    @Throws(IOException::class)
    fun isExistDir(saveDir: String): String {
        val file = File(saveDir)
        Log.e("testlocalpath", "" + saveDir + file.isFile)
        // 下载位置
        val downloadFile = File(saveDir)
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile()
        }
        return downloadFile.absolutePath
    }
}
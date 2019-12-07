package com.cyuan.bimibimi.core.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.cyuan.bimibimi.core.App
import java.io.FileOutputStream
import java.util.*

object ImageUtils {

    fun saveBitmap(bitmap: Bitmap): String {
        val targetDir = App.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.path
        val savePath = "${targetDir}/${CalendarUtils.dateToStr(Date(), CalendarUtils.TimeFormat.yyyy_MM_dd__HH_mm_ss)}.png"
        val fos = FileOutputStream(savePath)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        return savePath
    }

    /**
     * 将指定图片路径插入到系统的相册当中。
     *
     * @return 插入到相册之后图片对应的Uri。
     */
    fun insertImageToSystem(
        context: Context,
        path: String
    ): Uri? {
        var uri: Uri? = null
        try {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, path)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + " = ?",
                arrayOf(path),
                null
            )
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                val id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                uri = Uri.parse("content://media/external/images/media/$id")
                return uri
            } else {
                uri = context.contentResolver
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
            cursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri
    }
}
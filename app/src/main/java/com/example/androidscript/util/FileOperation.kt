@file:Suppress("unused")

package com.example.androidscript.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import java.io.*
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.util.*

object FileOperation {

    lateinit var root: String

    fun readDir(pathName: String): Array<String>? {
        val dir = File(root + pathName)
        dir.mkdir()
        return if (dir.isDirectory) {
            dir.list()
        } else null
    }

    fun saveBitmapAsJPG(bm: Bitmap, fileName: String) {
        val file = createFileAndParent(root + fileName)
            val out = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
    }

    fun saveBitmapAsPNG(bm: Bitmap, fileName: String) {
        val file = createFileAndParent(root + fileName)
        val out = FileOutputStream(file)
        bm.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.flush()
        out.close()
    }

    fun writeLines(fileName: String, contents: Vector<String>) {
        val scriptFile = createFileAndParent(root + fileName)
        val bufferedWriter = BufferedWriter(FileWriter(scriptFile))
        for (content in contents) {
            bufferedWriter.write(content)
            bufferedWriter.newLine()
        }
        bufferedWriter.close()
    }

    fun writeWords(fileName: String, contents: Vector<Vector<String>>) {
        val scriptFile = createFileAndParent(root + fileName)
        val bufferedWriter = BufferedWriter(FileWriter(scriptFile))
        for (line in contents) {
            val buffer = StringBuilder()
            for (z in 0 until line!!.size - 1) {
                buffer.append(line[z])
                buffer.append(" ")
            }
            buffer.append(line[line.size - 1])
            bufferedWriter.write(buffer.toString())
            bufferedWriter.newLine()
        }
        bufferedWriter.close()
    }

    fun readFromFileLines(fileName: String): Vector<String> {
        val file = File(root + fileName)

        val content = Vector<String>()
        val myReader = Scanner(file)
        while (myReader.hasNextLine()) {
            content.add(myReader.nextLine())
        }
        myReader.close()
        return content
    }

    fun readFromFileWords(fileName: String): Vector<Vector<String>> {
        val file = File(root + fileName)
        assert(!file.exists())
        val content = Vector<Vector<String>>()
        val myReader = Scanner(file)
        while (myReader.hasNextLine()) {
            content.add(
                Vector(
                    listOf(
                        *myReader.nextLine().split(" ".toRegex()).toTypedArray()
                    )
                )
            )
        }
        myReader.close()
        return content
    }

    fun readWholeFile(fileName: String): String? {
        val file = File(root + fileName)
        if (file.exists()) {
            return null
        }
        val fileInputStream = FileInputStream(file)
        val data = ByteArray(file.length().toInt())
        fileInputStream.read(data)
        fileInputStream.close()
        return String(data, StandardCharsets.UTF_8)
    }

    fun readPicAsBitmap(fileName: String): Bitmap {
        return BitmapFactory.decodeFile(root + fileName)
    }

    fun saveImage(img: Image, fileName: String) {
        val buffer = img.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        FileOutputStream(createFileAndParent(root + fileName)).write(bytes)
    }

    fun browseAvailableFile(folder: String, targetType: String): Vector<String> {
        val ret = Vector<String>()
        for (s in readDir(folder)!!) {
            if (s.endsWith(targetType)) {
                ret.add(s)
            }
        }
        return ret
    }

    fun browseWithoutSuffix(folder: String, targetType: String): Vector<String> {
        val ret = Vector<String>()
        for (s in readDir(folder)!!) {
            if (s.endsWith(targetType)) {
                ret.add(s.substring(0, s.length - targetType.length))
            }
        }
        return ret
    }

    fun findFile(folder: String, target: String): Boolean {
        return File(root + folder + target).exists()
    }

    private fun createFileAndParent(fileName: String): File {
        MyLog.set("Writing File: $fileName")
        val file = File(fileName)
        if (file.exists() && file.delete()) {
            MyLog.set("Overwriting $fileName")
        } else if (file.parentFile?.mkdir() == true) {
            MyLog.set("Creating Parent Dir of $fileName")
        }
        return file
    }
}
package com.tranquilrock.androidscript.utils

import android.graphics.Bitmap
import android.util.Log
import android.view.WindowManager
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvException
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.ORB
import kotlin.math.abs
import kotlin.math.max

class ImageParser(
    private val projectionReader: ProjectionReader, private val windowManager: WindowManager
) {

    private val screenWidth: Int
        get() = windowManager.currentWindowMetrics.bounds.width()
    private val screenHeight: Int
        get() = windowManager.currentWindowMetrics.bounds.height()

    private suspend fun getScreenShotBitmap(): Bitmap? {
        val img = projectionReader.screenShot()
        img ?: return null

        Log.d(TAG, "IMAGE " + img.width + " " + img.height)

        val plane = img.planes[0]
        val buffer = plane.buffer
        buffer.rewind() // java.lang.RuntimeException: Buffer not large enough for pixels
        val pixelStride = plane.pixelStride //像素間距
        val rowStride = plane.rowStride //總間距
        val rowPadding = rowStride - pixelStride * screenWidth
        var bitmap = Bitmap.createBitmap(
            screenWidth + rowPadding / pixelStride, screenHeight, Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight)
        img.close()
        return bitmap
    }


    suspend fun compareWithScreen(Target: Bitmap, x1: Int, y1: Int, x2: Int, y2: Int): Int {

        getScreenShotBitmap()
        return compare(Target, x1, y1, x2, y2)

    }

    suspend fun compare(Target: Bitmap, x1: Int, y1: Int, x2: Int, y2: Int): Int {
        val screen = getScreenShotBitmap() ?: return 0
        return matchPictures(
            Bitmap.createBitmap(
                screen, x1, y1, x2 - x1, y2 - y1
            ), Target
        )
    }

    private fun bitmapToGrayScaleMat(bitmap: Bitmap): Mat {
        val tmp = Mat()
        Utils.bitmapToMat(bitmap, tmp)
        val ret = Mat()
        Imgproc.cvtColor(tmp, ret, Imgproc.COLOR_RGB2GRAY)
        return ret
    }

    private fun toMat(bitmap: Bitmap?): Mat {
        val ret = Mat()
        Utils.bitmapToMat(bitmap, ret)
        return ret
    }

    private fun featureExtraction(grayBitmap: Mat): Mat { //KeyPoint detection and extraction
        val orb = ORB.create(100000)
        val keyPoint = MatOfKeyPoint()
        orb.detect(grayBitmap, keyPoint)
        val ret = Mat()
        orb.compute(grayBitmap, keyPoint, ret)
        return ret
    }

    private fun featureMatch(screenDescriptor: Mat, targetDescriptor: Mat): MatOfDMatch? {
        return try {
            val ret = MatOfDMatch()
            //BFMatcher matcher = new BFMatcher(BFMatcher.BRUTEFORCE_HAMMING, false);
            //matcher.knnMatch(descInput,descLocal,matchPointsList,2);
            val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)
            matcher.match(screenDescriptor, targetDescriptor, ret)
            ret
        } catch (ex: CvException) {
            Log.i(TAG, "Matching failed $ex")
            null
        }
    }

    private fun matchPictures(screenshot: Bitmap, target: Bitmap): Int {
        val sourceMat = bitmapToGrayScaleMat(screenshot)
        val targetMat = bitmapToGrayScaleMat(target)
        val screenDescriptor = featureExtraction(sourceMat) // Size:(totalFeatures, 32)
        val targetDescriptor = featureExtraction(targetMat)
        val matchPoints =
            featureMatch(screenDescriptor, targetDescriptor)!!.toArray() // Length: max(AFeatures,B)
        var minDistance = 0f
        var matchCount = 0
        for (match in matchPoints) {
            if (minDistance > match.distance) {
                minDistance = match.distance
            }
        }
        minDistance = max(2 * minDistance, 30.0f)
        for (z in 0 until screenDescriptor.rows()) {
            if (matchPoints[z].distance <= minDistance) {
                matchCount++
            }
        }
        Log.i(TAG, "matchPictures:: Match $matchCount points")
        return matchCount
    }


    suspend fun findLocTODO(target: Bitmap?, resizeRatio: Double): Point? =
        findLocation(getScreenShotBitmap(), target, resizeRatio)

    private fun findLocation(screenshot: Bitmap?, target: Bitmap?, resizeRatio: Double): Point? {
        screenshot ?: return null
        val image = toMat(screenshot)
        val template = Mat()
        Imgproc.resize(
            toMat(target), template, Size(
                target!!.width * resizeRatio, target.height * resizeRatio
            )
        )
        val result = Mat()
        Imgproc.matchTemplate(image, template, result, Imgproc.TM_CCOEFF_NORMED)
        val mmr = Core.minMaxLoc(result)
        Log.i(TAG, "Confidence " + mmr.maxVal)

        return when {
            mmr.maxVal > 0.75 -> Point(
                mmr.maxLoc.x + template.width(), mmr.maxLoc.y + template.height()
            )

            else -> null
        }
    }

    fun findLocationAnyway(screenshot: Bitmap?, target: Bitmap, resizeRatio: Double): Point {
        val image = toMat(screenshot)
        val template = Mat()
        Imgproc.resize(
            toMat(target), template, Size(target.width * resizeRatio, target.height * resizeRatio)
        )
        val result = Mat()
        Imgproc.matchTemplate(image, template, result, Imgproc.TM_CCOEFF_NORMED)
        val mmr = Core.minMaxLoc(result)
        Log.i(TAG, "Confidence " + mmr.maxVal)
        return Point(mmr.maxLoc.x + template.width(), mmr.maxLoc.y + template.height())
    }

    suspend fun checkScreenColor(x: Int, y: Int, color: Int): Boolean =
        checkColor(getScreenShotBitmap(), x, y, color)

    fun checkColor(target: Bitmap?, x: Int, y: Int, color: Int): Boolean {
        target ?: return false
        val targetPixel = target.getPixel(x, y)
        var diff = 0
        for (mask in COLOR_MASKS.indices) {
            diff += abs((targetPixel and COLOR_MASKS[mask]) - (color and COLOR_MASKS[mask])) shr 8 * mask
        }
        Log.d(TAG, "CheckColor $diff")
        return diff < COLOR_THRESHOLD
    }

    companion object {
        private val TAG = ImageParser::class.java.simpleName
        private const val COLOR_THRESHOLD = 100 // TODO check
        private val COLOR_MASKS = intArrayOf(0x000000FF, 0x0000FF00, 0x00FF0000, -0x1000000)
    }
}
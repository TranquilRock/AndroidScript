package com.example.androidscript.util

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.ORB
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.max

object ImageHandler {
    private val LOG_TAG = ImageHandler::class.java.simpleName

    private fun grayScale(bitmap: Bitmap): Mat {
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
            Log.i(LOG_TAG, "Matching failed $ex")
            null
        }
    }

    fun matchPicture(screenshot: Bitmap?, target: Bitmap?): Int {
        screenshot ?: return 0
        target ?: return 0

        val sourceMat = grayScale(screenshot)
        val targetMat = grayScale(target)
        val screenDescriptor = featureExtraction(sourceMat) // Size:(totalFeatures, 32)
        val targetDescriptor = featureExtraction(targetMat)
        val matchPoints = featureMatch(screenDescriptor, targetDescriptor)!!
            .toArray() //Length: max(AFeatures,B)
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
        Log.i(LOG_TAG, "Match $matchCount points")
        return matchCount
    }

    fun findLocation(screenshot: Bitmap?, target: Bitmap?, resizeRatio: Double): Point? {
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
        Log.i(LOG_TAG, "Confidence " + mmr.maxVal)

        return when {
            mmr.maxVal > 0.75 -> Point(
                mmr.maxLoc.x + template.width(),
                mmr.maxLoc.y + template.height()
            )
            else -> {
                null
            }
        }
    }

    @Suppress("unused")
    fun findLocationAnyway(screenshot: Bitmap?, target: Bitmap, resizeRatio: Double): Point {
        val image = toMat(screenshot)
        val template = Mat()
        Imgproc.resize(
            toMat(target),
            template,
            Size(target.width * resizeRatio, target.height * resizeRatio)
        )
        val result = Mat()
        Imgproc.matchTemplate(image, template, result, Imgproc.TM_CCOEFF_NORMED)
        val mmr = Core.minMaxLoc(result)
        Log.i(LOG_TAG, "Confidence " + mmr.maxVal)
        return Point(mmr.maxLoc.x + template.width(), mmr.maxLoc.y + template.height())
    }

    private const val colorThreshold = 100
    private val colorMasks = intArrayOf(0x000000FF, 0x0000FF00, 0x00FF0000, -0x1000000)
    fun checkColor(target: Bitmap?, x: Int, y: Int, color: Int): Boolean {
        if (target == null) {
            return false
        }
        val targetPixel = target.getPixel(x, y)
        var diff = 0
        for (mask in colorMasks.indices) {
            diff += abs((targetPixel and colorMasks[mask]) - (color and colorMasks[mask])) shr 8 * mask
        }
        Log.d("kk", diff.toString())
        return diff < colorThreshold
    }

}
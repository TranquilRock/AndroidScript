package com.example.androidscript.util;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class ImageHandler {
    private static Mat grayScale(Bitmap bitmap) {
        Mat tmp = new Mat();
        Utils.bitmapToMat(bitmap, tmp);
        Mat ret = new Mat();
        Imgproc.cvtColor(tmp, ret, Imgproc.COLOR_RGB2GRAY);
        return ret;
    }

    private static Mat featureExtraction(Mat grayBitmap) {//KeyPoint detection and extraction
        ORB orb = ORB.create(100000);
        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
        orb.detect(grayBitmap, keyPoint);
        Mat ret = new Mat();
        orb.compute(grayBitmap, keyPoint, ret);
        return ret;
    }

    private static MatOfDMatch featureMatch(Mat screenDescriptor, Mat targetDescriptor) {
        try {
            MatOfDMatch ret = new MatOfDMatch();
            //BFMatcher matcher = new BFMatcher(BFMatcher.BRUTEFORCE_HAMMING, false);
            //matcher.knnMatch(descInput,descLocal,matchPointsList,2);
            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            matcher.match(screenDescriptor, targetDescriptor, ret);
            return ret;
        } catch (CvException ex) {
            DebugMessage.set("Matching failed " + ex.toString());
            return null;
        }
    }

    public static int matchPicture(Bitmap screenshot, Bitmap target) {
        if (screenshot == null || target == null) {
            return 0;
        }
        Mat sourceMat = grayScale(screenshot);
        Mat targetMat = grayScale(target);
        Mat screenDescriptor = featureExtraction(sourceMat); // Size:(totalFeatures, 32)
        Mat targetDescriptor = featureExtraction(targetMat);

        DMatch[] matchPoints = featureMatch(screenDescriptor, targetDescriptor).toArray();//Length: max(AFeatures,B)

        float minDistance = 0;
        int matchCount = 0;

        for (DMatch match : matchPoints) {
            if (minDistance > match.distance) {
                minDistance = match.distance;
            }
        }

        minDistance = max(2 * minDistance, 30.0f);

        for (int z = 0; z < screenDescriptor.rows(); z++) {
            if (matchPoints[z].distance <= minDistance) {
                matchCount++;
            }
        }

        DebugMessage.set("Match " + matchCount + " points");
        return matchCount;
    }

    public static boolean TestPictureContain(Bitmap screenshot, Bitmap target) {
        if (screenshot == null || target == null) {
            return false;
        }
        Mat sourceMat = grayScale(screenshot);
        Mat targetMat = grayScale(target);
        Mat screenDescriptor = featureExtraction(sourceMat); // Size:(totalFeatures, 32)
        Mat targetDescriptor = featureExtraction(targetMat);

        DMatch[] matchPoints = featureMatch(screenDescriptor, targetDescriptor).toArray();//Length: max(AFeatures,B)

        float minDistance = 0;
        int matchCount = 0;

        for (DMatch match : matchPoints) {
            if (minDistance > match.distance) {
                minDistance = match.distance;
            }
        }

        minDistance = max(2 * minDistance, 30.0f);

        for (int z = 0; z < screenDescriptor.rows(); z++) {
            if (matchPoints[z].distance <= minDistance) {
                matchCount++;
            }
        }

        DebugMessage.set("Match " + matchCount + " points " + screenDescriptor.width() + " " + screenDescriptor.height() + " " + targetDescriptor.width() + " " + targetDescriptor.height());
        return (3040.0 * 1440.0 / ScreenShot.getHeight() / ScreenShot.getWidth() * 3 * matchCount) >= min(screenDescriptor.height(), targetDescriptor.height());
    }
}

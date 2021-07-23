package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.common.Feature;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

public class ImageHandler {
    private static Mat grayScale(Bitmap bitmap) {
        Mat tmp = new Mat();
        Utils.bitmapToMat(bitmap, tmp);
        Mat ret = new Mat();
        Imgproc.cvtColor(tmp, ret, Imgproc.COLOR_RGB2GRAY);
        return ret;
    }

    private static Mat featureExtraction(Bitmap bitmap) {//KeyPoint detection and extraction
        ORB orb = ORB.create(1000);
        Mat grayBitmap = grayScale(bitmap);
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

    public static boolean matchPicture(Bitmap screenshot, Bitmap target) {
        if (screenshot == null || target == null) {
            DebugMessage.set("Null in matchPicture");
            return false;
        }

        Mat screenDescriptor = featureExtraction(screenshot); // Size:(totalFeatures, 32)
        Mat targetDescriptor = featureExtraction(target);

        DMatch[] matchPoints = featureMatch(screenDescriptor, targetDescriptor).toArray();//Length: totalFeatures
        DebugMessage.set("Num of features: " + matchPoints.length + "\n");

        float minDistance = 0;
        int matchCount = 0;

        for (DMatch match : matchPoints) {
            if (minDistance > match.distance) {
                minDistance = match.distance;
            }
        }

        minDistance = Math.max(2 * minDistance, 30.0f);

        for (int z = 0; z < screenDescriptor.rows(); z++) {
            if (matchPoints[z].distance <= minDistance) {
                matchCount++;
            }
        }

        DebugMessage.set("Match " + matchCount + " points\n");
        return 100 * matchCount > 80 * matchPoints.length;
    }
}

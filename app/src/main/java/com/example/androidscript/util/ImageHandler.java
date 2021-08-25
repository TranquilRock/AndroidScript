package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

import static java.lang.Math.abs;
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

    private static Mat toMat(Bitmap bitmap) {
        Mat ret = new Mat();
        Utils.bitmapToMat(bitmap, ret);
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

    public static Point findLocation(Bitmap screenshot, Bitmap target, double resizeRatio) {
        if(screenshot == null){
            return null;
        }
        Mat image = toMat(screenshot);
        Mat template = new Mat();
        Imgproc.resize(toMat(target), template, new Size(target.getWidth() * resizeRatio, target.getHeight() * resizeRatio));
        Mat result = new Mat();
        Imgproc.matchTemplate(image, template, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        DebugMessage.set("Confidence " + mmr.maxVal);
        if (mmr.maxVal > 0.75) {
            return new Point(mmr.maxLoc.x + template.width(), mmr.maxLoc.y + template.height());
        }
        return null;
    }

    public static Point findLocationAnyway(Bitmap screenshot, Bitmap target, double resizeRatio) {
        Mat image = toMat(screenshot);
        Mat template = new Mat();
        Imgproc.resize(toMat(target), template, new Size(target.getWidth() * resizeRatio, target.getHeight() * resizeRatio));
        Mat result = new Mat();
        Imgproc.matchTemplate(image, template, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        DebugMessage.set("Confidence " + mmr.maxVal);
        return new Point(mmr.maxLoc.x + template.width(), mmr.maxLoc.y + template.height());
    }


    public static final int ColorThreshold = 100;
    public static final int[] ColorMasks = {0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000};

    public static boolean checkColor(Bitmap target, int x, int y, int color) {
        if(target == null){
            return false;
        }
        int targetPixel = target.getPixel(x, y);
        int diff = 0;
        for (int mask = 0; mask < ColorMasks.length; mask++) {
            diff += abs((targetPixel & ColorMasks[mask]) - (color & ColorMasks[mask])) >> (8 * mask);
        }
        Log.d("kk", Integer.toString(diff));
        return diff < ColorThreshold;
    }
}

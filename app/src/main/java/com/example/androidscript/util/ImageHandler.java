package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.media.Image;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

import java.util.Vector;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

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

    public static int tmp = 0;

    public static int matchPicture(Bitmap screenshot, Bitmap target) {
        if (screenshot == null || target == null) {
            return 0;
        }
        FileOperation.saveBitmapAsJPG(screenshot, "Test" + tmp + ".jpg");
        tmp++;
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

//
//    public static boolean TestPictureContain(Bitmap screenshot, Bitmap target,Double ratio) {
//        if (screenshot == null || target == null) {
//            return false;
//        }
//        Mat sourceMat = grayScale(screenshot);
//        Mat targetMat = new Mat();
//        Imgproc.resize(grayScale(target),targetMat,new Size(ratio * target.getWidth(),ratio * target.getHeight()));
//        Mat result = new Mat();
//        Imgproc.matchTemplate(sourceMat, targetMat, result, Imgproc.TM_CCOEFF_NORMED);
//        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
//    }

    public static Point findLocation(Bitmap screenshot, Bitmap target, double resizeRatio) {
        Mat image = toMat(screenshot);
        Mat template = new Mat();
        Imgproc.resize(toMat(target), template, new Size(target.getWidth() * resizeRatio, target.getHeight() * resizeRatio));
        Mat result = new Mat();
        Imgproc.matchTemplate(image, template, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        DebugMessage.set("Confidence " + mmr.maxVal);
        if (mmr.maxVal > 0.6) {
            return new Point(mmr.maxLoc.x + template.width(), mmr.maxLoc.y + template.height());
        }
        return null;
    }
}

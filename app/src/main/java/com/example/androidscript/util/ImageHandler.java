package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.OpenCVLoader.*;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

public class ImageHandler {
    private static Mat getGrayMat(Bitmap bm) {
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bm, srcMat);
        Mat grayMat = new Mat();
        Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_RGB2GRAY);
        return grayMat;
    }

    public static boolean isPictureMatchLuckyMoney(Bitmap bmInput) throws CvException {
        if(bmInput == null){
            return false;
        }
//        String TargetPath = "";
//        Bitmap bmLocal = BitmapFactory.decodeFile(TargetPath);

        Mat inputGrayMat = getGrayMat(bmInput);
        Mat localGrayMat = getGrayMat(bmInput);


        //特征点提取
        ORB orb = ORB.create(1000);                           //精度越小越准确
        MatOfKeyPoint kptsInput = new MatOfKeyPoint();
        MatOfKeyPoint kptsLocal = new MatOfKeyPoint();
        orb.detect(inputGrayMat, kptsInput);
        orb.detect(localGrayMat, kptsLocal);

        //特征点描述,采用ORB默认的描述算法
        Mat descInput = new Mat();
        Mat descLocal = new Mat();
        orb.compute(inputGrayMat, kptsInput, descInput);
        orb.compute(localGrayMat, kptsLocal, descLocal);

        //BFMatcher matcher = new BFMatcher(BFMatcher.BRUTEFORCE_HAMMING, false);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        MatOfDMatch matchPoints = new MatOfDMatch();
        //Log.e("matchoutput", "--start---");
        //matcher.knnMatch(descInput,descLocal,matchPointsList,2);
        try {
            matcher.match(descInput, descLocal, matchPoints);
        } catch (CvException ex) {
            DebugMessage.set("matchoutput" + ex.toString());
            return false;
        }

        float min_dist = 0;

        DMatch[] arrays = matchPoints.toArray();

        for (int i = 0; i < descInput.rows(); ++i) {
            float dist = arrays[i].distance;
            if (dist < min_dist) min_dist = dist;
        }

        int goodMatchPointNum = 0;

        //筛选特征点
        float compareNum = Math.max(min_dist * 2, 30.0f);

        for (int j = 0; j < descInput.rows(); j++) {
            if (arrays[j].distance <= compareNum) {
                goodMatchPointNum++;
            }
        }
        DebugMessage.set(goodMatchPointNum + "\n");
        return goodMatchPointNum > 10;
    }
}

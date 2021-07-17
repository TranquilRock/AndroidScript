package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class SaveImg {
    public static void bitmap(Bitmap bm,String FileName) {
        if(bm == null) return;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/AndroidScript");
        myDir.mkdirs();
        String fileName = FileName + ".jpg";
        File file = new File(myDir, fileName);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

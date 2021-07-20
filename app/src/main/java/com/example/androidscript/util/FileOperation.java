package com.example.androidscript.util;

import android.app.Activity;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

public class FileOperation extends Activity {
    static String root;
    {
        root = getFilesDir().getAbsolutePath();
    }
    public static void saveBitmapAsJPG(Bitmap bm,String FileName) {
        if(bm == null) return;
        System.out.println("Saving File: " + FileName);
        File file = new File(FileName);
        file.getParentFile().mkdir();
        if (file.exists()){
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void WriteToFile(String FileName){

    }
}

package com.example.androidscript.util;

import android.app.Activity;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

public class SaveImg extends Activity {
    public static void bitmap(Bitmap bm,String FileName) {
        if(bm == null) return;
        String fileName = FileName;
        System.out.println(fileName);

        File file = new File(fileName);
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

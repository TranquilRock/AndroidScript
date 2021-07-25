package com.example.androidscript.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.Vector;

public class FileOperation extends Activity {
    private static FileOperation instance = null;
    private String root;

    public static void setUpFileRoot(String root) {
        if (FileOperation.instance == null) {
            new FileOperation(root);
        }
    }

    public static void saveImage(Image img, String FileName) {
        instance.SaveImage(img, FileName);
    }

    public static Bitmap readPicAsBitmap(String FileName) {
        return instance.ReadPicAsBitmap(FileName);
    }

    public static String readWholeFile(String FileName) {
        return instance.ReadWholeFile(FileName);
    }

    public static Vector<String> readFromFileLines(String FileName) {
        return instance.ReadFromFileLines(FileName);
    }

    public static void saveBitmapAsJPG(Bitmap bm, String FileName) {
        instance.SaveBitmapAsJPG(bm, FileName);
    }

    public static void writeToFile(String FileName, Vector<String> contents) {
        instance.WriteToFile(FileName, contents);
    }

    private FileOperation(String root) {
        this.root = root;
        FileOperation.instance = this;
    }

    private void SaveBitmapAsJPG(Bitmap bm, String FileName) {
        FileName = root + FileName;
        if (bm == null) {
            return;
        }
        File file = createFileAndParent(FileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            DebugMessage.printStackTrace(e);
        }
    }

    private void WriteToFile(String FileName, Vector<String> contents) {
        FileName = root + FileName;
        try {
            File scriptFile = createFileAndParent(FileName);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(scriptFile));
            for (String content : contents) {
                bufferedWriter.write(content);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            DebugMessage.printStackTrace(e);
        }
    }

    private Vector<String> ReadFromFileLines(String FileName) {
        FileName = root + FileName;
        File file = new File(FileName);
        if (file.exists()) {
            try {
                Vector<String> content = new Vector<>();
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    content.add(myReader.nextLine());
                }
                myReader.close();
                return content;
            } catch (IOException e) {
                DebugMessage.printStackTrace(e);
            }
        } else {
            DebugMessage.set("File " + FileName + " not found");
        }
        return null;
    }

    private String ReadWholeFile(String FileName) {
        FileName = root + FileName;
        File file = new File(FileName);
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fileInputStream.read(data);
                fileInputStream.close();
                return new String(data, "UTF-8");

            } catch (IOException e) {
                DebugMessage.printStackTrace(e);
            }
        } else {
            DebugMessage.set("File " + FileName + " not found");
        }
        return null;
    }

    private Bitmap ReadPicAsBitmap(String FileName) {
        FileName = root + FileName;
        Bitmap ret = BitmapFactory.decodeFile(FileName);
        if (ret == null) {
            DebugMessage.set("File " + FileName + " not found");
        }
        return ret;
    }

    private File createFileAndParent(String FileName) {
        DebugMessage.set("Writing File: " + FileName);
        File file = new File(FileName);
        if (file.exists() && file.delete()) {
            DebugMessage.set("Overwriting " + FileName);
        } else if (file.getParentFile() != null && (file.getParentFile()).mkdir()) {
            DebugMessage.set("Creating Parent Dir of " + FileName);
        }
        return file;
    }

    private void SaveImage(Image img, String FileName) {
        FileName = root + FileName;
        ByteBuffer buffer = img.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(createFileAndParent(FileName));
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

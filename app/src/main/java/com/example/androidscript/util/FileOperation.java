package com.example.androidscript.util;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;

public class FileOperation extends Activity {
    public static FileOperation instance = null;
    private String root;

    public static void setUpFileRoot(String root) {
        if (FileOperation.instance == null) {
            new FileOperation(root);
        }
    }

    public FileOperation(String root) {
        this.root = root;
        FileOperation.instance = this;
    }

    public void saveBitmapAsJPG(Bitmap bm, String FileName) {
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

    public void WriteToFile(String FileName, Vector<String> contents) {
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

    protected Vector<String> ReadFromFile(String FileName) {
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
        }
        return null;
    }

    public File createFileAndParent(String FileName) {
        FileName = root + FileName;
        DebugMessage.set("Writing File: " + FileName);
        File file = new File(FileName);
        if (file.exists() && file.delete()) {
            DebugMessage.set("Overwriting " + FileName);
        } else if (file.getParentFile() != null && (file.getParentFile()).mkdir()) {
            DebugMessage.set("Creating Parent Dir of " + FileName);
        }
        return file;
    }

}

package com.example.androidscript.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public final class FileOperation extends Activity {
    private static String root = null;

    public static void fileRootInit(String _root) {
        if (FileOperation.root == null) {
            FileOperation.root = _root;
            DebugMessage.set(_root);
        }
    }

    public static String[] readDir(String PathName) {
        PathName = root + PathName;
        File dir = new File(PathName);
        if (dir.mkdir()) {
            DebugMessage.set("MakeDir");
        }
        if (dir.isDirectory()) {
            return dir.list();
        }
        return null;
    }

    public static void saveBitmapAsJPG(Bitmap bm, String FileName) {
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

    public static void saveBitmapAsPNG(Bitmap bm, String FileName) {
        FileName = root + FileName;
        if (bm == null) {
            return;
        }
        File file = createFileAndParent(FileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            DebugMessage.printStackTrace(e);
        }
    }

    public static void writeLines(String FileName, Vector<String> contents) {
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

    public static void writeWords(String FileName, Vector<Vector<String>> contents) {
        FileName = root + FileName;
        try {
            File scriptFile = createFileAndParent(FileName);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(scriptFile));
            for (Vector<String> line : contents) {
                StringBuilder buffer = new StringBuilder();
                for (int z = 0; z < line.size() - 1; z++) {
                    buffer.append(line.get(z));
                    buffer.append(" ");
                }
                buffer.append(line.get(line.size() - 1));
                bufferedWriter.write(buffer.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            DebugMessage.printStackTrace(e);
        }
    }

    public static Vector<String> readFromFileLines(String FileName) {
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

    public static Vector<Vector<String>> readFromFileWords(String FileName) {
        FileName = root + FileName;
        File file = new File(FileName);
        if (file.exists()) {
            try {
                Vector<Vector<String>> content = new Vector<>();
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    content.add(new Vector<>(Arrays.asList(myReader.nextLine().split(" "))));
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

    public static String readWholeFile(String FileName) {
        FileName = root + FileName;
        File file = new File(FileName);
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fileInputStream.read(data);
                fileInputStream.close();
                return new String(data, StandardCharsets.UTF_8);

            } catch (IOException e) {
                DebugMessage.printStackTrace(e);
            }
        } else {
            DebugMessage.set("File " + FileName + " not found");
        }
        return null;
    }

    public static Bitmap readPicAsBitmap(String FileName) {
        FileName = root + FileName;
        Bitmap ret = BitmapFactory.decodeFile(FileName);
        if (ret == null) {
            DebugMessage.set("File " + FileName + " not found");
        }
        return ret;
    }

    public static void saveImage(Image img, String FileName) {
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

    public static Vector<String> browseAvailableFile(String Folder, String targetType) {
        Vector<String> ret = new Vector<>();
        for (String s : FileOperation.readDir(Folder)) {
            if (s.contains(targetType)) {
                ret.add(s);
            }
        }
        return ret;
    }

    public static boolean findFile(String Folder, String target) {
        return (new File(FileOperation.root + Folder + target)).exists();
    }
    private static File createFileAndParent(String FileName) {
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

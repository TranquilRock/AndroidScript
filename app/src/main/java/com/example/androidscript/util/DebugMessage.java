package com.example.androidscript.util;

public final class DebugMessage {//Turn these off when publish
    public static void set(String output){
        System.out.println(output);
    }
    public static void printStackTrace(Exception e){
        e.printStackTrace();
    }
}

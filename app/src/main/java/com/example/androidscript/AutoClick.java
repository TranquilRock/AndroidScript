package com.example.androidscript;

public class AutoClick {

    public static void autoClickPos(final double x1, final double y1, final double x2, final double y2) {
        new Thread(() -> {
            String[] order = {"input", "swipe", "" + x1, "" + y1, "" + x2, "" + y2,};
            try {
                new ProcessBuilder(order).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

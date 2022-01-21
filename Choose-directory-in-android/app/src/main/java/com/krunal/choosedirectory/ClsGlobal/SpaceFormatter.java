package com.krunal.choosedirectory.ClsGlobal;
import java.util.Locale;
public class SpaceFormatter
{
    public String format(long originalSize)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        String label = "B";
        double size = originalSize;

        if (size > 1024) {
            size /= 1024;
            label = "KB";
        }

        if (size > 1024) {
            size /= 1024;
            label = "MB";
        }

        if (size > 1024) {
            size /= 1024;
            label = "GB";
        }

        if (size % 1 == 0) {
            return String.format(Locale.getDefault(), "%d %s", (long) size, label);
        } else {
            return String.format(Locale.getDefault(), "%.1f %s", size, label);
        }
    }
}

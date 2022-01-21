package com.krunal.choosedirectory.Provider;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
public class LegacyCompatFileProvider extends FileProvider
{
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return (
                   new LegacyCompatCursorWrapper(
                       super.query(uri, projection, selection, selectionArgs, sortOrder)));
    }
}

package com.krunal.choosedirectory.models;
import android.util.Log;
import android.widget.TextView;
import com.krunal.choosedirectory.Fragments.FolderFragment;
import com.krunal.choosedirectory.R;
public class ToolBar
{
    private final TextView folderName;
    public ToolBar(TextView textview)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        this.folderName = textview;
    }
    public void update(FolderFragment fragment)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        Log.e("Check", "fragment: " + fragment.folderName());
        updateTitle(fragment.folderName());
    }
    public void update(String title)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        updateTitle(title);
    }
    private void updateTitle(String text)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        Log.e("Check", "updateTitle text : " + text);

        try {
            folderName.setText(text);
        } catch (Exception e) {
            //            CrashUtils.report(e);
            Log.e("Check", "Exception text : " + text);
            folderName.setText(R.string.file_explorer);
        }
    }
}

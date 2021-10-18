package com.google.android.mobly.snippet.example2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import java.util.concurrent.ExecutionException;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ActivityScenario;
import com.google.android.mobly.snippet.Snippet;
import com.google.android.mobly.snippet.rpc.Rpc;
import org.junit.Rule;
import com.google.gson.Gson;
import androidx.test.core.app.ActivityScenario.ActivityAction;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class EspressoSnippet implements Snippet {
    private static final String TAG = "EspressoSnippet";
    private static DriveServiceHelper service;
    private Context mContext;

    public EspressoSnippet() {
        mContext = InstrumentationRegistry.getInstrumentation().getContext();
    }

    public DriveServiceHelper getDriveService() {
        return MainActivity.getInstance().getDriveService();
    }

    @Rpc(description = "Returns the given integer with the prefix \"foo\"")
    public String getFoo(Integer input) {
        return "foo " + input;
    }

    @Rpc(description = "Opens the main activity of the app")
    public void startMainActivity() {
        System.out.println("YAO startMainActivity");
        Intent intent = new Intent();
        intent.setClass(mContext.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    @Rpc(description = "add file")
    public void addFile() throws InterruptedException, ExecutionException {
        System.out.println("YAO add file");
        DriveServiceHelper service = getDriveService();

        if (service != null) {
            service.createTextFileX("textfilename.txt", "some text", null).get();
        }
    }

    @Rpc(description = "download file")
    public void downloadFile() {
        DriveServiceHelper service = getDriveService();

        if (service != null) {
            service.downloadFileX(new java.io.File("/data/xxx", "filename.txt"), "google_drive_file_id_here");
        }
    }

    @Rpc(description = "upload file")
    public void uploadFile() {
        DriveServiceHelper service = getDriveService();

        if (service != null) {
            service.uploadFileX(new java.io.File("/data/xxx", "dummy.txt"), "text/plain", null);
        }
    }

    @Rpc(description = "close")
    public void close() {
    }

    @Override
    public void shutdown() {
        System.out.println("YAO shutdown");
    }
}

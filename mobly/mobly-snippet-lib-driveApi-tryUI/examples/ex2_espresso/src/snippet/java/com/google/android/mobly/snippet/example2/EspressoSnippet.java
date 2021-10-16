package com.google.android.mobly.snippet.example2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class EspressoSnippet implements Snippet {
    private static final String TAG = "EspressoSnippet";
    private static DriveServiceHelper service;
    private ActivityScenario<MainActivity> mActivityScenario;
    private Context mContext;

    @Rpc(description = "Returns the given integer with the prefix \"foo\"")
    public String getFoo(Integer input) {
        return "foo " + input;
    }

    @Rpc(description = "Opens the main activity of the app")
    public void startMainActivity() {
        System.out.println("YAO startMainActivity");
        mContext = InstrumentationRegistry.getInstrumentation().getContext();

        if (mActivityScenario == null) {
            Intent intent = new Intent();
            intent.setClass(mContext.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mActivityScenario = ActivityScenario.launch(intent);

            //mActivityScenario = ActivityScenario.launch(MainActivity.class);


            mActivityScenario.onActivity(
            new ActivityAction<MainActivity>() {
                @Override
                public void perform(MainActivity activity) {
                    service = activity.getDriveService();
                    System.out.println("YAO service:" + service);
                }
            });
        }
    }

    @Rpc(description = "add file")
    public void addFile() {
        System.out.println("YAO add file");
        mActivityScenario.onActivity(
        new ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                service = activity.getDriveService();
            }
        });

        if (service != null) {
            service.createTextFileX("textfilename.txt", "some text", null);
        }
    }

    @Rpc(description = "download file")
    public void downloadFile() {
        System.out.println("YAO download");
        mActivityScenario.onActivity(
        new ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                service = activity.getDriveService();
            }
        });

        if (service != null) {
            service.downloadFileX(new java.io.File("/data/xxx", "filename.txt"), "google_drive_file_id_here");
        }
    }

    @Rpc(description = "upload file")
    public void uploadFile() {
        System.out.println("YAO upload");

        mActivityScenario.onActivity(
        new ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                service = activity.getDriveService();
            }
        });

        if (service != null) {
            service.uploadFileX(new java.io.File("/data/xxx", "dummy.txt"), "text/plain", null);
        }
    }

    @Rpc(description = "close")
    public void close() {
        System.out.println("YAO close");
        mActivityScenario.close();
    }

    @Override
    public void shutdown() {
        System.out.println("YAO shutdown");
        mActivityScenario.close();
    }
}

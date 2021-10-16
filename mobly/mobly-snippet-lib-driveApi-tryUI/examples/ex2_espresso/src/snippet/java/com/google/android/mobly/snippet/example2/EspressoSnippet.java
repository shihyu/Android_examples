/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.mobly.snippet.example2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ActivityScenario;
import com.google.android.mobly.snippet.Snippet;
import com.google.android.mobly.snippet.rpc.Rpc;
import org.junit.Rule;
import com.google.gson.Gson;
import androidx.test.core.app.ActivityScenario.ActivityAction;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class EspressoSnippet implements Snippet {
    private static final String TAG = "EspressoSnippet";
    private static DriveServiceHelper service;
    private ActivityScenario<MainActivity> mActivityScenario;
    private MainActivity mainActivity;

    //@Rule
    //public ActivityTestRule<MainActivity> mActivityRule =
    //    new ActivityTestRule<>(MainActivity.class);

    @Rpc(description = "Returns the given integer with the prefix \"foo\"")
    public String getFoo(Integer input) {
        return "foo " + input;
    }

    @Rpc(description = "Opens the main activity of the app")
    public void startMainActivity() {
        System.out.println("YAO startMainActivity");

        if (mActivityScenario == null) {
            mActivityScenario = ActivityScenario.launch(MainActivity.class);
            mainActivity = MainActivity.getInstance();

            mActivityScenario.onActivity(
            new ActivityAction<MainActivity>() {
                @Override
                public void perform(MainActivity activity) {
                    service = activity.getDriveService();
                    System.out.println("YAO Instance:" + mainActivity);
                }
            });


            if (mainActivity != null) {
                System.out.println("YAO Instance:" + mainActivity);
                System.out.println("YAO google drive:" + mainActivity.getDriveService());
            }
        }

        //mActivityRule.launchActivity(null /* startIntent */);
        //service = mActivityRule.getActivity().getDriveService();
        // ActivityScenario.launch(MainActivity.class);
        System.out.println("YAO service:" + service);
    }

    @Rpc(description = "add file")
    public void addFile() {
        System.out.println("YAO add file");
        mActivityScenario.onActivity(
        new ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                service = activity.getDriveService();
                System.out.println("YAO Instance:" + mainActivity);
            }
        });

        if (service != null) {
            service.createTextFile("textfilename.txt", "some text", null)
            .addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                @Override
                public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });
        }

        //if (MainActivity.getInstance() != null && MainActivity.getInstance().getDriveService() != null) {
        //    service = MainActivity.getInstance().getDriveService();
        //    service.createTextFile("textfilename.txt", "some text", null)
        //    .addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
        //        @Override
        //        public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
        //            Gson gson = new Gson();
        //            Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
        //        }
        //    })
        //    .addOnFailureListener(new OnFailureListener() {
        //        @Override
        //        public void onFailure(@NonNull Exception e) {
        //            Log.d(TAG, "onFailure: " + e.getMessage());
        //        }
        //    });
        //}
    }

    @Rpc(description = "download file")
    public void downloadFile() {
        System.out.println("YAO download");
        mActivityScenario.onActivity(
        new ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                service = activity.getDriveService();
                System.out.println("YAO Instance:" + mainActivity);
            }
        });

        if (service != null) {
            service.downloadFileX(new java.io.File("/data/xxx", "filename.txt"), "google_drive_file_id_here");
        }

        //if (MainActivity.getInstance() != null && MainActivity.getInstance().getDriveService() != null) {
        //    service = MainActivity.getInstance().getDriveService();
        //    service.downloadFileX(new java.io.File("/data/xxx", "filename.txt"), "google_drive_file_id_here");
        //}
    }

    @Rpc(description = "upload file")
    public void uploadFile() {
        System.out.println("YAO upload");

        mActivityScenario.onActivity(
        new ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                service = activity.getDriveService();
                System.out.println("YAO Instance:" + mainActivity);
            }
        });

        if (service != null) {
            service.uploadFileX(new java.io.File("/data/xxx", "dummy.txt"), "text/plain", null);
        }

        //service = mActivityRule.getActivity().getDriveService();
        //if (MainActivity.getInstance() != null && MainActivity.getInstance().getDriveService() != null) {
        //    service = MainActivity.getInstance().getDriveService();
        //    service.uploadFileX(new java.io.File("/data/xxx", "dummy.txt"), "text/plain", null);
        //}
    }

    @Rpc(description = "close")
    public void close() {
        System.out.println("YAO close");
        mActivityScenario.close();
    }

    @Override
    public void shutdown() {
        //mActivityRule.getActivity().finish();
        System.out.println("YAO shutdown");
        //ActivityScenario.close();
        mActivityScenario.close();
    }
}

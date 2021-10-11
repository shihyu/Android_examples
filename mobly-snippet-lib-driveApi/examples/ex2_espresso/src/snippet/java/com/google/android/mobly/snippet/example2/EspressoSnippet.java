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
import com.google.android.mobly.snippet.Snippet;
import com.google.android.mobly.snippet.rpc.Rpc;
import org.junit.Rule;
import com.google.gson.Gson;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class EspressoSnippet implements Snippet {
    private static final String TAG = "EspressoSnippet";
    private static DriveServiceHelper service;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
        new ActivityTestRule<>(MainActivity.class);

    @Rpc(description = "Returns the given integer with the prefix \"foo\"")
    public String getFoo(Integer input) {
        return "foo " + input;
    }

    @Rpc(description = "Opens the main activity of the app")
    public void startMainActivity() {
        System.out.println("YAO YAO");
        System.out.println("YAO hhh " + MainActivity.getHello());
        mActivityRule.launchActivity(null /* startIntent */);
        service = mActivityRule.getActivity().getDriveService();
        System.out.println("YAO service:" + service);
    }

    @Rpc(description = "add file")
    public void addFile() {
        System.out.println("YAO add file");
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


    @Rpc(description = "Pushes the main app button, and checks the label if this is the first time.")
    public void pushMainButton(boolean checkFirstRun) {
        System.out.println("YAO 2222222");

        if (checkFirstRun) {
            onView(withId(R.id.main_text_view)).check(matches(withText("Hello World!")));
        }

        onView(withId(R.id.main_button)).perform(ViewActions.click());

        if (checkFirstRun) {
            onView(withId(R.id.main_text_view)).check(matches(withText("Button pressed 1 times.")));
        }
    }

    @Rpc(description = "download file")
    public void downloadFile() {
        System.out.println("YAO download");
        service = mActivityRule.getActivity().getDriveService();
        if (service != null) {
            // service.downloadFileX(new java.io.File(getApplicationContext().getFilesDir(), "filename.txt"), "google_drive_file_id_here");
            service.downloadFileX(new java.io.File("/data/xxx", "filename.txt"), "google_drive_file_id_here");
        }
    }

    @Override
    public void shutdown() {
        mActivityRule.getActivity().finish();
        System.out.println("YAO shutdown");
    }
}

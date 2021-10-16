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
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.filters.LargeTest;
import com.google.android.mobly.snippet.Snippet;
import com.google.android.mobly.snippet.rpc.Rpc;
import org.junit.Rule;
import com.google.gson.Gson;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import org.junit.runner.RunWith;
import android.app.Activity;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class EspressoSnippet implements Snippet {
    private ActivityScenario<MainActivity> mActivityScenario;
    private MainActivity mainActivity;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rpc(description = "foo")
    public void foo() {
        if (mActivityScenario == null) {
            mActivityScenario = ActivityScenario.launch(MainActivity.class);
            mainActivity = MainActivity.getInstances();
            if (mainActivity != null) {
                System.out.println("YAO Instance:" + mainActivity);
                System.out.println("YAO google drive:" + mainActivity.getDriveService());
            }
        }

        //mActivityScenario.getResult().getResultCode();
        //Activity testActivity = tryAcquireScenarioActivity(mActivityScenario);
        //testActivity.getDriveService();
        System.out.println("YAOO Hel11o");
        mActivityScenario.onActivity(activity -> {
            System.out.println("YAOO Hello");
            //getDriveService();
        });


        //System.out.println("YAO+:" + activityRule);
        //activityRule.getScenario();
        //System.out.println("YAO-:" + activityRule);
    }

    protected static Activity tryAcquireScenarioActivity(ActivityScenario activityScenario) {
        Semaphore activityResource = new Semaphore(0);
        Activity[] scenarioActivity = new Activity[1];
        activityScenario.onActivity(activity -> {
            scenarioActivity[0] = activity;
            activityResource.release();
            //activity.getDriveService();
        });

        try {
            activityResource.tryAcquire(15000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            //Assert.fail("Failed to acquire activity scenario semaphore");
        }

        //Assert.assertNotNull("Scenario Activity should be non-null", scenarioActivity[0]);
        return scenarioActivity[0];
    }
}

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

import android.util.Log;
import android.content.Intent;
import android.app.Activity;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.drive.Drive;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends Activity {
    private TextView mTextView;
    private Button mButton;
    private int mNumPressed;
    private static final int REQUEST_CODE_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MainActivity";
    private DriveServiceHelper mDriveServiceHelper;

    /**
     * Attaches a simple listener that increments the text in the textbox whenever the button is
     * pressed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("YAO 1111111111111");
        /*
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.main_text_view);
        mButton = (Button) findViewById(R.id.main_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumPressed++;
                mTextView.setText(String.format(Locale.ROOT, "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY Button pressed %d times.", mNumPressed));
            }
        });
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if (account == null) {
            signIn();
        } else {
            System.out.println("YAO account.getEmail:" + account.getEmail());
            mDriveServiceHelper = new DriveServiceHelper(DriveServiceHelper.getGoogleDriveService(getApplicationContext(), account, "appName"));
        }
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
        .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());
                //email.setText(googleSignInAccount.getEmail());
                System.out.println("YAO account.getEmail:" + googleSignInAccount.getEmail());
                mDriveServiceHelper = new DriveServiceHelper(DriveServiceHelper.getGoogleDriveService(getApplicationContext(), googleSignInAccount, "appName"));
                Log.d(TAG, "handleSignInResult: " + mDriveServiceHelper);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to sign in.", e);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
        case REQUEST_CODE_SIGN_IN:
            if (resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                handleSignInResult(resultData);
            }

            break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }


    private void signIn() {
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
        .requestEmail()
        .build();
        return GoogleSignIn.getClient(getApplicationContext(), signInOptions);
    }

    public static String getHello() {
        return "Hello worddddd!!";
    }

    public DriveServiceHelper getDriveService() {
        return mDriveServiceHelper;
    }
}

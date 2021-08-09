package com.eventyay.organizer.core.attendee;

import android.content.Context;
import android.content.Intent;

import com.eventyay.organizer.core.attendee.qrscan.ScanQRActivity;
import com.eventyay.organizer.core.main.MainActivity;

public class ScanningDecider {

    public void openScanQRActivity(Context context, long eventId) {
        Intent intent = new Intent(context, ScanQRActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainActivity.EVENT_KEY, eventId);
        context.startActivity(intent);
    }
}

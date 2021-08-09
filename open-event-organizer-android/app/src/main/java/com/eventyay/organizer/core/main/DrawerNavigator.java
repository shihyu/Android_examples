package com.eventyay.organizer.core.main;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;

import com.eventyay.organizer.OrgaProvider;
import com.eventyay.organizer.R;
import com.eventyay.organizer.core.event.create.CreateEventActivity;
import com.eventyay.organizer.core.organizer.detail.OrganizerDetailActivity;
import com.eventyay.organizer.utils.Utils;

import static com.eventyay.organizer.core.event.create.CreateEventActivity.EVENT_ID;

class DrawerNavigator {

    private static final String GOOGLE_FORM_LINK = "https://docs.google.com/forms/d/e/" +
        "1FAIpQLSfJ-v1mbmNp1ChpsikHDx6HZ5G9Bq8ELCivckPPcYlOAFOy2Q/viewform?usp=sf_link";

    private final Context context;
    private final FragmentNavigator fragmentNavigator;
    private final OrganizerViewModel organizerViewModel;

    private AlertDialog logoutDialog;

    DrawerNavigator(Context context, FragmentNavigator fragmentNavigator, OrganizerViewModel organizerViewModel) {
        this.context = context;
        this.fragmentNavigator = fragmentNavigator;
        this.organizerViewModel = organizerViewModel;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    void setLogoutDialog(AlertDialog logoutDialog) {
        this.logoutDialog = logoutDialog;
    }

    void selectItem(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            showLogoutDialog();
        } else if (id == R.id.nav_account) {
            Intent intent = new Intent(context, OrganizerDetailActivity.class);
            context.startActivity(intent);
        } else if (id == R.id.nav_edit_event) {
            Intent intent = new Intent(context, CreateEventActivity.class);
            intent.putExtra(EVENT_ID, fragmentNavigator.getEventId());
            context.startActivity(intent);
        } else if (id == R.id.nav_share) {
            Utils.shareEvent(OrgaProvider.context);
        } /*else if (id == R.id.nav_suggestion) {
            BrowserUtils.launchUrl(context, GOOGLE_FORM_LINK);
        }*/ else
            fragmentNavigator.loadFragment(id);
    }

    private void showLogoutDialog() {
        if (logoutDialog == null)
            logoutDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.logout_confirmation)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> organizerViewModel.logout())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();

        logoutDialog.show();
    }
}

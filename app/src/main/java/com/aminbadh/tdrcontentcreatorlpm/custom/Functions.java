package com.aminbadh.tdrcontentcreatorlpm.custom;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class Functions {
    public static void DisplaySnackbarStr(View view, String message) {
        Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    public static void DisplaySnackbarRef(View view, int ref) {
        Snackbar.make(view, ref, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    public static boolean getInternetConnectionStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}

package ganainy.dev.gymmasters.utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ganainy.dev.gymmasters.R;

public class NetworkChangeReceiver extends BroadcastReceiver {

    String status;
    AlertDialog alert;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        status = NetworkUtil.getConnectivityStatusString(context);
        ReturnStatus(status, context);
    }

    public void ReturnStatus(String s, final Context cnt) {
        if (s.equals("Mobile data enabled") || s.equals("Wifi enabled")) {
            if (alert!=null && alert.isShowing()) alert.dismiss();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(cnt);
            // Set the Alert Dialog Message
            builder.setTitle(R.string.connection_lost)
                    .setMessage(R.string.app_need_network)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, (dialog, id) -> {
                        if (alert.isShowing()) {
                            alert.dismiss();
                        }
                    });

            alert = builder.create();
            alert.show();
        }
    }

}

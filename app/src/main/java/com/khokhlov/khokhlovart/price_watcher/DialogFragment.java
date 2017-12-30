package com.khokhlov.khokhlovart.price_watcher;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Dom on 16.12.2017.
 */

public class DialogFragment extends android.support.v4.app.DialogFragment{
    AlertDialog.Builder builder;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sign_tmp_header);
        builder.setMessage(R.string.sign_tmp_text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://price-watcher.ru/home"));
//                startActivity(browserIntent);
                dialog.cancel();
            }
        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
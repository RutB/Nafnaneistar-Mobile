package xyz.nafnaneistar.helpers;

import android.app.ProgressDialog;
import android.content.Context;

public class Loaders {

    public static ProgressDialog initDialog(String message, Context context,boolean cancelable){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;

    }
}

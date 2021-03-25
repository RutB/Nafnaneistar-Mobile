package xyz.nafnaneistar.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

public class Loaders {

    public static ProgressBar initProgressBar(Context context){
        ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);

        return pb;

    }
}

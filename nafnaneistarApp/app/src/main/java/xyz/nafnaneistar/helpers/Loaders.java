package xyz.nafnaneistar.helpers;

import android.content.Context;
import android.widget.ProgressBar;

public class Loaders {
    // TODO fallegur loader meðan hlaðið er stórum listum, progressDialog var depricated 😥
    public static ProgressBar initProgressBar(Context context){
        ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);

        return pb;

    }
}

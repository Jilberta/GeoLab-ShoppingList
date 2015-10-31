package ge.geolab.bucket.utils;

import android.graphics.Color;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Jay on 8/30/2015.
 */
public class Formater {

    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        int darkerColor = color;
        Color.colorToHSV(darkerColor, hsv);
        if (color == 0)
            hsv[2] *= 0.2f;
        else
            hsv[2] *= 0.8f; // value component
        darkerColor = Color.HSVToColor(hsv);
        return darkerColor;
    }

    public static void updateProgress(int maxItems, int progress, ProgressBar progressBar, TextView progressBarLabel){
        progressBar.setMax(maxItems);
        progressBar.setProgress(progress);
        if(progress == 0)
            progressBarLabel.setText("0%");
        else
            progressBarLabel.setText((int)((double)progress / maxItems * 100) + "%");
    }
}

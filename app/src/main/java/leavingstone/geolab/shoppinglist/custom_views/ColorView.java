package leavingstone.geolab.shoppinglist.custom_views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import leavingstone.geolab.shoppinglist.R;

/**
 * Created by Jay on 4/28/2015.
 */
public class ColorView extends LinearLayout {
    private View parentView;
    private int color;
    private boolean showTick = false;

    public ColorView(Context context, int color, boolean showTick) {
        super(context);
        this.color = color;
        this.showTick = showTick;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = inflater.inflate(R.layout.color_custom_view, this, true);
        ImageView color = (ImageView) parentView.findViewById(R.id.colorImage);
        ImageView tick = (ImageView) parentView.findViewById(R.id.tickIcon);

        Drawable colorImage = getResources().getDrawable(R.drawable.color_shape);
        ColorFilter filter = new LightingColorFilter(this.color, this.color);
        colorImage.setColorFilter(filter);
        color.setImageDrawable(colorImage);

        if(showTick)
            tick.setVisibility(VISIBLE);
    }

    public int getColor(){
        return this.color;
    }
}

package leavingstone.geolab.shoppinglist.utils;

import android.content.res.Resources;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import leavingstone.geolab.shoppinglist.R;

/**
 * Created by Jay on 9/3/2015.
 */
public class Utils {

    public static void makeGradientBackground(ViewGroup container, Resources resources){
        final int gradientColor = resources.getColor(R.color.background_gradient_color);
        final int color = resources.getColor(R.color.background_color);
        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
//                return new LinearGradient(0, 0, 0, height,
//                        new int[]{gradientColor, color, color, gradientColor},
//                        new float[]{0, 0.02f, 0.92f, 1}, Shader.TileMode.MIRROR);
                return new LinearGradient(0, 0, 0, height,
                        new int[]{color, color, color, color},
                        new float[]{0, 0.02f, 0.92f, 1}, Shader.TileMode.MIRROR);
            }
        };

        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());
        p.setShaderFactory(sf);
        container.setBackground(p);
    }
}

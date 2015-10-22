package leavingstone.geolab.shoppinglist.utils;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Jay on 10/6/2015.
 */
public class AnimationUtils {

    public void animate(RecyclerView.ViewHolder holder, boolean goesDown){
        slidingAnimation(holder, goesDown);
    }

    private void slidingAnimation(RecyclerView.ViewHolder holder, boolean goesDown){
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", goesDown ? 200 : -200, 0);

        animatorTranslateY.setDuration(1000);
        animatorTranslateY.start();
    }
}

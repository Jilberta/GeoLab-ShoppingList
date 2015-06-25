package leavingstone.geolab.shoppinglist.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;

import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.custom_views.ColorView;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 4/28/2015.
 */
public class ListColorDialog extends DialogFragment {
    private int currentColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentColor = getArguments().getInt("Color");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shopping_list_color_dialog_layout, container,
                false);

        getDialog().setTitle(R.string.list_color_dialog_title);

        LinearLayout row1 = (LinearLayout) rootView.findViewById(R.id.row1);
        LinearLayout row2 = (LinearLayout) rootView.findViewById(R.id.row2);

        String [] colors = getResources().getStringArray(R.array.list_colors);

        for(int i = 0; i < colors.length; i++){
            boolean showTick = false;
            int color = Color.parseColor(colors[i]);
            if(currentColor == color)
                showTick = true;
            ColorView newColorView = new ColorView(getActivity(), color, showTick);
            newColorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("ClickedColor", ((ColorView)v).getColor());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                }
            });
            if(i < colors.length / 2)
                row1.addView(newColorView);
            else
                row2.addView(newColorView);
        }

        return rootView;
    }
}

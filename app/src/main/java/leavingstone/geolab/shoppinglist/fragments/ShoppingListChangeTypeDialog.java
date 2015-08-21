package leavingstone.geolab.shoppinglist.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import leavingstone.geolab.shoppinglist.R;

/**
 * Created by Jay on 4/27/2015.
 */
public class ShoppingListChangeTypeDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shopping_list_change_type_dialog_layout, container,
                false);
        getDialog().setTitle("DialogFragment Tutorial");

        Button delete = (Button) rootView.findViewById(R.id.dialog_delete);
        Button keep = (Button) rootView.findViewById(R.id.dialog_keep);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                dismiss();
            }
        });

        keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                dismiss();
            }
        });

        return rootView;
    }
}

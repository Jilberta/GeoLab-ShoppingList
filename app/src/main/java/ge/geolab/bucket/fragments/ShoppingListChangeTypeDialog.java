package ge.geolab.bucket.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import ge.geolab.bucket.R;

/**
 * Created by Jay on 4/27/2015.
 */
public class ShoppingListChangeTypeDialog extends DialogFragment {

    public interface ListChangeTypeDialogListener {
        void onFinishListChangeTypeDialog(int result);
    }

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
                //onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                ((ListChangeTypeDialogListener) getActivity()).onFinishListChangeTypeDialog(Activity.RESULT_OK);
                dismiss();
            }
        });

        keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                ((ListChangeTypeDialogListener) getActivity()).onFinishListChangeTypeDialog(Activity.RESULT_CANCELED);
                dismiss();
            }
        });

        return rootView;
    }
}

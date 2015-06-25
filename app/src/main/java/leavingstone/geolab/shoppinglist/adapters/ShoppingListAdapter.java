package leavingstone.geolab.shoppinglist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;

import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.custom_views.CheckBoxView;
import leavingstone.geolab.shoppinglist.custom_views.ListItemView;
import leavingstone.geolab.shoppinglist.model.ListItemModel;

/**
 * Created by Jay on 3/8/2015.
 */
public class ShoppingListAdapter extends ArrayAdapter<ListItemModel> {
    private Context context;
    private int currentlyFocusedRow = 0;


    public ShoppingListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ListItemView itemView = (ListItemView) convertView;
        if(itemView == null) {
//            itemView = LayoutInflater.from(context).inflate(R.layout.checkbox_custom_view, null, false);
//            itemView = new CheckBoxView(context, null, null);
        }

        ListItemModel listItem = getItem(position);
        itemView.setValue(listItem);

//        EditText textView = (EditText) itemView.getView().findViewById(R.id.text);
//        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
////                if(hasFocus){
////                    currentlyFocusedRow = position;
////                }
//                if (!hasFocus){
//                    final int position = v.getId();
//                    final EditText Caption = (EditText) v;
//                    getItem(position).caption = Caption.getText().toString();
//                }
//            }
//        });

//        textView.setText(String.valueOf(getItem(position)));

        return (View) itemView;
    }
}

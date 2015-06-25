package leavingstone.geolab.shoppinglist.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;

import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 3/7/2015.
 */
public class MainFragmentListAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<ShoppingListModel> shoppingList, filteredShoppingList;
    private ListFilter listFilter;

    public MainFragmentListAdapter(Context context, ArrayList<ShoppingListModel> shoppingList){
        this.context = context;
        this.shoppingList = shoppingList;
        this.filteredShoppingList = this.shoppingList;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return filteredShoppingList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredShoppingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = View.inflate(context, R.layout.fragment_main, null);
        TextView textView = (TextView) itemView.findViewById(R.id.section_label);
        textView.setText(String.valueOf(getItem(position)));

        return itemView;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<ShoppingListModel> tempList;
            if(constraint != null && constraint.length() > 0){

                tempList = DBManager.getShoppingList((String) constraint);

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                tempList = DBManager.getShoppingList(null);
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredShoppingList = (ArrayList<ShoppingListModel>) results.values;
            notifyDataSetChanged();
        }
    }
}

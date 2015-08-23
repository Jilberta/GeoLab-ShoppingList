package leavingstone.geolab.shoppinglist.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.custom_views.ShoppingListItemView;
import leavingstone.geolab.shoppinglist.database.DBHelper;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ListItemModel;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 3/7/2015.
 */
public class MainFragmentListAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<ShoppingListModel> shoppingList, filteredShoppingList;
    private ListFilter listFilter;

    public MainFragmentListAdapter(Context context, ArrayList<ShoppingListModel> shoppingList) {
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
        View itemView = View.inflate(context, R.layout.shopping_listitem_layout_new, null);
        ShoppingListModel list = (ShoppingListModel) getItem(position);

        TextView titleView = (TextView) itemView.findViewById(R.id.title);
        titleView.setText(list.getTitle());

        TextView reminderView = (TextView) itemView.findViewById(R.id.reminder);
        reminderView.setText(list.getAlarmDate());

        LinearLayout itemContainer = (LinearLayout) itemView.findViewById(R.id.item_container);

        ArrayList<ListItemModel> listItems = DBManager.getShoppingListItems(DBHelper.SHOPPING_LIST_ITEM_PARENT_ID + " = " + list.getId());
        double maxItems = listItems.size();
        double checkedCount = 0;
        for (int i = 0; i < listItems.size(); i++) {
            ListItemModel item = listItems.get(i);
            ShoppingListItemView listItem = new ShoppingListItemView(context, item, i + 1, list.getColor());
            itemContainer.addView(listItem);
            if (item.isChecked() == ListItemModel.ListItemState.Checked.ordinal()) {
                checkedCount++;
            }
        }

        TextView progressPrecentageView = (TextView) itemView.findViewById(R.id.progress_percentage_label);
        if(checkedCount == 0){
            progressPrecentageView.setText("0%");
        }else{
            progressPrecentageView.setText((int)(checkedCount / maxItems * 100) + "%");
        }

        ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        progressBar.setMax((int)maxItems);
        progressBar.setProgress((int)checkedCount);

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
            if (constraint != null && constraint.length() > 0) {

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

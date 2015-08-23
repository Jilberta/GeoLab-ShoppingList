package leavingstone.geolab.shoppinglist.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.activities.ShoppingListItemActivity;
import leavingstone.geolab.shoppinglist.custom_views.ShoppingListItemView;
import leavingstone.geolab.shoppinglist.database.DBHelper;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ListItemModel;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 8/23/2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ShoppingListHolder> implements Filterable {

    private Context context;
    private ArrayList<ShoppingListModel> shoppingList, filteredShoppingList;
    private ListFilter listFilter;

    public RecyclerAdapter(Context context, ArrayList<ShoppingListModel> shoppingList) {
        this.context = context;
        this.shoppingList = shoppingList;
        this.filteredShoppingList = this.shoppingList;
    }

    @Override
    public ShoppingListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shopping_listitem_layout_new, viewGroup, false);
        return new ShoppingListHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return this.filteredShoppingList.size();
    }

    @Override
    public void onBindViewHolder(ShoppingListHolder shoppingListHolder, int position) {
        final ShoppingListModel list = this.filteredShoppingList.get(position);
        shoppingListHolder.titleView.setText(list.getTitle());
        shoppingListHolder.reminderView.setText(list.getAlarmDate());

        ArrayList<ListItemModel> listItems = DBManager.getShoppingListItems(DBHelper.SHOPPING_LIST_ITEM_PARENT_ID + " = " + list.getId());
        double maxItems = listItems.size();
        double checkedCount = 0;
        shoppingListHolder.itemContainer.removeAllViews();
        for (int i = 0; i < listItems.size(); i++) {
            ListItemModel item = listItems.get(i);
            ShoppingListItemView listItem = new ShoppingListItemView(context, item, i + 1, list.getColor());
            shoppingListHolder.itemContainer.addView(listItem);
            if (item.isChecked() == ListItemModel.ListItemState.Checked.ordinal()) {
                checkedCount++;
            }
        }

        if(checkedCount == 0){
            shoppingListHolder.progressPercentageView.setText("0%");
        }else{
            shoppingListHolder.progressPercentageView.setText((int)(checkedCount / maxItems * 100) + "%");
        }

        shoppingListHolder.progressBar.setMax((int) maxItems);
        shoppingListHolder.progressBar.setProgress((int) checkedCount);

        shoppingListHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shoppingList = new Intent(context, ShoppingListItemActivity.class);
                Bundle extras = new Bundle();
                extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, list.getId());
                shoppingList.putExtras(extras);
                context.startActivity(shoppingList);
            }
        });
    }

    class ShoppingListHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleView;
        TextView reminderView;
        LinearLayout itemContainer;
        TextView progressPercentageView;
        ProgressBar progressBar;

        public ShoppingListHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            titleView = (TextView) itemView.findViewById(R.id.title);
            reminderView = (TextView) itemView.findViewById(R.id.reminder);
            itemContainer = (LinearLayout) itemView.findViewById(R.id.item_container);
            progressPercentageView = (TextView) itemView.findViewById(R.id.progress_percentage_label);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
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

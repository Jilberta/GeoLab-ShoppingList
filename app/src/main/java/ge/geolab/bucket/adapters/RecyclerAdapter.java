package ge.geolab.bucket.adapters;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andexert.library.RippleView;

import java.util.ArrayList;

import ge.geolab.bucket.R;
import ge.geolab.bucket.activities.ShoppingListItemActivity;
import ge.geolab.bucket.custom_views.ShoppingListItemView;
import ge.geolab.bucket.database.DBHelper;
import ge.geolab.bucket.database.DBManager;
import ge.geolab.bucket.model.ListItemModel;
import ge.geolab.bucket.model.ShoppingListModel;
import ge.geolab.bucket.utils.AnimationUtils;

/**
 * Created by Jay on 8/23/2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ShoppingListHolder> implements Filterable {

    private Context context;
    private ArrayList<ShoppingListModel> shoppingList, filteredShoppingList;
    private ListFilter listFilter;
    private int previousPosition = 0;

    public RecyclerAdapter(Context context, ArrayList<ShoppingListModel> shoppingList) {
        this.context = context;
        this.shoppingList = shoppingList;
        this.filteredShoppingList = shoppingList;
    }

    public void setData(ArrayList<ShoppingListModel> shoppingList){
        this.filteredShoppingList.clear();
        this.filteredShoppingList.addAll(shoppingList);
        notifyDataSetChanged();
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
    public void onBindViewHolder(final ShoppingListHolder shoppingListHolder, int position) {
        final ShoppingListModel list = this.filteredShoppingList.get(position);
        shoppingListHolder.titleView.setText(list.getTitle());

//        if(list.getAlarmDate() == null || list.getAlarmDate().isEmpty())
//            shoppingListHolder.reminderView.setText("--/--/--");
//        else
//            shoppingListHolder.reminderView.setText(list.getAlarmDate());
        if(list.getAlarmDate() != null){
            shoppingListHolder.timeReminderBlock.setVisibility(View.VISIBLE);
            shoppingListHolder.reminderView.setText(list.getAlarmDate());
        }

        if(list.getLocationReminder() != null)
            shoppingListHolder.locationPin.setVisibility(View.VISIBLE);

        ArrayList<ListItemModel> listItems = DBManager.getShoppingListItems(DBHelper.SHOPPING_LIST_ITEM_PARENT_ID + " = " + list.getId());
        double maxItems = listItems.size();
        double checkedCount = 0;
        shoppingListHolder.itemContainer.removeAllViews();
        shoppingListHolder.itemContainer.setBackgroundColor(list.getColor());
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

        shoppingListHolder.rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent shoppingList = new Intent(context, ShoppingListItemActivity.class);
                Bundle extras = new Bundle();
                extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, list.getId());
                shoppingList.putExtras(extras);
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    context.startActivity(shoppingList, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context).toBundle());
////                    ActivityOptionsCompat anim = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, shoppingListHolder.testView, "targetImage");
////                    context.startActivity(shoppingList, anim.toBundle());
//                }else{
//                    context.startActivity(shoppingList);
//                }
                context.startActivity(shoppingList);
                ((Activity) context).overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
            }
        });

        if(position > previousPosition){
            new AnimationUtils().animate(shoppingListHolder, true);
        } else {
            new AnimationUtils().animate(shoppingListHolder, false);
        }
        previousPosition = position;
    }

    class ShoppingListHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CardView testView;
        RippleView rippleView;
        TextView titleView;
        TextView reminderView;
        ImageView locationPin;
        LinearLayout timeReminderBlock;
        LinearLayout itemContainer;
        TextView progressPercentageView;
        ProgressBar progressBar;

        public ShoppingListHolder(View itemView) {
            super(itemView);
            rippleView = (RippleView) itemView.findViewById(R.id.card_view_ripple);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            titleView = (TextView) itemView.findViewById(R.id.title);
            reminderView = (TextView) itemView.findViewById(R.id.reminder);
            locationPin = (ImageView) itemView.findViewById(R.id.location_pin);
            timeReminderBlock = (LinearLayout) itemView.findViewById(R.id.reminder_block);
            itemContainer = (LinearLayout) itemView.findViewById(R.id.item_container);
            progressPercentageView = (TextView) itemView.findViewById(R.id.progress_percentage_label);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

            testView = (CardView) itemView.findViewById(R.id.card_view2);
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

                tempList = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_TITLE + " like '%" + constraint + "%' or " +
                                                    DBHelper.SHOPPING_LIST_TAGS + " like '%" + constraint + "%'");

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
            filteredShoppingList.clear();
            filteredShoppingList.addAll((ArrayList<ShoppingListModel>) results.values);
            notifyDataSetChanged();
        }
    }
}

package leavingstone.geolab.shoppinglist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.database.DBHelper;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 6/3/2015.
 */
public class TagsListAdapter extends BaseAdapter implements Filterable {
    private ShoppingListModel shoppingList;
    private ArrayList<String> tagsList;
    private ArrayList<String> filteredTagsList;
    private TagsFilter tagsFilter;
    private Context context;

    public TagsListAdapter(Context context, ShoppingListModel shoppingList){
        this.context = context;
        this.shoppingList = shoppingList;
        this.tagsList = DBManager.getTags(null);
        this.filteredTagsList = tagsList;

        getFilter();
    }


    @Override
    public int getCount() {
        return filteredTagsList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredTagsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // if(convertView != null)
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.tags_fragment_item, parent, false);

        final String item = (String) getItem(position);
        ((TextView)convertView.findViewById(R.id.text)).setText(item);

        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
        if(this.shoppingList.getTags() != null && this.shoppingList.getTags().contains(item)){
            checkBox.setChecked(true);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (shoppingList.getTags() == null) {
                        shoppingList.setTags(new ArrayList<String>());
                    }
                    shoppingList.getTags().add(item);
                } else {
//                    if(shoppingList.getTags() == null)
//                        return;
                    shoppingList.getTags().remove(item);
                }
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.show_from_bottom);
        convertView.startAnimation(animation);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (tagsFilter == null) {
            tagsFilter = new TagsFilter();
        }

        return tagsFilter;
    }

    private class TagsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<String> tempList;
            if(constraint != null && constraint.length() > 0){

//                for(String tag : tagsList) {
//                    if(tag.toLowerCase().contains(constraint.toString().toLowerCase())){
//                        tempList.add(tag);
//                    }
//                }

                tempList = DBManager.getTags(DBHelper.TAGS_NAME + " like '%" + constraint + "%'");


                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                tempList = DBManager.getTags(null);
//                filterResults.count = tagsList.size();
//                filterResults.values = tagsList;
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredTagsList = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }
}

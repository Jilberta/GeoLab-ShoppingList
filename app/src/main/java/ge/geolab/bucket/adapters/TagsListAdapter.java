package ge.geolab.bucket.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import ge.geolab.bucket.R;
import ge.geolab.bucket.database.DBHelper;
import ge.geolab.bucket.database.DBManager;
import ge.geolab.bucket.model.ShoppingListModel;

/**
 * Created by Jay on 6/3/2015.
 */
public class TagsListAdapter extends BaseAdapter implements Filterable {
    private ShoppingListModel shoppingList;
    private ArrayList<String> tagsList;
    private ArrayList<String> filteredTagsList;
    private TagsFilter tagsFilter;
    private Context context;
    private int previousPosition;

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

//        Animation animation;
//        if(position > previousPosition){
//            animation = AnimationUtils.loadAnimation(context, R.anim.tags_bottom_up);
//        } else {
//            animation = AnimationUtils.loadAnimation(context, R.anim.tags_bottom_down);
//        }
//        convertView.startAnimation(animation);
//        previousPosition = position;

        ObjectAnimator animatorTranslateY;
        if(position > previousPosition){
            animatorTranslateY = ObjectAnimator.ofFloat(convertView, "translationY", 200, 0);
            animatorTranslateY.setDuration(500);
            animatorTranslateY.start();
        } else {
            animatorTranslateY = ObjectAnimator.ofFloat(convertView, "translationY", -200, 0);
            animatorTranslateY.setDuration(500);
            animatorTranslateY.start();
        }
        previousPosition = position;

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

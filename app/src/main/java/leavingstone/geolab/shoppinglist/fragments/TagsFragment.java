package leavingstone.geolab.shoppinglist.fragments;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import leavingstone.geolab.shoppinglist.MainActivity;
import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.adapters.TagsListAdapter;
import leavingstone.geolab.shoppinglist.async.ListItemsUpdater;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 6/1/2015.
 */
public class TagsFragment extends Fragment {
    private ShoppingListModel shoppingList;
    private ArrayList<String> tags;

    private SearchView searchView;

    private LinearLayout newTag;
    private ListView tagsList;

    public static TagsFragment newInstance() {
        return new TagsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            shoppingList = (ShoppingListModel) getArguments().getSerializable(ShoppingListModel.SHOPPING_LIST_MODEL_KEY);

//            ArrayList<String> array = new ArrayList<String>();
//            for (int i = 0; i < 10; i++) {
//                array.add("Geo " + i);
//            }
//
//            shoppingList.setTags(array);

//            System.out.println(shoppingList);
        }

        this.tags = DBManager.getTags(null);

//        this.tags = new ArrayList<String>();
//        for (int i = 0; i < 10; i++) {
//            if(i%2 == 0)
//                this.tags.add("Avoe " + i);
//            else
//                this.tags.add("Geo " + i);
//        }

//        ActionBar actionbar = ((ActionBarActivity)getActivity()).getSupportActionBar();
//        actionbar.setDisplayShowTitleEnabled(false);
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setDisplayShowHomeEnabled(true);

//        LayoutInflater mInflater = LayoutInflater.from(getActivity());
//        View mCustomView = mInflater.inflate(R.layout.tags_fragment_item, null);
//        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.text);
//        mTitleTextView.setText("My Own Title");
//        actionbar.setCustomView(mCustomView);
//        actionbar.setDisplayShowCustomEnabled(true);

//        ((MainActivity) getActivity()).resetActionBar(true, 0);

//        ((MainActivity) getActivity()).getActionBarToolbar().setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tags_fragment_menu, menu);


        MenuItem item = menu.findItem(R.id.search);
//        item.expandActionView();
        searchView = (SearchView) item.getActionView();
        searchView.setIconified(false);

        ImageView searchViewClose = (ImageView) searchView.findViewById(R.id.search_close_btn);
        searchViewClose.setVisibility(View.GONE);

        ImageView magImage = (ImageView) searchView.findViewById(R.id.search_button);
        magImage.setVisibility(View.GONE);
//        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

//        LinearLayout searchBar = (LinearLayout) searchView.findViewById(R.id.search_bar);
//        searchBar.setLayoutTransition(new LayoutTransition());
//        searchView.setIconified(true);
//        searchView.onActionViewCollapsed();
        searchView.setQueryHint("თეგის დამატება");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(getView() != null){
//                    ListView labels = (ListView) getView().findViewById(R.id.listview);
//                    LinearLayout newTag = (LinearLayout) getView().findViewById(R.id.newTag);
                    TextView newTagText = (TextView) newTag.findViewById(R.id.newTagText);
                    if(s == null || s.isEmpty()) {
                        newTagText.setText("");
                        newTag.setVisibility(View.GONE);
                    } else if(!tags.contains(s)){
                        newTagText.setText("\"" + s + "\"");
                        newTag.setVisibility(View.VISIBLE);
                    } else {
                        newTagText.setText("");
                        newTag.setVisibility(View.GONE);
                    }
                    ((TagsListAdapter)tagsList.getAdapter()).getFilter().filter(s);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tags_fragment_view, container, false);

        LinearLayout tagsContainer = (LinearLayout) rootView.findViewById(R.id.tags_container);
        tagsContainer.setLayoutTransition(new LayoutTransition());

        newTag = (LinearLayout) rootView.findViewById(R.id.newTag);

        tagsList = (ListView) rootView.findViewById(R.id.listview);
        TagsListAdapter adapter = new TagsListAdapter(getActivity(), shoppingList);
        tagsList.setAdapter(adapter);


        final TextView newTagText = (TextView) rootView.findViewById(R.id.newTagText);
        newTagText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = (String) newTagText.getText();
                txt = txt.replace("\"", "");
                System.out.println(txt);
                DBManager.insertTag(txt);
                tags.add(txt);
                newTag.setVisibility(View.GONE);
                searchView.setQuery("", false);
                ((TagsListAdapter) tagsList.getAdapter()).getFilter().filter(null);
            }
        });

        ImageButton newTagButton = (ImageButton) rootView.findViewById(R.id.newTagButton);
        newTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = (String) newTagText.getText();
                txt = txt.replace("\"", "");
                System.out.println(txt);
                DBManager.insertTag(txt);
                tags.add(txt);
                newTag.setVisibility(View.GONE);
                searchView.setQuery("", false);
                ((TagsListAdapter) tagsList.getAdapter()).getFilter().filter(null);
            }
        });

        TextView newTagPrefix = (TextView) rootView.findViewById(R.id.newTagPrefix);
        newTagPrefix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = (String) newTagText.getText();
                txt = txt.replace("\"", "");
                System.out.println(txt);
                DBManager.insertTag(txt);
                tags.add(txt);
                newTag.setVisibility(View.GONE);
                searchView.setQuery("", false);
                ((TagsListAdapter) tagsList.getAdapter()).getFilter().filter(null);
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        new ListItemsUpdater(getActivity()).execute(shoppingList, null);
    }
    
}

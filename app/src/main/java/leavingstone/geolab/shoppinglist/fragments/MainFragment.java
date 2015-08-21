package leavingstone.geolab.shoppinglist.fragments;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.adapters.MainFragmentListAdapter;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 3/7/2015.
 */
public class MainFragment extends Fragment {

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        ActionBar actionbar = ((ActionBarActivity)getActivity()).getSupportActionBar();


        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        final View mCustomView = mInflater.inflate(R.layout.test, null);
        SearchView searchView = (SearchView) mCustomView.findViewById(R.id.search);

      //  searchView.setIconifiedByDefault(false);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Daechira Search -s");

                LinearLayout buttons = (LinearLayout) mCustomView.findViewById(R.id.buttons);
                buttons.setVisibility(View.VISIBLE);
            }
        });

        actionbar.setCustomView(mCustomView);
        actionbar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
      //  inflater.inflate(R.menu.tags_fragment_menu, menu);


//        MenuItem item = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) item.getActionView();
//
//        searchView.setIconifiedByDefault(false);
//
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("Daechira Search -s");
//            }
//        });
    }


    private DynamicListView list;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_fragment_layout, container, false);

        list = (DynamicListView) rootView.findViewById(android.R.id.list);
        MainFragmentListAdapter adapter = new MainFragmentListAdapter(getActivity(), DBManager.getShoppingList(null));
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(list);
        list.setAdapter(animationAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShoppingListFragment shoppingListFragment = ShoppingListFragment.newInstance();
                Bundle extras = new Bundle();
                extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, ((ShoppingListModel) parent.getAdapter().getItem(position)).getId());
                shoppingListFragment.setArguments(extras);
                FragmentTransaction ft = getFragmentManager().beginTransaction()
                        .replace(R.id.container, shoppingListFragment)
                        .addToBackStack("transaction");
                ft.commit();
            }
        });

        Button newList = (Button) rootView.findViewById(R.id.newList);
        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime());
                ShoppingListModel shoppingListModel = new ShoppingListModel(ShoppingListModel.ShoppingListType.WithCheckboxes.ordinal(), date);
                long id = DBManager.insertShoppingList(shoppingListModel);
                shoppingListModel.setId(id);

                ShoppingListFragment shoppingListFragment = ShoppingListFragment.newInstance();
                Bundle extras = new Bundle();
                extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingListModel.getId());
                shoppingListFragment.setArguments(extras);
                FragmentTransaction ft = getFragmentManager().beginTransaction()
                        .replace(R.id.container, shoppingListFragment)
                        .addToBackStack("transaction");
                ft.commit();

            }
        });

        return rootView;
    }
}

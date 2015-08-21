package leavingstone.geolab.shoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import leavingstone.geolab.shoppinglist.activities.ShoppingListItemActivity;
import leavingstone.geolab.shoppinglist.adapters.MainFragmentListAdapter;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar toolbar;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        try {
            DBManager.init(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        ActionBar actionbar = getSupportActionBar();
//        if(actionbar != null){
//            actionbar.setDisplayHomeAsUpEnabled(true);
//        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
                R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().add("Label 25");


//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, MainFragment.newInstance())
//                .commit();
//
//        if (getIntent() != null) {
//            if (getIntent().getAction() != null && getIntent().getAction().equals(getString(R.string.shopping_list_fragment))) {
//                ShoppingListFragment shoppingListFragment = ShoppingListFragment.newInstance();
//                Bundle extras = getIntent().getExtras();
//                System.out.println(extras);
//                shoppingListFragment.setArguments(extras);
//                fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, shoppingListFragment)
//                        .addToBackStack("transaction")
//                        .commit();
//            }
//        }



        ListView list = (ListView) findViewById(android.R.id.list);
        MainFragmentListAdapter adapter = new MainFragmentListAdapter(this, DBManager.getShoppingList(null));
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(list);
        list.setAdapter(animationAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ShoppingListFragment shoppingListFragment = ShoppingListFragment.newInstance();
//                Bundle extras = new Bundle();
//                extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, ((ShoppingListModel) parent.getAdapter().getItem(position)).getId());
//                shoppingListFragment.setArguments(extras);
//                FragmentTransaction ft = getFragmentManager().beginTransaction()
//                        .replace(R.id.container, shoppingListFragment)
//                        .addToBackStack("transaction");
//                ft.commit();

                Intent shoppingList = new Intent(mActivity, ShoppingListItemActivity.class);
                Bundle extras = new Bundle();
                ShoppingListModel mda = ((ShoppingListModel) parent.getAdapter().getItem(position));
                extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, ((ShoppingListModel) parent.getAdapter().getItem(position)).getId());
                shoppingList.putExtras(extras);
                startActivity(shoppingList);
            }
        });

        Button newList = (Button)findViewById(R.id.newList);
        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime());
                ShoppingListModel shoppingListModel = new ShoppingListModel(ShoppingListModel.ShoppingListType.WithCheckboxes.ordinal(), date);
                long id = DBManager.insertShoppingList(shoppingListModel);
                shoppingListModel.setId(id);

//                ShoppingListFragment shoppingListFragment = ShoppingListFragment.newInstance();
//                Bundle extras = new Bundle();
//                extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingListModel.getId());
//                shoppingListFragment.setArguments(extras);
//                FragmentTransaction ft = getFragmentManager().beginTransaction()
//                        .replace(R.id.container, shoppingListFragment)
//                        .addToBackStack("transaction");
//                ft.commit();


                Intent shoppingList = new Intent(mActivity, ShoppingListItemActivity.class);
                Bundle extras = new Bundle();
                extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingListModel.getId());
                shoppingList.putExtras(extras);
                startActivity(shoppingList);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
//
//
//            restoreActionBar();
//            return true;
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

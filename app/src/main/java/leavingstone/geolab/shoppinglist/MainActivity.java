package leavingstone.geolab.shoppinglist;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Choreographer;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import leavingstone.geolab.shoppinglist.activities.ShoppingListItemActivity;
import leavingstone.geolab.shoppinglist.adapters.MainFragmentListAdapter;
import leavingstone.geolab.shoppinglist.adapters.RecyclerAdapter;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;
import leavingstone.geolab.shoppinglist.utils.Utils;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar toolbar;
    private Activity mActivity;
    private int mScrollOffset = 4;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        Utils.makeGradientBackground(container, getResources());

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


//        ListView list = (ListView) findViewById(android.R.id.list);
//        MainFragmentListAdapter adapter = new MainFragmentListAdapter(this, DBManager.getShoppingList(null));
//        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(adapter);
//        animationAdapter.setAbsListView(list);
//        list.setAdapter(animationAdapter);

        RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerAdapter adapter = new RecyclerAdapter(this, DBManager.getShoppingList(null));
        recyclerView.setAdapter(adapter);

    /*    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });*/

        final FloatingActionButton newList = (FloatingActionButton) findViewById(R.id.newList);
        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime());
                ShoppingListModel shoppingListModel = new ShoppingListModel(ShoppingListModel.ShoppingListType.WithCheckboxes.ordinal(), date);
                long id = DBManager.insertShoppingList(shoppingListModel);
                shoppingListModel.setId(id);
//                String [] colors = getResources().getStringArray(R.array.list_colors);
//                shoppingListModel.setColor(Color.parseColor(colors[0]));

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


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > mScrollOffset) {
                    if (dy > 0) {
                        newList.hide();
                    } else {
                        newList.show();
                    }
                }
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
//
//
//
//            restoreActionBar();
//            return true;
//        }
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        ImageView button = (ImageView) searchView.findViewById(R.id.search_button);
        button.setImageResource(R.drawable.search_icon);

        LinearLayout searchBar = (LinearLayout) searchView.findViewById(R.id.search_bar);
        searchBar.setLayoutTransition(new LayoutTransition());

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

package leavingstone.geolab.shoppinglist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import leavingstone.geolab.shoppinglist.activities.ShoppingListItemActivity;
import leavingstone.geolab.shoppinglist.adapters.RecyclerAdapter;
import leavingstone.geolab.shoppinglist.database.DBHelper;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;
import leavingstone.geolab.shoppinglist.utils.Utils;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar toolbar, toolbar2;
    private TextView toolbarTitle;
    private LinearLayout filterTab;
    private Activity mActivity;
    private int mScrollOffset = 4;
    private RecyclerView recyclerView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUpAnimation(){
        Explode explode = (Explode) TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
        explode.setDuration(500);
        getWindow().setExitTransition(explode);

        Fade fade = new Fade();
        fade.setDuration(500);
        getWindow().setReenterTransition(fade);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setUpAnimation();
        }

        mActivity = this;

        try {
            DBManager.init(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        toolbar2.inflateMenu(R.menu.search_menu);
        final SearchView searchView = (SearchView)toolbar2.findViewById(R.id.action_search);
        searchView.setIconified(false);
        final ImageView searchViewClose = (ImageView) searchView.findViewById(R.id.search_close_btn);
        searchViewClose.setVisibility(View.GONE);
        searchView.findViewById(R.id.search_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("", false);
                searchViewClose.setVisibility(View.GONE);
            }
        });


        toolbar2.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility(View.VISIBLE);
                toolbar2.setVisibility(View.GONE);
                filterTab.setVisibility(View.GONE);
                searchView.setQuery("", false);
            }
        });
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        filterTab = (LinearLayout) findViewById(R.id.filter_tool);
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

        final ArrayList<String> tags = DBManager.getTags(null);
        for(int i = 0; i < tags.size(); i++) {
            navigationView.getMenu().add(R.id.labels, Menu.NONE, i, tags.get(i)).setIcon(R.drawable.ic_label_black_24dp).setCheckable(true);
        }

        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        Utils.makeGradientBackground(container, getResources());

        recyclerView = (RecyclerView) findViewById(android.R.id.list);
        recyclerView.setItemAnimator(new ScaleInAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        final RecyclerAdapter adapter = new RecyclerAdapter(this, DBManager.getShoppingList(null));
//        recyclerView.setAdapter(new jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter(adapter));
        recyclerView.setAdapter(adapter);

        final FloatingActionButton newList = (FloatingActionButton) findViewById(R.id.newList);
        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime());
                ShoppingListModel shoppingListModel = new ShoppingListModel(ShoppingListModel.ShoppingListType.WithCheckboxes.ordinal(), date);
                long id = DBManager.insertShoppingList(shoppingListModel);
                shoppingListModel.setId(id);

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

        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        filterTab.findViewById(R.id.time_reminder_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ShoppingListModel> list = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_ALARMDATE + " IS NOT NULL");
                ((RecyclerAdapter) recyclerView.getAdapter()).setData(list);
            }
        });

        filterTab.findViewById(R.id.location_reminder_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ShoppingListModel> list = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_LOCATION + " IS NOT NULL");
                ((RecyclerAdapter)recyclerView.getAdapter()).setData(list);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

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
        } else if(id == R.id.action_search_button){
            toolbar.setVisibility(View.GONE);
            toolbar2.setVisibility(View.VISIBLE);
            filterTab.setVisibility(View.VISIBLE);
            toolbar2.findViewById(R.id.action_search).findViewById(R.id.search_close_btn).setVisibility(View.GONE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package ge.geolab.bucket;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import ge.geolab.bucket.activities.ShoppingListItemActivity;
import ge.geolab.bucket.adapters.RecyclerAdapter;
import ge.geolab.bucket.database.DBHelper;
import ge.geolab.bucket.database.DBManager;
import ge.geolab.bucket.fragments.ListColorDialog;
import ge.geolab.bucket.model.ShoppingListModel;
import ge.geolab.bucket.utils.Utils;


public class MainActivity extends ActionBarActivity implements ListColorDialog.ListColorDialogListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Activity mActivity;
    private int mScrollOffset = 4;
    private RecyclerView recyclerView;

    private Toolbar toolbar, toolbar2;
    private TextView toolbarTitle;
    private LinearLayout filterTab;
    private ImageButton filterTimeReminder, filterLocationReminder, filterColorNotes, filterShareNote;
    private SearchView searchView;
    private boolean filterTimeReminderIsPressed = false, filterLocationReminderIsPressed = false,
            filterSharedNotesIsPressed = false, filterColorNotesIsPressed = false;
    private int filterChosenColor;

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

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setUpAnimation();
//        }

        mActivity = this;

        try {
            DBManager.init(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        toolbar2.inflateMenu(R.menu.search_menu);
        searchView = (SearchView)toolbar2.findViewById(R.id.action_search);
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
        searchView.clearFocus();


        toolbar2.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility(View.VISIBLE);
                toolbar2.setVisibility(View.GONE);
                filterTab.setVisibility(View.GONE);
//                searchView.setQuery("", false);
                closeSearchTab();
            }
        });
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        filterTab = (LinearLayout) findViewById(R.id.filter_tool);
        filterTimeReminder = (ImageButton) filterTab.findViewById(R.id.time_reminder_notes);
        filterLocationReminder = (ImageButton) filterTab.findViewById(R.id.location_reminder_notes);
        filterColorNotes = (ImageButton) filterTab.findViewById(R.id.color_notes);

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

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if(menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                if(menuItem.getItemId() == R.id.navigation_item_notes){
                    closeSearchTab();
                    return true;
                } else if (menuItem.getItemId() == R.id.navigation_item_reminders){
                    getRemindersList();
                    return true;
                }
//                else if (menuItem.getItemId() == R.id.navigation_item_about_us){
//                    System.out.println();
//                    return true;
//                }
                else {
                    if(menuItem.getItemId() == Menu.NONE){
                        String title = (String) menuItem.getTitle();
                        getTaggedNotes(title);
                    }
                    return true;
                }
            }
        });


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

                overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
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
                //adapter.getFilter().filter(newText);
                getFilteredList(newText);
                return true;
            }
        });

        filterTimeReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<ShoppingListModel> list = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_ALARMDATE + " IS NOT NULL");
//                ((RecyclerAdapter) recyclerView.getAdapter()).setData(list);
                if (filterTimeReminderIsPressed) {
                    v.setBackgroundColor(Color.TRANSPARENT);
                    filterTimeReminderIsPressed = false;
                } else {
                    v.setBackground(getResources().getDrawable(R.drawable.filter_icon_pressed));
                    filterTimeReminderIsPressed = true;
                }
                getFilteredList(null);
            }
        });

        filterLocationReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<ShoppingListModel> list = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_LOCATION + " IS NOT NULL");
//                ((RecyclerAdapter)recyclerView.getAdapter()).setData(list);
                if (filterLocationReminderIsPressed) {
                    v.setBackgroundColor(Color.TRANSPARENT);
                    filterLocationReminderIsPressed = false;
                } else {
                    v.setBackground(getResources().getDrawable(R.drawable.filter_icon_pressed));
                    filterLocationReminderIsPressed = true;
                }
                getFilteredList(null);
            }
        });

        filterColorNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterColorNotesIsPressed) {
                    v.setBackgroundColor(Color.TRANSPARENT);
                    filterColorNotesIsPressed = false;
                    filterChosenColor = -1;
                    getFilteredList(null);
                } else {
                    v.setBackground(getResources().getDrawable(R.drawable.filter_icon_pressed));
                    filterColorNotesIsPressed = true;
                    ListColorDialog colorDialog = new ListColorDialog();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Color", filterChosenColor);
                    colorDialog.setArguments(bundle);
//                    colorDialog.setTargetFragment(this, COLOR_DIALOG_FRAGMENT);
                    colorDialog.show(getSupportFragmentManager(), "ShoppingListColorDialog");
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

    @Override
    public void onFinishListColorDialog(int color) {
        filterChosenColor = color;
//        ArrayList<ShoppingListModel> list = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_LOCATION + " IS NOT NULL");
//        ((RecyclerAdapter)recyclerView.getAdapter()).setData(list);
        getFilteredList(null);
    }

    private void getFilteredList(String query){
        String whereQuery = null;
        if(filterTimeReminderIsPressed){
            if(whereQuery != null)
                whereQuery += " and " + DBHelper.SHOPPING_LIST_ALARMDATE + " IS NOT NULL";
            else {
                whereQuery = "";
                whereQuery += DBHelper.SHOPPING_LIST_ALARMDATE + " IS NOT NULL";
            }
        }

        if(filterLocationReminderIsPressed){
            if(whereQuery != null)
                whereQuery += " and " + DBHelper.SHOPPING_LIST_LOCATION + " IS NOT NULL";
            else {
                whereQuery = "";
                whereQuery += DBHelper.SHOPPING_LIST_LOCATION + " IS NOT NULL";
            }
        }

        if(filterColorNotesIsPressed){
            if(whereQuery != null)
                whereQuery += " and " + DBHelper.SHOPPING_LIST_COLOR + " = " + filterChosenColor;
            else {
                whereQuery = "";
                whereQuery += DBHelper.SHOPPING_LIST_COLOR + " = " + filterChosenColor;
            }
        }

        if(query != null && !query.isEmpty()){
            if(whereQuery != null)
                whereQuery += " and (" + DBHelper.SHOPPING_LIST_TITLE + " like '%" + query + "%' or " +
                        DBHelper.SHOPPING_LIST_TAGS + " like '%" + query + "%')";
            else {
                whereQuery = "";
                whereQuery += DBHelper.SHOPPING_LIST_TITLE + " like '%" + query + "%' or " +
                        DBHelper.SHOPPING_LIST_TAGS + " like '%" + query + "%'";
            }
        }

        ArrayList<ShoppingListModel> list = DBManager.getShoppingList(whereQuery);
        ((RecyclerAdapter)recyclerView.getAdapter()).setData(list);
    }

    private void closeSearchTab(){
        filterTimeReminderIsPressed = false;
        filterTimeReminder.setBackgroundColor(Color.TRANSPARENT);

        filterLocationReminderIsPressed = false;
        filterLocationReminder.setBackgroundColor(Color.TRANSPARENT);

        filterColorNotesIsPressed = false;
        filterChosenColor = -1;
        filterColorNotes.setBackgroundColor(Color.TRANSPARENT);

        if(searchView.getQuery().toString().isEmpty())
            getFilteredList(null);
        else
            searchView.setQuery("", false);
    }

    private void getRemindersList(){
        String whereQuery = DBHelper.SHOPPING_LIST_ALARMDATE + " IS NOT NULL or " + DBHelper.SHOPPING_LIST_LOCATION + " IS NOT NULL";
        ArrayList<ShoppingListModel> list = DBManager.getShoppingList(whereQuery);
        ((RecyclerAdapter)recyclerView.getAdapter()).setData(list);
    }

    private void getTaggedNotes(String tag){
        String whereQuery =  DBHelper.SHOPPING_LIST_TAGS + " like '%" + tag + "%'";
        ArrayList<ShoppingListModel> list = DBManager.getShoppingList(whereQuery);
        ((RecyclerAdapter)recyclerView.getAdapter()).setData(list);
    }
}

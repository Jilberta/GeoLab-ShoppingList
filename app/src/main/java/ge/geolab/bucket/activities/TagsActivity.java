package ge.geolab.bucket.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import ge.geolab.bucket.R;
import ge.geolab.bucket.fragments.TagsFragment;
import ge.geolab.bucket.model.ShoppingListModel;
import ge.geolab.bucket.utils.Utils;

public class TagsActivity extends ActionBarActivity {
    private ShoppingListModel shoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);

        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        Utils.makeGradientBackground(container, getResources());


        if (getIntent().getExtras() != null) {
            // long id = getIntent().getExtras().getLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY);
            //shoppingList = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_ID + " = " + id).get(0);
            shoppingList = (ShoppingListModel) getIntent().getExtras().getSerializable(ShoppingListModel.SHOPPING_LIST_MODEL_KEY);
            System.out.println(shoppingList);
        }

        TagsFragment tagsFragment = TagsFragment.newInstance();
        Bundle extras = new Bundle();
        extras.putSerializable(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingList);
        tagsFragment.setArguments(extras);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, tagsFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tags, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            Intent intent = new Intent(this, ShoppingListItemActivity.class);
            Bundle extras = new Bundle();
            extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingList.getId());
            intent.putExtras(extras);
            startActivity(intent);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

//        if(id == R.id.test){
//            Intent shoppingList = new Intent(this, MainActivity.class);
//            startActivity(shoppingList);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent backtoHome = new Intent(this, ShoppingListItemActivity.class);
        backtoHome.addCategory(Intent.CATEGORY_HOME);
        backtoHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle extras = new Bundle();
        extras.putLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingList.getId());
        backtoHome.putExtras(extras);
        startActivity(backtoHome);
    }
}

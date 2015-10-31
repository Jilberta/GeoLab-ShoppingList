package ge.geolab.bucket.async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import ge.geolab.bucket.database.DBHelper;
import ge.geolab.bucket.database.DBManager;
import ge.geolab.bucket.model.ListItemModel;
import ge.geolab.bucket.model.ShoppingListModel;

/**
 * Created by Jay on 4/18/2015.
 */
public class ListItemsUpdater extends AsyncTask {
    private Context context;

    public ListItemsUpdater(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        ShoppingListModel shoppingList = (ShoppingListModel) params[0];
        ArrayList<ListItemModel> listItems = (ArrayList<ListItemModel>) params[1];

        if(shoppingList != null){
            if(shoppingList.isDeleted()){
                DBManager.deleteItem(DBHelper.SHOPPING_LIST_ITEM_TABLE, DBHelper.SHOPPING_LIST_ITEM_PARENT_ID, shoppingList.getId());
                DBManager.deleteItem(DBHelper.SHOPPING_LIST_TABLE, DBHelper.SHOPPING_LIST_ID, shoppingList.getId());
                listItems = null;
            }else{
                DBManager.updateShoppingList(shoppingList);
            }
        }

        if(listItems != null){
            for (int i = 0; i < listItems.size(); i++) {
                ListItemModel item = listItems.get(i);
                if(item.isDeleted()){
                    DBManager.deleteItem(DBHelper.SHOPPING_LIST_ITEM_TABLE, DBHelper.SHOPPING_LIST_ITEM_ID, item.getId());
                } else if(item.isChanged()) {
                    DBManager.updateShoppingListItem(item);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
//        Toast.makeText(context, "DaaUpdata", Toast.LENGTH_LONG).show();
    }
}

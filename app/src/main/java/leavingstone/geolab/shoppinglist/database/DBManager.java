package leavingstone.geolab.shoppinglist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import java.util.ArrayList;

import leavingstone.geolab.shoppinglist.model.ListItemModel;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 4/10/2015.
 */
public class DBManager {
    private static DBHelper dbHelper;
    private static SQLiteDatabase db;

    public static void init(Context context) throws SQLException {
        if(dbHelper == null){
            dbHelper = new DBHelper(context);
            open();
        }
    }

    public static void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public static void close() throws SQLException {
        db.close();
    }

    public static void dropTables(){
        DBHelper.dropTables(db);
    }

    /**
     * ShoppingList Methods
     * @param shoppingList
     * @return
     */

    public static long insertShoppingList(ShoppingListModel shoppingList){
        ContentValues values = new ContentValues();
        values.put(DBHelper.SHOPPING_LIST_TYPE, shoppingList.getType());
        values.put(DBHelper.SHOPPING_LIST_DATE, shoppingList.getDate());

        return db.insert(DBHelper.SHOPPING_LIST_TABLE, null, values);
    }

    public static int updateShoppingList(ShoppingListModel shoppingList){
        ContentValues values = new ContentValues();
        values.put(DBHelper.SHOPPING_LIST_TYPE, shoppingList.getType());
//        values.put(DBHelper.SHOPPING_LIST_DATE, shoppingList.getDate());

        values.put(DBHelper.SHOPPING_LIST_TITLE, shoppingList.getTitle());
        values.put(DBHelper.SHOPPING_LIST_COLOR, shoppingList.getColor());
        values.put(DBHelper.SHOPPING_LIST_TAGS, shoppingList.getTagsJson());
        values.put(DBHelper.SHOPPING_LIST_LOCATION, shoppingList.getLocationReminderJson());
        values.put(DBHelper.SHOPPING_LIST_ALARMDATE, shoppingList.getAlarmDate());
        values.put(DBHelper.SHOPPING_LIST_SHAREDPEOPLE, shoppingList.getSharedPeopleJson());

        return db.update(DBHelper.SHOPPING_LIST_TABLE, values, DBHelper.SHOPPING_LIST_ID + " = " + shoppingList.getId(), null);
    }

    public static ArrayList<ShoppingListModel> getShoppingList(String whereQuery){
        ArrayList<ShoppingListModel> shoppingList = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.SHOPPING_LIST_TABLE, null, whereQuery, null, null, null, DBHelper.SHOPPING_LIST_DATE + " DESC");
        if(cursor.moveToFirst()){
            do {
                long id = cursor.getLong(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_ID));
                String date = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_DATE));
                int type = cursor.getInt(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_TYPE));
                String title = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_TITLE));
                int color = cursor.getInt(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_COLOR));
                String location = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_LOCATION));
                String alarmDate = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_ALARMDATE));
                String tags = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_TAGS));
                String sharedPeople = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_SHAREDPEOPLE));
                ShoppingListModel shoppingListModel = new ShoppingListModel(type, date);
                shoppingListModel.setId(id);
                shoppingListModel.setTitle(title);
                shoppingListModel.setColor(color);
                shoppingListModel.setLocationReminderJson(location);
                shoppingListModel.setAlarmDate(alarmDate);
                shoppingListModel.setTagsJson(tags);
                shoppingListModel.setSharedPeopleJson(sharedPeople);
                shoppingList.add(shoppingListModel);
            } while(cursor.moveToNext());
        }
        return shoppingList;
    }

    public static boolean deleteItem(String tableName, String idKey, long id){
        return db.delete(tableName, idKey + " = " + id, null) > 0;
    }

    /**
     * ShoppingList Item Methods
     */

    public static long insertListItem(ListItemModel listitem){
        ContentValues values = new ContentValues();
        values.put(DBHelper.SHOPPING_LIST_ITEM_PARENT_ID, listitem.getParentId());
        values.put(DBHelper.SHOPPING_LIST_ITEM_VALUE, listitem.getValue());
        values.put(DBHelper.SHOPPING_LIST_ITEM_ISCHECKED, listitem.isChecked());

        return db.insert(DBHelper.SHOPPING_LIST_ITEM_TABLE, null, values);
    }

    public static int updateShoppingListItem(ListItemModel listitem){
        ContentValues values = new ContentValues();
        values.put(DBHelper.SHOPPING_LIST_ITEM_PARENT_ID, listitem.getParentId());
        values.put(DBHelper.SHOPPING_LIST_ITEM_VALUE, listitem.getValue());
        values.put(DBHelper.SHOPPING_LIST_ITEM_ISCHECKED, listitem.isChecked());

        return db.update(DBHelper.SHOPPING_LIST_ITEM_TABLE, values, DBHelper.SHOPPING_LIST_ITEM_ID + " = " + listitem.getId(), null);
    }

    public static ArrayList<ListItemModel> getShoppingListItems(String whereQuery){
        ArrayList<ListItemModel> listItems = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.SHOPPING_LIST_ITEM_TABLE, null, whereQuery, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                long id = cursor.getLong(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_ITEM_ID));
                long parentId = cursor.getLong(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_ITEM_PARENT_ID));
                int isChecked = cursor.getInt(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_ITEM_ISCHECKED));
                String value = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPPING_LIST_ITEM_VALUE));
                ListItemModel listItem = new ListItemModel(parentId, value, isChecked);
                listItem.setId(id);
                listItems.add(listItem);
            } while(cursor.moveToNext());
        }
        return listItems;
    }

    public static ArrayList<String> getTags(String whereQuery){
        ArrayList<String> tags = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TAGS_TABLE, null, whereQuery, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                String tag = cursor.getString(cursor.getColumnIndex(DBHelper.TAGS_NAME));
                tags.add(tag);
            } while(cursor.moveToNext());
        }
        return tags;
    }

    public static long insertTag(String tag){
        ContentValues values = new ContentValues();
        values.put(DBHelper.TAGS_NAME, tag);

        return db.insert(DBHelper.TAGS_TABLE, null, values);
    }

}

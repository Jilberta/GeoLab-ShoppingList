package ge.geolab.bucket.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jay on 4/10/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GeoLabShoppingList";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTables(SQLiteDatabase db){
        db.execSQL(createListTable());
        db.execSQL(createListItemTable());
        db.execSQL(createTagsTable());
    }

    /**
     * ???????? Public
     * @param db
     */
    public static void dropTables(SQLiteDatabase db){
        db.execSQL(dropTable(SHOPPING_LIST_ITEM_TABLE));
        db.execSQL(dropTable(SHOPPING_LIST_TABLE));
        db.execSQL(dropTable(TAGS_TABLE));
    }


    public static final String SHOPPING_LIST_TABLE = "SHOPPING_LIST_TABLE";
    public static final String SHOPPING_LIST_ID = "Id";
    public static final String SHOPPING_LIST_TITLE = "Title";
    public static final String SHOPPING_LIST_TYPE = "Type";
    public static final String SHOPPING_LIST_DATE = "Date";
    public static final String SHOPPING_LIST_COLOR = "Color";
    public static final String SHOPPING_LIST_TAGS = "Tags";
    public static final String SHOPPING_LIST_LOCATION = "Location";
    public static final String SHOPPING_LIST_ALARMDATE = "AlarmDate";
    public static final String SHOPPING_LIST_SHAREDPEOPLE = "SharedPeople";

    private String createListTable(){
        String query = "create table if not exists " +
                SHOPPING_LIST_TABLE + "(" +
                SHOPPING_LIST_ID + " integer primary key autoincrement," +
                SHOPPING_LIST_TYPE + " integer not null, " +
                SHOPPING_LIST_DATE + " text not null, " +
                SHOPPING_LIST_TITLE + " text, " +
                SHOPPING_LIST_COLOR + " integer, " +
                SHOPPING_LIST_TAGS + " text, " +
                SHOPPING_LIST_LOCATION + " text, " +
                SHOPPING_LIST_ALARMDATE + " text, " +
                SHOPPING_LIST_SHAREDPEOPLE + " text);";

        return query;
    }

    public static final String SHOPPING_LIST_ITEM_TABLE = "SHOPPING_LIST_ITEM_TABLE";
    public static final String SHOPPING_LIST_ITEM_ID = "Id";
    public static final String SHOPPING_LIST_ITEM_PARENT_ID = "ParentId";
    public static final String SHOPPING_LIST_ITEM_VALUE = "Value";
    public static final String SHOPPING_LIST_ITEM_ISCHECKED = "IsChecked";

    private String createListItemTable(){
        String query = "create table if not exists " +
                SHOPPING_LIST_ITEM_TABLE + "(" +
                SHOPPING_LIST_ITEM_ID + " integer primary key autoincrement," +
                SHOPPING_LIST_ITEM_PARENT_ID + " integer , " +
                SHOPPING_LIST_ITEM_VALUE + " text, " +
                SHOPPING_LIST_ITEM_ISCHECKED + " integer default 0, "
                + " FOREIGN KEY(" + SHOPPING_LIST_ITEM_PARENT_ID + ") REFERENCES " + SHOPPING_LIST_TABLE + "(" + SHOPPING_LIST_ID + "));";

        return query;
    }

    /**
     * Tags Table
     */
    public static final String TAGS_TABLE = "TAGS_TABLE";
    public static final String TAGS_ID = "Id";
    public static final String TAGS_NAME = "Name";

    private String createTagsTable(){
        String query = "create table if not exists " +
                TAGS_TABLE + "(" +
                TAGS_ID + " integer primary key autoincrement," +
                TAGS_NAME + " text not null);";

        return query;
    }


    /**
     * ???????? Static
     * @param tableName
     * @return
     */
    private static String dropTable(String tableName){
        String query = "DROP TABLE IF EXISTS " + tableName;
        return query;
    }
}

package leavingstone.geolab.shoppinglist.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

import leavingstone.geolab.shoppinglist.MainActivity;
import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.database.DBHelper;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 4/9/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ShoppingListModel shoppingList = null;
        if (intent.getExtras() != null) {
            long shoppingListId = intent.getExtras().getLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY);
            try {
                DBManager.init(context);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ArrayList<ShoppingListModel> list = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_ID + " = " + shoppingListId);
            shoppingList = list.get(0);
            System.out.println(shoppingList);
            shoppingList.setAlarmDate(null);

            DBManager.updateShoppingList(shoppingList);
        }

        Notification.Builder nBuilder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alarm Notification")
                .setContentText("Reminding Your Reminder");
        nBuilder.setAutoCancel(true);

        Intent resultIntent;
        int notificationId = 1;
        if (shoppingList != null) {
            resultIntent = new Intent(context, MainActivity.class);
            resultIntent.putExtra(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingList.getId());
            notificationId = (int) shoppingList.getId();
        } else {
            resultIntent = new Intent(context, MainActivity.class);
        }
        resultIntent.setAction(context.getString(R.string.shopping_list_fragment));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, nBuilder.build());
    }
}

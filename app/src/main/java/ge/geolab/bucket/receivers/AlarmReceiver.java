package ge.geolab.bucket.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.TaskStackBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

import ge.geolab.bucket.MainActivity;
import ge.geolab.bucket.R;
import ge.geolab.bucket.activities.ShoppingListItemActivity;
import ge.geolab.bucket.database.DBHelper;
import ge.geolab.bucket.database.DBManager;
import ge.geolab.bucket.model.ShoppingListModel;
import ge.geolab.bucket.utils.GlobalConsts;

/**
 * Created by Jay on 4/9/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderTitle = "";
        String reminderText = "";
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

            reminderTitle = shoppingList.getTitle();
            reminderText = context.getResources().getString(R.string.notification_text) + shoppingList.getAlarmDate();

            shoppingList.setAlarmDate(null);
            DBManager.updateShoppingList(shoppingList);
        }


        Notification.Builder nBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.filter_time_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.shopping_list_icon))
                .setColor(Color.parseColor("#FFD700"))
                .setContentTitle(reminderTitle)
                .setContentText(reminderText)
                .setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setAutoCancel(true);

        Intent resultIntent;
        int notificationId = 1;
        if (shoppingList != null) {
            resultIntent = new Intent(context, ShoppingListItemActivity.class);
            resultIntent.putExtra(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingList.getId());
            resultIntent.putExtra(GlobalConsts.FROM_ALARM_KEY, true);
            notificationId = (int) shoppingList.getId();
        } else {
            resultIntent = new Intent(context, MainActivity.class);
        }
        resultIntent.setAction(context.getString(R.string.shopping_list_fragment));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, nBuilder.build());
    }
}

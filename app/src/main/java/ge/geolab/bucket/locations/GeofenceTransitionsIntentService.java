package ge.geolab.bucket.locations;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ge.geolab.bucket.MainActivity;
import ge.geolab.bucket.R;
import ge.geolab.bucket.activities.ShoppingListItemActivity;
import ge.geolab.bucket.database.DBHelper;
import ge.geolab.bucket.database.DBManager;
import ge.geolab.bucket.model.ShoppingListModel;
import ge.geolab.bucket.utils.GlobalConsts;

/**
 * Created by Jay on 3/28/2015.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "geofence-trans-service";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );


            long shoppingListId = -1;
            if(intent.getExtras() != null){
//                ShoppingListModel shoppingList = (ShoppingListModel) intent.getExtras().getSerializable("leavingstone.geolab.shoppinglist.model");
                shoppingListId = intent.getExtras().getLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY);
                System.out.println(shoppingListId);
            }

            // Send notification and log the transition details.
            sendNotification(shoppingListId);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(long shoppingListId) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), ShoppingListItemActivity.class);
        notificationIntent.putExtra(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingListId);
        notificationIntent.putExtra(GlobalConsts.FROM_LOCATION_KEY, true);
        notificationIntent.setAction(getString(R.string.shopping_list_fragment));
        try {
            DBManager.init(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<ShoppingListModel> list = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_ID + " = " + shoppingListId);
        ShoppingListModel shoppingList = list.get(0);

        String notificationTitle = shoppingList.getTitle();
        String notificationContent = getResources().getString(R.string.notification_text) + shoppingList.getLocationReminder().getAddress();

        shoppingList.setLocationReminder(null);
        DBManager.updateShoppingList(shoppingList);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.pin_marker_icon)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.shopping_list_icon))
                .setColor(Color.RED)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify((int) shoppingList.getId(), builder.build());

    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

}

package leavingstone.geolab.shoppinglist.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import leavingstone.geolab.shoppinglist.MainActivity;
import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.async.ListItemsUpdater;
import leavingstone.geolab.shoppinglist.custom_views.CheckBoxView;
import leavingstone.geolab.shoppinglist.database.DBHelper;
import leavingstone.geolab.shoppinglist.database.DBManager;
import leavingstone.geolab.shoppinglist.locations.GeofenceConstants;
import leavingstone.geolab.shoppinglist.locations.GeofenceErrorMessages;
import leavingstone.geolab.shoppinglist.locations.GeofenceTransitionsIntentService;
import leavingstone.geolab.shoppinglist.model.ListItemModel;
import leavingstone.geolab.shoppinglist.model.LocationModel;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;
import leavingstone.geolab.shoppinglist.receivers.AlarmReceiver;

/**
 * Created by Jay on 3/8/2015.
 */
public class ShoppingListFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, ResultCallback<Status>,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int PLACE_PICKER_REQUEST = 1;
    public static final int DIALOG_FRAGMENT = 2;
    public static final int COLOR_DIALOG_FRAGMENT = 3;

    private static final String DATEPICKER_TAG = "datepicker";
    private static final String TIMEPICKER_TAG = "timepicker";

    private ShoppingListModel shoppingList;
    private ArrayList<ListItemModel> listItems;
    private LinearLayout uncheckedContainer, checkedContainer, tagsContainer;
    private EditText titleView;
    private RelativeLayout locationPin, reminderPin;


    public static ShoppingListFragment newInstance() {
        return new ShoppingListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            long id = getArguments().getLong(ShoppingListModel.SHOPPING_LIST_MODEL_KEY);
            shoppingList = DBManager.getShoppingList(DBHelper.SHOPPING_LIST_ID + " = " + id).get(0);
            System.out.println(shoppingList);
        }

        ActionBar actionbar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(false);
//        actionbar.setDisplayShowTitleEnabled(false);
   //     actionbar.setDisplayHomeAsUpEnabled(true);

//        ((MainActivity) getActivity()).getDrawerToggle().syncState();

//        ((MainActivity) getActivity()).resetActionBar(true, 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.shopping_list_fragment_menu, menu);
        if (shoppingList.getType() == ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal()) {
            menu.findItem(R.id.showCheckBoxes).setVisible(true);
            menu.findItem(R.id.hideCheckBoxes).setVisible(false);
        } else {
            menu.findItem(R.id.showCheckBoxes).setVisible(false);
            menu.findItem(R.id.hideCheckBoxes).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(getActivity());
//                return true;
            case R.id.listColor:
                ListColorDialog colorDialog = new ListColorDialog();
                Bundle bundle = new Bundle();
                bundle.putInt("Color", shoppingList.getColor());
                colorDialog.setArguments(bundle);
                colorDialog.setTargetFragment(this, COLOR_DIALOG_FRAGMENT);
                colorDialog.show(getFragmentManager(), "ShoppingListColorDialog");
                return true;
            case R.id.deleteList:
                DBManager.deleteItem(DBHelper.SHOPPING_LIST_ITEM_TABLE, DBHelper.SHOPPING_LIST_ITEM_PARENT_ID, shoppingList.getId());
                DBManager.deleteItem(DBHelper.SHOPPING_LIST_TABLE, DBHelper.SHOPPING_LIST_ID, shoppingList.getId());

//                shoppingList.setIsDeleted(true);

                shoppingList = null;
                listItems = null;

                MainFragment mainFragment = MainFragment.newInstance();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction ft = getFragmentManager().beginTransaction()
                        .replace(R.id.container, mainFragment);
                ft.commit();
                Toast.makeText(getActivity(), "????????", Toast.LENGTH_LONG).show();

                return true;
            case R.id.showCheckBoxes:
                shoppingList.setType(ShoppingListModel.ShoppingListType.WithCheckboxes.ordinal());
                changeShoppingListType(ShoppingListModel.ShoppingListType.WithCheckboxes.ordinal());
                return true;
            case R.id.hideCheckBoxes:
                shoppingList.setType(ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal());
                if (checkedContainer.getChildCount() != 0) {
                    ShoppingListChangeTypeDialog dialog = new ShoppingListChangeTypeDialog();
                    dialog.setTargetFragment(this, DIALOG_FRAGMENT);
                    dialog.show(getFragmentManager(), "ShoppingListChangeTypeDialog");
                } else
                    changeShoppingListType(ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal());
                return true;
            case R.id.addTags:
                TagsFragment tagsFragment = TagsFragment.newInstance();
                Bundle extras = new Bundle();
                extras.putSerializable(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingList);
                tagsFragment.setArguments(extras);
                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .replace(R.id.container, tagsFragment)
                        .addToBackStack("labels");
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeShoppingListType(int type) {
        shoppingList.setType(type);
        for (int i = 0; i < uncheckedContainer.getChildCount(); i++) {
            ((CheckBoxView) uncheckedContainer.getChildAt(i)).setListType(shoppingList.getType());
        }
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shopping_list_fragment, container, false);
        changeFragmentColor(rootView, shoppingList.getColor());

        tagsContainer = (LinearLayout) rootView.findViewById(R.id.tags);
        uncheckedContainer = (LinearLayout) rootView.findViewById(R.id.unchecked_container);
        checkedContainer = (LinearLayout) rootView.findViewById(R.id.checked_container);

        locationPin = (RelativeLayout) rootView.findViewById(R.id.location_pin);
        reminderPin = (RelativeLayout) rootView.findViewById(R.id.reminder_pin);

        locationPin.findViewById(R.id.location_pin_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) locationPin.findViewById(R.id.location_pin_text)).setText("");
                locationPin.setVisibility(View.GONE);

                List<String> locationReminderRequestId = new ArrayList<String>();
                locationReminderRequestId.add("" + shoppingList.getId());

                LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, locationReminderRequestId);
                shoppingList.setLocationReminder(null);
                shoppingList.setLocationReminderJson(null);
                DBManager.updateShoppingList(shoppingList);
            }
        });

        reminderPin.findViewById(R.id.reminder_pin_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) reminderPin.findViewById(R.id.reminder_pin_text)).setText("");
                reminderPin.setVisibility(View.GONE);

                Intent intentAlarm = new Intent(getActivity(), AlarmReceiver.class);
//                intentAlarm.putExtra(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingList.getId());
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(PendingIntent.getBroadcast(getActivity(), (int) shoppingList.getId(), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

                shoppingList.setAlarmDate(null);
                DBManager.updateShoppingList(shoppingList);
            }
        });

        titleView = (EditText) rootView.findViewById(R.id.listTitle);


        loadShoppingList();

        final EditText newItemValue = (EditText) rootView.findViewById(R.id.newItem);
        Button addNewItem = (Button) rootView.findViewById(R.id.addItem);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListItemModel newItem = new ListItemModel(shoppingList.getId(), String.valueOf(newItemValue.getText()), ListItemModel.ListItemState.UnChecked.ordinal());
                long id = DBManager.insertListItem(newItem);
                newItem.setId(id);

                listItems.add(newItem);
                CheckBoxView item = new CheckBoxView(getActivity(), newItem, shoppingList.getType(), checkedContainer, uncheckedContainer);
                uncheckedContainer.addView(item);
            }
        });

        Button placeButton = (Button) rootView.findViewById(R.id.placeButton);
        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


        // Locations

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();
        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;
        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getActivity().getSharedPreferences(GeofenceConstants.SHARED_PREFERENCES_NAME,
                getActivity().MODE_PRIVATE);
        addGeofence = false;

        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();


        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);


        Button timePicker = (Button) rootView.findViewById(R.id.timeButton);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(false);
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
            }
        });

        Button friendsButton = (Button) rootView.findViewById(R.id.friendsButton);
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.setVibrate(false);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
            }
        });

        Button shareButton = (Button) rootView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpdatedData();
            }
        });
        return rootView;
    }

    private void loadShoppingList(){
        if(shoppingList.getTitle() != null)
            titleView.setText(shoppingList.getTitle());

        if(shoppingList.getAlarmDate() != null){
            ((TextView) reminderPin.findViewById(R.id.reminder_pin_text)).setText(shoppingList.getAlarmDate());
            reminderPin.setVisibility(View.VISIBLE);
        }

        if(shoppingList.getLocationReminder() != null){
            ((TextView) locationPin.findViewById(R.id.location_pin_text)).setText(shoppingList.getLocationReminder().getAddress());
            locationPin.setVisibility(View.VISIBLE);
        }

        if(shoppingList.getTags() != null && !shoppingList.getTags().isEmpty()){
            for(int i = 0; i < shoppingList.getTags().size(); i++){
                TextView tag = new TextView(getActivity());
                tag.setBackground(getResources().getDrawable(R.drawable.tag_background_white));

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                lp.setMargins(5, 0, 5, 0);
                tag.setLayoutParams(lp);

                tag.setText(shoppingList.getTags().get(i));

                tagsContainer.addView(tag);
            }
        }

        loadListItems(checkedContainer, uncheckedContainer);
    }

    private void loadListItems(LinearLayout checked, LinearLayout unchecked) {
        if (shoppingList == null)
            return;
        listItems = DBManager.getShoppingListItems(DBHelper.SHOPPING_LIST_ITEM_PARENT_ID + " = " + shoppingList.getId());
        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).isChecked() == ListItemModel.ListItemState.Checked.ordinal()) {
                CheckBoxView item = new CheckBoxView(getActivity(), listItems.get(i), shoppingList.getType(), checked, unchecked);
                checked.addView(item);
            } else {
                CheckBoxView item = new CheckBoxView(getActivity(), listItems.get(i), shoppingList.getType(), checked, unchecked);
                unchecked.addView(item);
            }
        }
    }

    private void saveUpdatedData() {
//        ArrayList<ListItemModel> shitTest = listItems;

//        ArrayList<ListItemModel> listItems = new ArrayList<>();
//        if(checkedContainer.getChildCount() > 0){
//            for (int i = 0; i < checkedContainer.getChildCount(); i++) {
//                ListItemModel listItem = ((CheckBoxView)checkedContainer.getChildAt(i)).getValue();
//                listItems.add(listItem);
//            }
//        }
//        if(uncheckedContainer.getChildCount() > 0){
//            for (int i = 0; i < uncheckedContainer.getChildCount(); i++) {
//                ListItemModel listItem = ((CheckBoxView)uncheckedContainer.getChildAt(i)).getValue();
//                listItems.add(listItem);
//            }
//        }

        if(titleView != null && shoppingList != null){
            String title = String.valueOf(titleView.getText());
            shoppingList.setTitle(title);
        }

        new ListItemsUpdater(getActivity()).execute(shoppingList, listItems);
    }

    private Calendar alarmDate;

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        alarmDate = Calendar.getInstance();
        alarmDate.set(Calendar.YEAR, year);
        alarmDate.set(Calendar.MONTH, month);
        alarmDate.set(Calendar.DAY_OF_MONTH, day);
        Toast.makeText(getActivity(), "new date:" + year + "-" + month + "-" + day + " : " + alarmDate.get(Calendar.YEAR) + "-" + alarmDate.get(Calendar.MONTH) + "-" + alarmDate.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_LONG).show();


        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        timePickerDialog.setVibrate(false);
        timePickerDialog.setCloseOnSingleTapMinute(false);
        timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        alarmDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        alarmDate.set(Calendar.MINUTE, minute);
     //   Toast.makeText(getActivity(), "new time:" + hourOfDay + "-" + minute + " : " + alarmDate.get(Calendar.HOUR_OF_DAY) + "-" + alarmDate.get(Calendar.MINUTE), Toast.LENGTH_LONG).show();

//        Toast.makeText(getActivity(), alarmDate.get(Calendar.DAY_OF_MONTH) + " " + alarmDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + alarmDate.get(Calendar.HOUR) + ":" + alarmDate.get(Calendar.MINUTE), Toast.LENGTH_LONG).show();

        String reminderPinText = new SimpleDateFormat("dd MMMM HH:mm").format(alarmDate.getTime());
        Toast.makeText(getActivity(), reminderPinText, Toast.LENGTH_LONG).show();

        reminderPin.setVisibility(View.VISIBLE);
        ((TextView)reminderPin.findViewById(R.id.reminder_pin_text)).setText(reminderPinText);

        shoppingList.setAlarmDate(reminderPinText);
        DBManager.updateShoppingList(shoppingList);

        Intent intentAlarm = new Intent(getActivity(), AlarmReceiver.class);
        intentAlarm.putExtra(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingList.getId());
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), PendingIntent.getBroadcast(getActivity(), (int) shoppingList.getId(), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, getActivity());
                    String toastMsg = String.format("Place: %s", place.getName() + " Adress: " + place.getAddress() + " Lat: " + place.getLatLng().latitude + " Long: " + place.getLatLng().longitude);
                    Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();

                    mCurrentLocation = new Location("");
                    mCurrentLocation.setLongitude(place.getLatLng().longitude);
                    mCurrentLocation.setLatitude(place.getLatLng().latitude);


//                mGeofenceList.add(new Geofence.Builder()
//                        .setRequestId("GeofenceLocation")
//                        .setCircularRegion(
//                                41.7252900,
//                                44.7634355,
//                                GeofenceConstants.GEOFENCE_RADIUS_IN_METERS
//                        )
//                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
//                        .build());

                    mGeofenceList.add(new Geofence.Builder()
                            .setRequestId("" + shoppingList.getId())
                            .setCircularRegion(
                                    mCurrentLocation.getLatitude(),
                                    mCurrentLocation.getLongitude(),
                                    GeofenceConstants.GEOFENCE_RADIUS_IN_METERS
                            )
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                            .build());

                    // addGeofencesButtonHandler();
                    addGeofence = true;

                    LocationModel locationModel = new LocationModel(place.getLatLng().longitude, place.getLatLng().latitude, String.valueOf(place.getName()));
                    shoppingList.setLocationReminder(locationModel);
                    DBManager.updateShoppingList(shoppingList);

                    ((TextView)locationPin.findViewById(R.id.location_pin_text)).setText(locationModel.getAddress());
                    locationPin.setVisibility(View.VISIBLE);

                    //   startUpdatesButtonHandler();
                }
                break;
            case DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    for (int i = 0; i < listItems.size(); i++) {
                        if (listItems.get(i).isChecked() == ListItemModel.ListItemState.Checked.ordinal()) {
                            listItems.get(i).setIsDeleted(true);
                        }
                    }
                    checkedContainer.removeAllViews();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    for (int i = 0; i < listItems.size(); i++) {
                        if (listItems.get(i).isChecked() == ListItemModel.ListItemState.Checked.ordinal()) {
                            listItems.get(i).setIsChecked(ListItemModel.ListItemState.UnChecked.ordinal());
                        }
                    }
                    for(int i = 0; i < checkedContainer.getChildCount(); i++){
                        CheckBoxView checkedItem = (CheckBoxView) checkedContainer.getChildAt(i);
                        checkedItem.setChecked(ListItemModel.ListItemState.UnChecked.ordinal());
                    }
                }
                changeShoppingListType(ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal());
                break;
            case COLOR_DIALOG_FRAGMENT:
                if(resultCode == Activity.RESULT_OK){
                    int color = data.getIntExtra("ClickedColor", 0);
                    shoppingList.setColor(color);
                    getView().setBackgroundColor(color);
//                    getView().setBackground(new ColorDrawable(color));
                    ((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
                }
                break;
            default:
                break;
        }
    }


    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    protected static final String TAG = "location-updates-sample";

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Represents a geographical location.
     */


    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);

            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            // startLocationUpdates();
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
//        if (mCurrentLocation != null) {
//            mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
//            mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
//            mLastUpdateTimeTextView.setText(mLastUpdateTime);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveUpdatedData();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Toast.makeText(getActivity(), "GoogleApiClient Connected", Toast.LENGTH_LONG).show();

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        if (addGeofence) {
            addGeofencesButtonHandler();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        Toast.makeText(getActivity(), getResources().getString(R.string.location_updated_message) + " Lat: " + mCurrentLocation.getLatitude() + " Long: " + mCurrentLocation.getLongitude(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }


    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    private boolean addGeofence;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }


    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }


    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p/>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(GeofenceConstants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.commit();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            // setButtonsEnabledState();

            Toast.makeText(
                    getActivity(),
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(getActivity(),
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(getActivity(), GeofenceTransitionsIntentService.class);
//        intent.putExtra("leavingstone.geolab.shoppinglist.model", shoppingList);
        intent.putExtra(ShoppingListModel.SHOPPING_LIST_MODEL_KEY, shoppingList.getId());
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

//    /**
//     * This sample hard codes geofence data. A real app might dynamically create geofences based on
//     * the user's location.
//     */
//    public void populateGeofenceList() {
//        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {
//
//            mGeofenceList.add(new Geofence.Builder()
//                    // Set the request ID of the geofence. This is a string to identify this
//                    // geofence.
//                    .setRequestId(entry.getKey())
//
//                            // Set the circular region of this geofence.
//                    .setCircularRegion(
//                            entry.getValue().latitude,
//                            entry.getValue().longitude,
//                            Constants.GEOFENCE_RADIUS_IN_METERS
//                    )
//
//                            // Set the expiration duration of the geofence. This geofence gets automatically
//                            // removed after this period of time.
//                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
//
//                            // Set the transition types of interest. Alerts are only generated for these
//                            // transition. We track entry and exit transitions in this sample.
//                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                            Geofence.GEOFENCE_TRANSITION_EXIT)
//
//                            // Create the geofence.
//                    .build());
//        }
//    }

    private void changeFragmentColor(View root, int color){
        ((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        root.setBackgroundColor(color);
    }
}

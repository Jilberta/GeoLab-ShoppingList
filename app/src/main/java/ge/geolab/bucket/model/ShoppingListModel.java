package ge.geolab.bucket.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jay on 4/10/2015.
 */
public class ShoppingListModel implements Serializable {
    public static final String SHOPPING_LIST_MODEL_KEY = "ShoppingList";
    public static final String LOCATION_JSON_KEY = "Location";
    public static final String LOCATION_JSON_LONGITUDE_KEY = "Longitude";
    public static final String LOCATION_JSON_LATITUDE_KEY = "Latitude";
    public static final String LOCATION_JSON_ADDRESS_KEY = "Address";
    public static final String TAGS_JSON_KEY = "Tags";
    public static final String SHAREDPEOPLE_JSON_KEY = "SharedPeople";

    public enum ShoppingListType {WithoutCheckboxes, WithCheckboxes};

    private long id;
    private String title;
    private int type;
    private String date;
    private int color = -1;
    private LocationModel locationReminder;
    private String alarmDate;
    private ArrayList<String> tags;
    private ArrayList<String> sharedPeople;

    private boolean isDeleted = false;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ShoppingListModel(int type, String date) {
        this.type = type;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public LocationModel getLocationReminder() {
        return locationReminder;
    }

    public void setLocationReminder(LocationModel locationReminder) {
        this.locationReminder = locationReminder;
    }

    public void setLocationReminderJson(String jsonString){
        if(jsonString != null){
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if(jsonObject.has(LOCATION_JSON_KEY)){
                    JSONObject locationItem = jsonObject.getJSONObject(LOCATION_JSON_KEY);
                    double longitude = locationItem.getDouble(LOCATION_JSON_LONGITUDE_KEY);
                    double latitude = locationItem.getDouble(LOCATION_JSON_LATITUDE_KEY);
                    String address = locationItem.getString(LOCATION_JSON_ADDRESS_KEY);
                    this.locationReminder = new LocationModel(longitude, latitude, address);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getLocationReminderJson() {
        JSONObject json = new JSONObject();
        JSONObject locationItem = new JSONObject();
        if(this.locationReminder == null)
            return null;
        try {
            locationItem.put(LOCATION_JSON_LONGITUDE_KEY, this.locationReminder.getLongitude());
            locationItem.put(LOCATION_JSON_LATITUDE_KEY, this.locationReminder.getLatitude());
            locationItem.put(LOCATION_JSON_ADDRESS_KEY, this.locationReminder.getAddress());
            json.put(LOCATION_JSON_KEY, locationItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public String getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(String alarmDate) {
        this.alarmDate = alarmDate;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setTagsJson(String jsonString){
        if(jsonString != null){
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if(jsonObject.has(TAGS_JSON_KEY)){
                    JSONArray jsonArray = jsonObject.getJSONArray(TAGS_JSON_KEY);
                    this.tags = new ArrayList<>();
                    for(int i = 0; i < jsonArray.length(); i++){
                        this.tags.add(jsonArray.getString(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTagsJson() {
        JSONObject json = new JSONObject();
        JSONArray tags = new JSONArray();
        if(this.tags == null)
            return null;
        try {
            for (int i = 0; i < this.tags.size(); i++) {
                tags.put(this.tags.get(i));
            }
            json.put(TAGS_JSON_KEY, tags);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public ArrayList<String> getSharedPeople() {
        return sharedPeople;
    }

    public void setSharedPeople(ArrayList<String> sharedPeople) {
        this.sharedPeople = sharedPeople;
    }

    public void setSharedPeopleJson(String jsonString){
        if(jsonString != null){
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if(jsonObject.has(SHAREDPEOPLE_JSON_KEY)){
                    JSONArray jsonArray = jsonObject.getJSONArray(SHAREDPEOPLE_JSON_KEY);
                    this.sharedPeople = new ArrayList<>();
                    for(int i = 0; i < jsonArray.length(); i++){
                        this.sharedPeople.add(jsonArray.getString(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getSharedPeopleJson(){
        JSONObject json = new JSONObject();
        JSONArray sharedPeople = new JSONArray();
        if(this.sharedPeople == null)
            return null;
        try {
            for (int i = 0; i < this.sharedPeople.size(); i++) {
                sharedPeople.put(this.sharedPeople.get(i));
            }
            json.put(SHAREDPEOPLE_JSON_KEY, sharedPeople);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

//    @Override
//    public String toString() {
//        return "ShoppingListModel{" +
//                "id=" + id +
//                ", title='" + title + '\'' +
//                ", color=" + color +
//                ", date=" + date +
//                ", locationReminder=" + locationReminder +
//                ", alarmDate=" + alarmDate +
//                ", tags=" + tags +
//                ", sharedPeople=" + sharedPeople +
//                '}';
//    }


    @Override
    public String toString() {
        return "ShoppingListModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", date='" + date + '\'' +
                ", color=" + color +
                ", locationReminder=" + locationReminder +
                ", alarmDate='" + alarmDate + '\'' +
                ", tags=" + tags +
                ", sharedPeople=" + sharedPeople +
                '}';
    }
}

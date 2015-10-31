package ge.geolab.bucket.custom_views;

import android.view.View;

import ge.geolab.bucket.model.ListItemModel;

/**
 * Created by Jay on 4/18/2015.
 */
public interface ListItemView {
    public void setValue(ListItemModel listItem);
    public ListItemModel getValue();
    public View getView();
}

package ge.geolab.bucket.model;

import java.io.Serializable;

/**
 * Created by Jay on 4/10/2015.
 */
public class ListItemModel implements Serializable {
    public enum ListItemState {UnChecked, Checked};
    private long id;
    private long parentId;
    private String value;
    private int isChecked = ListItemState.UnChecked.ordinal();
    private boolean isChanged = false, isDeleted = false;

    public ListItemModel(long parentId, String value, int isChecked) {
        this.parentId = parentId;
        this.value = value;
        this.isChecked = isChecked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int isChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setIsChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "ListItemModel{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", value='" + value + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}

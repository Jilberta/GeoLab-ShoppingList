package leavingstone.geolab.shoppinglist.custom_views;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.model.ListItemModel;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;

/**
 * Created by Jay on 4/16/2015.
 */
public class CheckBoxView extends LinearLayout implements ListItemView {
    private Context context;
    private ListItemModel listItem;
    private LinearLayout parentView;
    private CheckBox checkBox;
    private EditText editText;
    private Button removeBtn;

    private int listType = ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal();
    private LinearLayout checkedContainer, uncheckedContainer;


    public CheckBoxView(Context context, ListItemModel listItem, int listType, LinearLayout checkedContainer, LinearLayout uncheckedContainer) {
        super(context);
        this.context = context;
        this.listItem = listItem;
        this.listType = listType;
        this.checkedContainer = checkedContainer;
        this.uncheckedContainer = uncheckedContainer;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = (LinearLayout) inflater.inflate(R.layout.checkbox_custom_view, this, true);
        checkBox = (CheckBox) parentView.findViewById(R.id.checkBox);
        editText = (EditText) parentView.findViewById(R.id.text);
        removeBtn = (Button) parentView.findViewById(R.id.removeBtn);

        setValue(this.listItem);

        if(listType == ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal()){
            checkBox.setVisibility(GONE);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listItem.setIsChecked(ListItemModel.ListItemState.Checked.ordinal());
                    editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    listItem.setIsChanged(true);
                    uncheckedContainer.removeView(getView());
                    CheckBoxView copy = new CheckBoxView(context, listItem, listType, checkedContainer, uncheckedContainer);
                    checkedContainer.addView(copy);
                } else {
                    listItem.setIsChecked(ListItemModel.ListItemState.UnChecked.ordinal());
                    editText.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    listItem.setIsChanged(true);
                    checkedContainer.removeView(getView());
                    CheckBoxView copy = new CheckBoxView(context, listItem, listType, checkedContainer, uncheckedContainer);
                    uncheckedContainer.addView(copy);
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                listItem.setValue(String.valueOf(s));
                listItem.setIsChanged(true);
            }
        });

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(listType == ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal())
                    return;
                if (hasFocus) {
                    removeBtn.setVisibility(VISIBLE);
                } else {
                    removeBtn.setVisibility(INVISIBLE);
                }
            }
        });

        removeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listItem.setIsDeleted(true);
                if (listItem.isChecked() == ListItemModel.ListItemState.Checked.ordinal()) {
                    checkedContainer.removeView(getView());
                } else {
                    uncheckedContainer.removeView(getView());
                }
            }
        });
    }

    public void setValue(ListItemModel listItem) {
        // this.listItem = listItem;
        this.editText.setText(listItem.getValue());
        if (listItem.isChecked() == ListItemModel.ListItemState.Checked.ordinal()) {
            this.checkBox.setChecked(true);
            this.editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            this.checkBox.setChecked(false);
            this.editText.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    public ListItemModel getValue() {
        return this.listItem;
    }

    public View getView() {
        return this;
    }

    public void setText(String value) {
        editText.setText(value);
    }

    public int getIsChecked() {
        return checkBox.isChecked() ? 1 : 0;
    }

    public void setChecked(int isChecked) {
        boolean checked = false;
        if (isChecked == 1)
            checked = true;
        checkBox.setChecked(checked);
    }

    public void setListType(int type){
        this.listType = type;
        if(this.listType == ShoppingListModel.ShoppingListType.WithCheckboxes.ordinal()){
            checkBox.setVisibility(VISIBLE);
        }else{
            checkBox.setVisibility(GONE);
        }
    }
}

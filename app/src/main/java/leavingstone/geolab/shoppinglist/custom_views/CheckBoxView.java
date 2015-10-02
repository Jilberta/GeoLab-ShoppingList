package leavingstone.geolab.shoppinglist.custom_views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.Format;
import java.text.Normalizer;

import leavingstone.geolab.shoppinglist.R;
import leavingstone.geolab.shoppinglist.model.ListItemModel;
import leavingstone.geolab.shoppinglist.model.ShoppingListModel;
import leavingstone.geolab.shoppinglist.utils.Formater;

/**
 * Created by Jay on 4/16/2015.
 */
public class CheckBoxView extends LinearLayout implements ListItemView {
    private Context context;
    private ListItemModel listItem;
    private LinearLayout parentView, itemContainer;
    private CheckBox checkBox;
    private EditText editText;
    private TextView orderView;
//    private Button removeBtn;
    private ImageButton removeBtn;

    private int listColor;
    private int listType = ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal();
    private LinearLayout uncheckedContainer;
    private ProgressBar progressBar;
    private TextView progressBarLabel;


    public CheckBoxView(Context context, ListItemModel listItem, int listType, int listColor, LinearLayout uncheckedContainer, ProgressBar progressBar, TextView progressBarLabel) {
        super(context);
        this.context = context;
        this.listItem = listItem;
        this.listType = listType;
        this.listColor = listColor;
        this.progressBar = progressBar;
        this.progressBarLabel = progressBarLabel;
        this.uncheckedContainer = uncheckedContainer;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = (LinearLayout) inflater.inflate(R.layout.checkbox_custom_view, this, true);
        itemContainer = (LinearLayout) parentView.findViewById(R.id.itemContainer);
        checkBox = (CheckBox) parentView.findViewById(R.id.checkBox);
        editText = (EditText) parentView.findViewById(R.id.text);
        orderView = (TextView) parentView.findViewById(R.id.order_view);
//        removeBtn = (Button) parentView.findViewById(R.id.removeBtn);
        removeBtn = (ImageButton) parentView.findViewById(R.id.removeBtn);

//        removeBtn.setColorFilter(Color.RED);

//        itemContainer.setBackgroundColor();

//        orderView = new TextView(context);
//        orderView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        orderView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//        orderView.setTextColor(Color.WHITE);
//        orderView.setPadding(0, (int) getResources().getDimension(R.dimen.item_container_margin), 0, (int) getResources().getDimension(R.dimen.item_container_margin));
//
//        orderContainer.addView(orderView);

        setValue(this.listItem);
        setOrder(uncheckedContainer.getChildCount() + 1);
        setColor(listColor);

        if (listType == ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal()) {
            checkBox.setVisibility(GONE);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listItem.setIsChecked(ListItemModel.ListItemState.Checked.ordinal());
                    editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    listItem.setIsChanged(true);

                    Formater.updateProgress(progressBar.getMax(), progressBar.getProgress() + 1, progressBar, progressBarLabel);
//                    uncheckedContainer.removeView(getView());
//                    CheckBoxView copy = new CheckBoxView(context, listItem, listType, checkedContainer, uncheckedContainer);
//                    checkedContainer.addView(copy);
                } else {
                    listItem.setIsChecked(ListItemModel.ListItemState.UnChecked.ordinal());
                    editText.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    listItem.setIsChanged(true);

                    Formater.updateProgress(progressBar.getMax(), progressBar.getProgress() - 1, progressBar, progressBarLabel);
//                    checkedContainer.removeView(getView());
//                    CheckBoxView copy = new CheckBoxView(context, listItem, listType, checkedContainer, uncheckedContainer);
//                    uncheckedContainer.addView(copy);
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
                if (listType == ShoppingListModel.ShoppingListType.WithoutCheckboxes.ordinal())
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
//                if (listItem.isChecked() == ListItemModel.ListItemState.Checked.ordinal()) {
//                    checkedContainer.removeView(getView());
//                } else {
//                orderContainer.removeView(orderView);

                int progress = progressBar.getProgress();
                if(getIsChecked() == ListItemModel.ListItemState.Checked.ordinal())
                    progress--;
                Formater.updateProgress(progressBar.getMax() - 1, progress, progressBar, progressBarLabel);

                uncheckedContainer.removeView(getView());
//                }

                for (int i = 0; i < uncheckedContainer.getChildCount(); i++) {
                    CheckBoxView cbv = (CheckBoxView) uncheckedContainer.getChildAt(i);
                    cbv.setOrder(i + 1);
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

    public void setListType(int type) {
        this.listType = type;
        if (this.listType == ShoppingListModel.ShoppingListType.WithCheckboxes.ordinal()) {
            checkBox.setVisibility(VISIBLE);
        } else {
            checkBox.setVisibility(GONE);
        }
    }

    public void setOrder(int order) {
        orderView.setText(String.valueOf(order) + '.');
    }

    public void setColor(int color) {
        itemContainer.setBackgroundColor(color);
        orderView.setBackgroundColor(Formater.getDarkerColor(color));
    }
}

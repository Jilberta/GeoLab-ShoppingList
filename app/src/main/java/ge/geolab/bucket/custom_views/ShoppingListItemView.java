package ge.geolab.bucket.custom_views;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ge.geolab.bucket.R;
import ge.geolab.bucket.model.ListItemModel;
import ge.geolab.bucket.utils.Formater;

/**
 * Created by Jay on 8/23/2015.
 */
public class ShoppingListItemView extends LinearLayout implements ListItemView {
    private Context context;
    private ListItemModel listItem;
    private int order, color;
    private LinearLayout parentView;
    private TextView itemView, orderView;


    public ShoppingListItemView(Context context, ListItemModel listItem, int order, int color) {
        super(context);
        this.context = context;
        this.listItem = listItem;
        this.order = order;
        this.color = color;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = (LinearLayout) inflater.inflate(R.layout.shopping_list_item_custom_view, this, true);
        orderView = (TextView) parentView.findViewById(R.id.order_view);
        itemView = (TextView) parentView.findViewById(R.id.item_view);

        setValue(listItem);
        orderView.setText(order + ".");

        int darkerColor = Formater.getDarkerColor(color);

        itemView.setBackgroundColor(color);
        orderView.setBackgroundColor(darkerColor);
    }

    @Override
    public void setValue(ListItemModel listItem) {
        this.itemView.setText(listItem.getValue());
        if (listItem.isChecked() == ListItemModel.ListItemState.Checked.ordinal()) {
            this.itemView.setPaintFlags(itemView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            this.itemView.setPaintFlags(itemView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public ListItemModel getValue() {
        return this.listItem;
    }

    @Override
    public View getView() {
        return this;
    }
}

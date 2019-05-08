package ir.drax.kenar_menu.interfaces;

import android.view.View;

import ir.drax.kenar_menu.ReserveItem;

public interface SliderPlusInteraction {
    public void onRequestListByPage(int pageNo);
    public boolean onListItemClicked(ReserveItem reserveItem, int position);
    public View listItemLayout(ReserveItem item, int position, View listItemLayout);

}
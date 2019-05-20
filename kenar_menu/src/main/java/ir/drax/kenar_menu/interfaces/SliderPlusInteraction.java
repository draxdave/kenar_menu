package ir.drax.kenar_menu.interfaces;

import android.view.View;

public interface SliderPlusInteraction {
    public void onRequestListByPage(int pageNo);
    public boolean onListItemClicked(Object sampleItem, int position);
    public View listItemLayout(Object item, int position, View listItemLayout);
    public void onFilterChanged(boolean enabled);

}
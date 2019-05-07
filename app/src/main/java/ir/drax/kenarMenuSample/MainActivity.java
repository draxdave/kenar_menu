package ir.drax.kenarMenuSample;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import ir.drax.kenar_menu.SliderPlus;
import ir.drax.kenar_menu.interfaces.SliderPlusInteraction;
import ir.drax.kenar_menu.models.ReserveItem;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private SliderPlus sliderPlus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.dl);
        sliderPlus = findViewById(R.id.SlideMenu);


        sliderPlus.setPlusInteractions(new SliderPlusInteraction() {
            @Override
            public void onRequestListByPage(int pageNo) {

            }

            @Override
            public boolean onListItemClicked(ReserveItem reserveItem, int position) {
                return false;
            }

            @Override
            public View listItemLayout(ReserveItem item, int position, View listItemLayout) {
                return null;
            }
        });
    }


    public void openStartDrawer(View view) {
        drawerLayout.openDrawer(Gravity.START);

    }

    public void openEndDrawer(View view) {
        sliderPlus.open();
    }
}

package ir.drax.kenarMenuSample;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ir.drax.kenar_menu.KenarMenu;
import ir.drax.kenar_menu.interfaces.SliderPlusInteraction;
import ir.drax.kenar_menu.interfaces.Swipe;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private KenarMenu kenarMenu;
    private View innerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
          Changing layout direction and locale to try rtl and ltr layouts
         */


       /* String languageToLoad  = "fa"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/


        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.dl);
        kenarMenu = findViewById(R.id.SlideMenu);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        innerLayout = inflater.inflate(R.layout.kenar_menu_item_detail, null, false);

        kenarMenu
                .setInnerRoomLayout(innerLayout)
                .setTitle(R.string.my_drawer_title)
                .setFilterTitle(R.string.my_drawer_filter)
                .setListItemLayoutId(R.layout.kenar_menu_item)
                .setPlusInteractions(new SliderPlusInteraction() {
                    @Override
                    public void onRequestListByPage(int pageNo) {
                        /*
                         * A Fake RestApi call to get list items
                         * */

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                kenarMenu.fillListByPage(FakeGenerator.getFakeItems(20),1);
                            }
                        },500);
                    }

                    @Override
                    public boolean onListItemClicked( Object obj, final int position) {
                        final SampleObject item = (SampleObject) obj;
                        switch (position){
                            case 0:
                                return false;// This item will not go to DETAILS page

                            case 1:
                                Toast.makeText(MainActivity.this, item.getTitle() + " Clicked!", Toast.LENGTH_SHORT).show();
                                return false;// This item will not go to DETAILS page too

                        }

                        setInnerLayoutData(item);
                        kenarMenu.roomDataLoaded();

                        return true;
                    }

                    @Override
                    public View listItemLayout(Object obj, int position, View listItemLayout) {
                        final SampleObject item = (SampleObject) obj;

                        TextView title =listItemLayout.findViewById(R.id.title);
                        ImageView icon =listItemLayout.findViewById(R.id.icon);
                        title.setText(item.getTitle());
                        icon.setImageResource(item.getIcon());

                        return listItemLayout;
                    }

                    @Override
                    public void onFilterChanged(boolean enabled) {
                        if (enabled)
                            kenarMenu.fillListByPage(FakeGenerator.getFakeItems(4),1);
                        else
                            kenarMenu.fillListByPage(FakeGenerator.getFakeItems(20),1);
                    }
                })
                .setSwipe(new Swipe() {
                    @Override
                    public boolean onListItemSwiped(Object sampleItem, int position) {
                        return true;
                    }
                })
                .setHeaderTextColor(getResources().getColor(R.color.white));
    }

    private void setInnerLayoutData(SampleObject item){
        TextView title= innerLayout.findViewById(R.id.title);
        TextView desc= innerLayout.findViewById(R.id.desc);
        ImageView icon= innerLayout.findViewById(R.id.icon);

        title.setText(item.getTitle());
        desc.setText(item.getDesc());
        icon.setImageResource(item.getIcon());
    }


    public void openStartDrawer(View view) {
        drawerLayout.openDrawer(Gravity.START);

    }

    public void openEndDrawer(View view) {
        kenarMenu.open();
    }

    @Override
    public void onBackPressed() {

        if (kenarMenu.isOpen())
            kenarMenu.close();

        else
            super.onBackPressed();
    }
}

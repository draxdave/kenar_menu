package ir.drax.kenarMenuSample;

import java.util.ArrayList;
import java.util.List;

import ir.drax.kenar_menu.ReserveItem;

public class FakeGenerator  {

    private static String[] titles = {"Curabitur eget augue consectetur", "Efficitur elit facilisis","Sagittis risus","Aliquam rhoncus massa","At lacus pretium","Quis semper ex pellentesque"};
    private static int[] icons = {R.drawable.ic_arrow_back_black_24dp,R.drawable.ic_arrow_downward_black_24dp,R.drawable.ic_beach_access_black_24dp,R.drawable.ic_refresh,R.drawable.ic_settings_black_24dp,R.drawable.ic_refresh_black_24dp};
    private static String[] descs = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            ,"Curabitur eget augue consectetur, efficitur elit facilisis, sagittis risus.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit. odio viverra lacinia.Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            ,"Sed vel ipsum sed orci posuere consectetur ac vitae ipsum.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit. odio viverra lacinia.Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            ,"Aliquam rhoncus massa at lacus pretium, quis semper ex pellentesque.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit. odio viverra lacinia.Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            ,"Proin quis lectus in odio viverra lacinia.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit. odio viverra lacinia.Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            ,"Sed tempor metus vitae dictum consequatLorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit..Lorem ipsum dolor sit amet, consectetur adipiscing elit. odio viverra lacinia.Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            ,"Praesent sit amet turpis ac tortor vestibulum luctus vitae ut est.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit. odio viverra lacinia.Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            ,"Nullam venenatis orci id vehicula mattisLorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit. odio viverra lacinia.Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    };


    public static List<ReserveItem> getFakeItems(int count){
        List<ReserveItem> list = new ArrayList<>();
        for (int i = 0 ; count > i   ; i++){
            list.add(new ReserveItem(i,icons[(int) (Math.random() * icons.length)],titles[(int) (Math.random() * titles.length)] ,descs[(int) (Math.random() * descs.length)]  ));
        }

        return list;
    }
}

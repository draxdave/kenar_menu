package ir.drax.kenar_menu;

public class ReserveItem {
    private int id;
    private int icon;
    private String title;
    private String desc;

    public ReserveItem(int id, int icon, String title, String desc) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }
}

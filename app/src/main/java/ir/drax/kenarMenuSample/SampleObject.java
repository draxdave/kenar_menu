package ir.drax.kenarMenuSample;

public class SampleObject {
    private int id;
    private int icon;
    private String title;
    private String desc;

    public SampleObject(int id, int icon, String title, String desc) {
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

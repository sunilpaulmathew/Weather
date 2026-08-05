package in.sunilpaulmathew.weatherwidget.serializable;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 05, 2026
 */
public class MenuItems implements Serializable {

    private final int drawable, id, title;

    public MenuItems(int title, int drawable, int id) {
        this.title = title;
        this.drawable = drawable;
        this.id = id;
    }

    public int getDrawable() {
        return this.drawable;
    }

    public int getID() {
        return this.id;
    }

    public int getTitle() {
        return this.title;
    }

}
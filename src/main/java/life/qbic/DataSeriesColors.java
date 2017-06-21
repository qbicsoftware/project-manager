package life.qbic;

import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sven1103 on 18/01/17.
 */
public class DataSeriesColors {

    public final static Color SHADE0 = new SolidColor(155, 155, 155);
    public final static Color SHADE1 = new SolidColor(100, 100, 100);
    public final static Color SHADE2 = new SolidColor(199, 198, 198);
    public final static Color SHADE3 = new SolidColor(100, 100, 100);

    public final static Color QBICBLUE = new SolidColor(17, 103, 189);

    public final static List<Color> SHADELIST = new LinkedList<>();

    static {
        SHADELIST.add(SHADE0);
        SHADELIST.add(SHADE1);
        SHADELIST.add(SHADE2);
        SHADELIST.add(SHADE3);
    }
}

package life.qbic;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by sven1103 on 25/01/17.
 */
public class NumberIndicator extends VerticalLayout {

    private Label caption;

    private Label number;

    public NumberIndicator() {
        this.caption = new Label();
        this.number = new Label();
        init();
    }

    private void init() {
        this.caption.setStyleName("header");
        this.number.setStyleName("number");
        this.addComponent(caption);
        this.addComponent(number);
        this.setComponentAlignment(caption, Alignment.MIDDLE_CENTER);
        this.setComponentAlignment(number, Alignment.MIDDLE_CENTER);
        this.setSizeFull();
    }

    public void setHeader(String caption) {
        this.caption.setValue(caption);
    }

    public Label getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number.setValue(number.toString());
    }


}

package GUI;

import LeapInterface.ScreenGadget;

import javax.swing.*;
import java.awt.*;


public class ScreenGadgetListItem extends JLabel implements ListCellRenderer {

    private ScreenGadget gadget;

    public ScreenGadgetListItem(ScreenGadget gadget) {
        this.gadget = gadget;
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {


        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setText(String.format("%s: %s", gadget.getGadgetName(), gadget.getGadgetCommand().getTitle()));

        return this;
    }
}

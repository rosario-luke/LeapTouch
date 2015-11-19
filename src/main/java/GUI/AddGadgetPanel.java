package GUI;

import LeapInterface.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class AddGadgetPanel extends JPanel {

    private final String NAME_FIELD_STRING = "Name Field";
    private final String NONE_STRING = "<None>";

    private LeapTouchController myController;
    private GadgetPanel gadgetPanel;
    private JButton clearTopLeftBtn;
    private JButton clearBottomRightBtn;
    private JButton okayButton;
    private JComboBox<String> commandComboBox;
    private JTextField gadgetNameField;
    private JButton addGadgetBtn;
    private JButton cancelBtn;
    private GUIGadgetSetup gadgetSetup;


    public AddGadgetPanel(LeapTouchController controller){
        myController = controller;
        setLayout(new GridBagLayout());
        setupPanel();
    }


    public void paint(Graphics g){
        paintComponents(g);
        repaint();
    }


    private void setupPanel(){
        GridBagConstraints constraints = new GridBagConstraints();

        addGadgetBtn = new JButton("Add Gadget");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        add(addGadgetBtn, constraints);

        cancelBtn = new JButton("Cancel");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        add(cancelBtn, constraints);

        gadgetPanel = new GadgetPanel(myController.getPointListener());
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipady = 40;      //make this component tall
        constraints.weightx = 0.0;
        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weighty = 1;
        add(gadgetPanel, constraints);

        clearTopLeftBtn = new JButton("Clear Top Left");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        add(clearTopLeftBtn, constraints);


        clearBottomRightBtn = new JButton("Clear Bottom Right");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        add(clearBottomRightBtn, constraints);


        okayButton = new JButton("Accept");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        add(okayButton, constraints);


        gadgetNameField = new JTextField();
        gadgetNameField.setText(NAME_FIELD_STRING);
        gadgetNameField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                gadgetNameField.setText("");
            }

            public void focusLost(FocusEvent e) {

            }
        });
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(gadgetNameField, constraints);

        commandComboBox = new JComboBox<String>();
        String[] myCommandStrings = myController.getCommandParser().getAllCommandNames();
        commandComboBox.addItem(NONE_STRING);
        for (int i = 0; i < myCommandStrings.length; i++){
            commandComboBox.addItem(myCommandStrings[i]);
        }
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(commandComboBox, constraints);

        setupButtonEvents();
    }


    private void setupButtonEvents(){
        addGadgetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addGadget();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelGadget();
            }
        });

        clearTopLeftBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (gadgetSetup != null){
                    gadgetSetup.clearTopLeft();
                }
            }
        });

        clearBottomRightBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (gadgetSetup != null){
                    gadgetSetup.clearBottomRight();
                }
            }
        });

        okayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNewGadget();
            }
        });
    }


    private void addGadget(){
        gadgetSetup = new GUIGadgetSetup(myController.getController());
        gadgetPanel.setGadgetSetup(gadgetSetup);
    }


    private void cancelGadget(){
        if (gadgetSetup != null) {
            gadgetSetup.cleanUp();
            gadgetSetup = null;
        }
        gadgetPanel.clearGadgetSetup();
    }


    private void addNewGadget(){
        if (gadgetSetup == null){
            JOptionPane.showMessageDialog(this,
                    "Please click \' Add Gadget \' to setup points",
                    "Setup Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!gadgetSetup.isFinished()){
            JOptionPane.showMessageDialog(this,
                    "Please finish setup before clicking okay",
                    "Setup Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (gadgetNameField.getText().isEmpty() || gadgetNameField.getText().equalsIgnoreCase(NAME_FIELD_STRING)){
            JOptionPane.showMessageDialog(this,
                    "Please Write A Name",
                    "Name Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (((String)commandComboBox.getSelectedItem()).equalsIgnoreCase(NONE_STRING)){
            JOptionPane.showMessageDialog(this,
                    "Please select a Command",
                    "Name Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Command selectedCommand = myController.getCommandParser().
                getCommandByTitle((String)commandComboBox.getSelectedItem());

        ScreenGadget screenGadget = new ScreenGadget(gadgetSetup.getRawTopLeft(),
                gadgetSetup.getRawBottomRight(), gadgetNameField.getText(), selectedCommand);

        if (!myController.addScreenGadget(screenGadget)){
            JOptionPane.showMessageDialog(this,
                    "Screen Gadget could not be added due to intersecting",
                    "Intersection Error",
                    JOptionPane.ERROR_MESSAGE);
            cancelGadget();
            return;
        } else {
            JOptionPane.showMessageDialog(this,
                    "Screen Gadget added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            cancelGadget();
            gadgetNameField.setText(NAME_FIELD_STRING);
            commandComboBox.setSelectedIndex(0);
        }
    }

}


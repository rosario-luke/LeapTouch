package GUI;

import LeapInterface.Command;
import LeapInterface.LeapTouchController;
import LeapInterface.LeapTrackPad;
import LeapInterface.ScreenGadget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class EditGadgetPanel extends JPanel {

    private final String NAME_STRING = "Name:";
    private final String DEFAULT_STRING = "<None Selected>";
    private final String NONE_STRING = "<None>";

    private LeapTouchController myController;
    private JList<String> myGadgetList;
    private JLabel currentGadgetLabel;
    private JComboBox<String> currentCommandComboBox;
    private JButton acceptChangesBtn;
    private JButton removeGadgetBtn;
    private DefaultListModel listModel;
    private ScreenGadget currentGadget;

    public EditGadgetPanel(LeapTouchController controller){
        myController = controller;
        currentGadget = null;
        setLayout(new GridBagLayout());
        setupPanel();
    }


    public void paint(Graphics g){
        if (myController.getPointListener().gadgetsChanged()){
            setupGadgetList();
        }
        acceptChangesBtn.setEnabled(areUnsavedChanges());
        removeGadgetBtn.setEnabled(currentGadget != null);
        paintComponents(g);
        repaint();
    }


    public void setupPanel(){
        GridBagConstraints constraints = new GridBagConstraints();

        myGadgetList = new JList();
        myGadgetList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    ScreenGadget selectedGadget = (ScreenGadget) listModel.getElementAt(myGadgetList.getSelectedIndex());
                    if (selectedGadget != null) {
                        loadScreenGadget(selectedGadget);
                    }
                }
            }
        });

        setupGadgetList();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(myGadgetList, constraints);

        JLabel nameStringLabel = new JLabel(NAME_STRING);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weighty = 0;
        add(nameStringLabel, constraints);

        currentGadgetLabel = new JLabel(DEFAULT_STRING);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0.5;
        add(currentGadgetLabel, constraints);

        currentCommandComboBox = new JComboBox<String>();
        setupGadgetComboBox();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.weightx = 0;
        add(currentCommandComboBox, constraints);

        acceptChangesBtn = new JButton("Accept Changes");
        acceptChangesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.5;
        add(acceptChangesBtn, constraints);

        removeGadgetBtn = new JButton("Remove Gadget");
        removeGadgetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeCurrentGadget();
            }
        });
        constraints.gridx = 2;
        constraints.gridy = 2;
        add(removeGadgetBtn, constraints);

    }


    private void setupGadgetList(){
        listModel = new DefaultListModel();
        for (ScreenGadget curGadget : myController.getPointListener().getScreenGadgets()){
            listModel.addElement(curGadget);
        }
        myGadgetList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        myGadgetList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        myGadgetList.setModel(listModel);
    }


    private void setupGadgetComboBox(){
        String[] myCommandStrings = myController.getCommandParser().getAllCommandNames();
        currentCommandComboBox.addItem(NONE_STRING);
        for (int i = 0; i < myCommandStrings.length; i++){
            currentCommandComboBox.addItem(myCommandStrings[i]);
        }
    }


    private void loadScreenGadget(ScreenGadget toLoad){
        if (areUnsavedChanges()){
            String question = String.format("There are unsaved changes to %s, would you like to continue?",
                    currentGadget.getGadgetName());
            int dialogResult = JOptionPane.showConfirmDialog (null,
                    question, "Warning", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.NO_OPTION){
                return;
            }
        }
        currentGadget = toLoad;
        if (currentGadget instanceof LeapTrackPad){
            currentCommandComboBox.setEnabled(false);
        } else {
            currentCommandComboBox.setEnabled(true);
        }
        currentGadgetLabel.setText(currentGadget.getGadgetName());
        currentCommandComboBox.setSelectedItem(currentGadget.getGadgetCommand().getTitle());
    }


    private void saveChanges(){
        if (currentGadget == null){
            return;
        }

        Command newCommand = myController.getCommandParser().
                getCommandByTitle((String) currentCommandComboBox.getSelectedItem());
        currentGadget.setGadgetCommand(newCommand);
        setupGadgetList();
    }


    private boolean areUnsavedChanges(){
        if (currentGadget == null){
            return false;
        }

        if (!currentGadget.getGadgetCommand().getTitle().equals(currentCommandComboBox.getSelectedItem())){
            return true;
        }

        return false;
    }


    private void removeCurrentGadget(){
        if (currentGadget == null){
            return;
        }

        myController.getPointListener().removeGadget(currentGadget);
        currentGadget = null;
        currentGadgetLabel.setText(DEFAULT_STRING);
        currentCommandComboBox.setSelectedIndex(0);
    }
}

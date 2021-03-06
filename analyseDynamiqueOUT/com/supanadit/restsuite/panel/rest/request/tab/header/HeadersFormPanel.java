package com.supanadit.restsuite.panel.rest.request.tab.header;
import com.supanadit.restsuite.entity.CollectionHeaderEntity;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
public class HeadersFormPanel extends JScrollPane {
    public ArrayList<HeadersFormInputPanel> listInputPanel = new ArrayList<>();

    public ArrayList<HeadersFormInputPanel> listRemovedInputPanel = new ArrayList<>();

    public JPanel formGroupPanel;

    public JButton addField;

    public HeadersFormPanel() {
        formGroupPanel = new JPanel(new MigLayout("", "", "[]0[]"));
        addField = new JButton("Add Field");
        addField.addActionListener(( k) -> {
            addFormInput(new HeadersFormInputPanel(this));
        });
        formGroupPanel.add(addField, "pushx,growx,wrap");
        setViewportView(formGroupPanel);
    }

    public JPanel getPanel() {
        return formGroupPanel;
    }

    public void addFormInput(HeadersFormInputPanel headersFormInputPanel) {
        analyse.Analyse.printAnalysis("HeadersFormPanel","HeadersFormPanel");
        // remove button add field
        formGroupPanel.remove(addField);
        // add form input
        formGroupPanel.add(headersFormInputPanel, "pushx,growx,wrap");
        // add back the button add field
        formGroupPanel.add(addField, "pushx,growx,wrap");
        // add to list input
        listInputPanel.add(headersFormInputPanel);
        // refresh ui
        updateChange();
    }

    public void addFormInput(CollectionHeaderEntity collectionHeaderEntity) {
        analyse.Analyse.printAnalysis("HeadersFormPanel","HeadersFormPanel");
        analyse.Analyse.printAnalysis("CollectionHeaderEntity","HeadersFormPanel");
        analyse.Analyse.printAnalysis("HeadersFormInputPanel","HeadersFormPanel");
        analyse.Analyse.printAnalysis("CollectionHeaderEntity","HeadersFormPanel");
        analyse.Analyse.printAnalysis("CollectionHeaderEntity","HeadersFormPanel");
        // Get Key
        String key = collectionHeaderEntity.getKey();
        // Get Value
        String value = collectionHeaderEntity.getValue();
        // Declare headers form input panel
        HeadersFormInputPanel headersFormInputPanel = new HeadersFormInputPanel(this, key, value);
        // Set ID
        headersFormInputPanel.setId(collectionHeaderEntity.getId());
        // Add Form Input
        addFormInput(headersFormInputPanel);
    }

    public void clearFormInput() {
        analyse.Analyse.printAnalysis("HeadersFormPanel","HeadersFormPanel");
        analyse.Analyse.printAnalysis("HeadersFormInputPanel","HeadersFormPanel");
        // Clone
        ArrayList<HeadersFormInputPanel> listInputPanelClone = listInputPanel;
        // Clear original variable
        listInputPanel = new ArrayList<>();
        // Clear removed input list
        listRemovedInputPanel = new ArrayList<>();
        // Looping
        for (HeadersFormInputPanel formInputPanel : listInputPanelClone) {
            formInputPanel.remove();
        }
        updateChange();
    }

    public void updateChange() {
        formGroupPanel.updateUI();
    }
}
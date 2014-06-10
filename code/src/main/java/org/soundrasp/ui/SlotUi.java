package org.soundrasp.ui;

import net.miginfocom.swing.MigLayout;
import org.flowutils.ui.UiComponentBase;
import org.soundrasp.model.Slot;

import javax.swing.*;

/**
 */
public class SlotUi extends UiComponentBase implements Slot.SlotListener {

    private final Slot slot;

    private JPanel modulePanel;
    private JPanel panel;
    private JLabel slotTitle;

    public SlotUi(Slot slot) {
        this.slot = slot;

        slot.addListener(this);
    }

    @Override
    protected JComponent createUi() {
        panel = new JPanel(new MigLayout("wrap 1"));

        slotTitle = new JLabel();
        panel.add(slotTitle);

        modulePanel = new JPanel(new MigLayout());
        panel.add(modulePanel);

        onModuleChanged(slot);

        return panel;
    }

    @Override
    public void onModuleChanged(Slot slot) {
        if (modulePanel != null) {
            modulePanel.removeAll();
            modulePanel.add(new ModuleUi(slot.getModule()).getUi());

            slotTitle.setText(slot.getName());
        }
    }
}

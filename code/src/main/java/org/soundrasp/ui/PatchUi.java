package org.soundrasp.ui;

import net.miginfocom.swing.MigLayout;
import org.flowutils.ui.UiComponentBase;
import org.soundrasp.model.Patch;
import org.soundrasp.model.Slot;

import javax.swing.*;

/**
 */
public class PatchUi extends UiComponentBase {

    private Patch patch;
    private JPanel panel;

    public PatchUi(Patch patch) {
        setPatch(patch);
    }

    public Patch getPatch() {
        return patch;
    }

    public void setPatch(Patch patch) {
        this.patch = patch;

        updateUi();
    }

    @Override
    protected JComponent createUi() {
        panel = new JPanel();

        updateUi();

        return panel;
    }

    private void updateUi() {
        if (panel != null) {
            panel.removeAll();

            if (patch != null) {
                for (Slot slot : patch.getSlots()) {
                    panel.add(new SlotUi(slot).getUi());
                }
            }
        }
    }
}

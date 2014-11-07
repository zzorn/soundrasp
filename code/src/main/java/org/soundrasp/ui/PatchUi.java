package org.soundrasp.ui;

import net.miginfocom.swing.MigLayout;
import org.flowutils.ui.UiComponentBase;
import org.soundrasp.model.Module;
import org.soundrasp.modules.PatchModule;

import javax.swing.*;

/**
 */
public class PatchUi extends UiComponentBase {

    private PatchModule patch;
    private JPanel panel;

    public PatchUi(PatchModule patch) {
        setPatch(patch);
    }

    public PatchModule getPatch() {
        return patch;
    }

    public void setPatch(PatchModule patch) {
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
                for (Module module: patch.getModules()) {
                    panel.add(new ModuleUi(module).getUi());
                }
            }
        }
    }
}

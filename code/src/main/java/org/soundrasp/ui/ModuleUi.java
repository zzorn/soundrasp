package org.soundrasp.ui;

import net.miginfocom.swing.MigLayout;
import org.flowutils.ui.UiComponentBase;
import org.soundrasp.model.Module;
import org.soundrasp.model.ModuleListener;
import org.soundrasp.model.Param;

import javax.swing.*;

/**
 */
public class ModuleUi extends UiComponentBase implements ModuleListener {

    private Module module;

    private JPanel panel;
    private JPanel paramPanel;
    private JLabel title;
    private JLabel valueLabel;

    public ModuleUi(Module module) {
        setModule(module);
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        if (this.module != module) {

            if (this.module != null) this.module.removeListener(this);

            this.module = module;

            if (this.module != null) this.module.addListener(this);

            updateUi();
        }
    }

    @Override
    protected JComponent createUi() {
        panel = new JPanel(new MigLayout("wrap 1"));
        title = new JLabel();
        panel.add(title);

        valueLabel = new JLabel();
        panel.add(valueLabel);

        paramPanel = new JPanel(new MigLayout("wrap 1"));
        panel.add(paramPanel);

        updateUi();

        return panel;
    }

    private void updateUi() {
        if (panel != null) {
            paramPanel.removeAll();

            if (module != null) {
                title.setText(module.getName());
                title.setToolTipText(module.getDescription());

                for (Param param : module.getParameters()) {
                    paramPanel.add(new ParamUi(param).getUi());
                }
            }
            else {
                title.setText("");
                title.setToolTipText("");
            }
        }
    }

    @Override
    public void onValueChange(Module module, long sampleCounter, double value) {
        valueLabel.setText(String.format("%.2f", value));
    }

    @Override public void onConfigurationChanged(Module module) {
        // TODO: Implement

    }

    @Override public void onParameterChanged(Module module, Param changedParameter) {
        // TODO: Implement

    }
}

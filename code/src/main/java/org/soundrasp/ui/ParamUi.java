package org.soundrasp.ui;

import net.miginfocom.swing.MigLayout;
import org.flowutils.Check;
import org.flowutils.MathUtils;
import org.flowutils.ui.UiComponentBase;
import org.soundrasp.model.Param;
import org.soundrasp.model.Source;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 */
public class ParamUi extends UiComponentBase implements Param.ParamListener {

    private static final int SLIDER_SCALE = 1000;
    private final Param param;
    private JPanel panel;
    private JLabel valueLabel;
    private JLabel sourceLabel;
    private JSlider slider;

    private boolean updateUiFromParam = true;
    private boolean updateParamFromUi = true;

    public ParamUi(Param param) {
        Check.notNull(param, "param");

        this.param = param;

        param.setListener(this);

    }

    @Override
    protected JComponent createUi() {
        panel = new JPanel(new MigLayout("wrap 4"));

        // Title
        final JLabel title = new JLabel(param.getName());
        title.setToolTipText(param.getDescription());
        panel.add(title);

        // Value
        valueLabel = new JLabel();
        panel.add(valueLabel);

        // Value adjust slider
        slider = new JSlider(-SLIDER_SCALE, SLIDER_SCALE);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (updateParamFromUi) {
                    updateUiFromParam = false;
                    final double sliderPos = (double) slider.getValue() / SLIDER_SCALE;
                    param.set(MathUtils.map(sliderPos, -1, 1, param.getMinValue(), param.getMaxValue()));
                    updateUiFromParam = true;
                }
            }
        });
        slider.setEnabled(param.getSource() == null);
        panel.add(slider);

        // Source
        sourceLabel = new JLabel();
        panel.add(sourceLabel);

        showValue(param.get());
        showSource(param.getSource());

        return panel;
    }



    @Override
    public void onValueChange(Param param, double value) {
        showValue(value);
    }

    @Override
    public void onSourceChange(Param param, Source oldSource, Source newSource) {
        showSource(newSource);

        if (slider != null) slider.setEnabled(newSource == null);
    }

    private void showSource(Source source) {
        if (source != null) {
            sourceLabel.setText(source.getName());
        }
        else {
            sourceLabel.setText("");
        }
    }

    private void showValue(double value) {
        valueLabel.setText(String.format("%.2f", value));

        if (updateUiFromParam) {
            updateParamFromUi = false;
            slider.setValue((int) MathUtils.mapAndClamp(value, param.getMinValue(), param.getMaxValue(), -SLIDER_SCALE, SLIDER_SCALE));
            updateParamFromUi = true;
        }
    }
}

package org.soundrasp.modules;

import org.flowutils.SimpleFrame;
import org.flowutils.SimplexGradientNoise;
import org.soundrasp.model.ModuleBase;
import org.soundrasp.model.Parameter;

import javax.swing.*;
import java.util.List;

/**
 *
 */
public class SineModule extends ModuleBase {

    private static final double Tau = Math.PI * 2;

    private  double frequency;
    private final double amplitude;
    private final double offset;

    private double phase;
    private JSlider frequencySlider;

    public SineModule() {
        this(440);
    }

    public SineModule(double frequency) {
        this(frequency, 1);
    }

    public SineModule(double frequency, double amplitude) {
        this(frequency, amplitude, 0);
    }

    public SineModule(double frequency, double amplitude, double offset) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.offset = offset;

        frequencySlider = new JSlider(100, 800, 300);
        SimpleFrame simpleFrame = new SimpleFrame("test", frequencySlider);
    }


    @Override
    protected double calculateValue(double durationSeconds, long sampleCounter) {
/*
        // Testing...
        //frequency = (Math.sin(sampleCounter / 100000.0) * 0.5 + 0.5 ) * 200 + 50;
        frequency = SimplexGradientNoise.sdnoise1(sampleCounter / 2390070.0) * 100 + 50;
        frequency = SimplexGradientNoise.sdnoise1(sampleCounter / 239008970.0) * 100 + 100;
        frequency += SimplexGradientNoise.sdnoise1(sampleCounter / 123770.0) * 50 + 50;
        frequency += SimplexGradientNoise.sdnoise1(sampleCounter / 32891.0) * 10 + 10;
//        frequency += SimplexGradientNoise.sdnoise1(sampleCounter / 7871.0) * 3 + 3;
*/
        frequency = frequencySlider.getValue();

        // Phase in the wave
        phase += durationSeconds * frequency;

        // Sine wave
        double value = amplitude * Math.sin((offset + phase) * Tau);

        // Bring phase down if it gets too large
        if (phase > 10000) phase -= 10000;
        if (phase > 1000) phase -= 1000;
        if (phase > 100) phase -= 100;
        if (phase > 10) phase -= 10;

        return value;
    }
}

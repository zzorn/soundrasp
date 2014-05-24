package org.soundrasp.modules;

import org.flowutils.SimpleFrame;
import org.soundrasp.model.ModuleBase;
import org.soundrasp.model.Parameter;

import javax.swing.*;

/**
 *
 */
public class SineModule extends ModuleBase {

    private static final double Tau = Math.PI * 2;

    public final Parameter frequency;
    public final Parameter amplitude;
    public final Parameter phase;
    public final Parameter offset;

    private double currentPhase;
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
        this(frequency, amplitude, offset, 0);
    }

    public SineModule(double frequency, double amplitude, double offset, double phase) {

        // Test
        frequencySlider = new JSlider(100, 800, 300);
        SimpleFrame simpleFrame = new SimpleFrame("test", frequencySlider);


        this.frequency = parameter("Frequency", frequency);
        this.amplitude = parameter("Amplitude", amplitude);
        this.offset = parameter("Offset", offset);
        this.phase = parameter("Phase", phase);
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
        //frequency.set(frequencySlider.getValue());

        // Phase in the wave
        currentPhase += durationSeconds * frequency.get();

        // Sine wave
        double value = amplitude.get() * Math.sin((phase.get() + currentPhase) * Tau) + offset.get();

        // Bring phase down if it gets too large
        if (currentPhase > 100000) currentPhase -= 100000;
        if (currentPhase > 1000) currentPhase -= 1000;
        if (currentPhase > 100) currentPhase -= 100;
        if (currentPhase > 10) currentPhase -= 10;

        return value;
    }
}

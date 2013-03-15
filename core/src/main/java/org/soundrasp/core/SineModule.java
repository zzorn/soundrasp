package org.soundrasp.core;

/**
 *
 */
public class SineModule extends ModuleBase {

    private static final double Tau = Math.PI * 2;

    private final double frequency;
    private final double amplitude;
    private final double offset;

    private double phase;

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
    }

    @Override
    protected double calculateSampleValue(double durationSeconds, long sampleCounter) {
        // Phase in the wave
        phase += durationSeconds * frequency;

        // Sine wave
        double value = amplitude * Math.sin((offset + phase) * Tau);

        // Bring phase down if it gets too large
        if (phase > 1000) phase -= 1000;
        if (phase > 100) phase -= 100;
        if (phase > 10) phase -= 10;

        return value;
    }
}

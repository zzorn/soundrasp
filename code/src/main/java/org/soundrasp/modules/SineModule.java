package org.soundrasp.modules;

import org.soundrasp.model.ModuleBase;
import org.soundrasp.model.Param;

/**
 *
 */
public class SineModule extends ModuleBase {

    private static final double Tau = Math.PI * 2;

    public final Param frequency;
    public final Param amplitude;
    public final Param phase;
    public final Param offset;

    private double currentPhase;

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
        super("Sine Wave");

        this.frequency = param("Frequency", frequency, 0, 100.0);
        this.amplitude = param("Amplitude", amplitude, -10.0, 10.0);
        this.offset = param("Offset", offset, -10.0, 10.0);
        this.phase = param("Phase", phase, -10.0, 10.0);
    }


    @Override
    protected double calculateValue(double durationSeconds, long sampleCounter) {

        // Update phase in the wave
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

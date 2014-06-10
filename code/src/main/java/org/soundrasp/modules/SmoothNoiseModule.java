package org.soundrasp.modules;

import org.flowutils.SimplexGradientNoise;
import org.soundrasp.model.ModuleBase;
import org.soundrasp.model.Param;

/**
 * Module producing noise with simplex noise.
 */
public class SmoothNoiseModule extends ModuleBase {

    public final Param frequency;
    public final Param amplitude;
    public final Param phase;
    public final Param offset;

    private double currentPhase;

    public SmoothNoiseModule() {
        this(440);
    }

    public SmoothNoiseModule(double frequency) {
        this(frequency, 1);
    }

    public SmoothNoiseModule(double frequency, double amplitude) {
        this(frequency, amplitude, 0);
    }

    public SmoothNoiseModule(double frequency, double amplitude, double offset) {
        this(frequency, amplitude, offset, 0);
    }

    public SmoothNoiseModule(double frequency, double amplitude, double offset, double phase) {
        super("Smooth Noise");

        this.frequency = param("Frequency", frequency, 0, 100.0);
        this.amplitude = param("Amplitude", amplitude, -10.0, 10.0);
        this.offset = param("Offset", offset, -10.0, 10.0);
        this.phase = param("Phase", "Initial offset in the noise to start from", phase, -10.0, 10.0);
    }


    @Override
    protected double calculateValue(double durationSeconds, long sampleCounter) {

        // Update position in the noise
        currentPhase += durationSeconds * frequency.get();

        // Noise
        return amplitude.get() * SimplexGradientNoise.sdnoise1((phase.get() + currentPhase)) + offset.get();

        // Note: After a long time, the phase will be very large and precision will start to be lost, as the simplex noise does not wrap.
    }
}

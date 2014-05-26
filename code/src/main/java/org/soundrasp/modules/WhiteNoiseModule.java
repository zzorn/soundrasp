package org.soundrasp.modules;

import org.soundrasp.model.ModuleBase;
import org.soundrasp.model.Param;

import java.util.Random;

/**
 * Just random values with the specified range.
 */
public class WhiteNoiseModule extends ModuleBase {

    public final Param frequency;
    public final Param amplitude;
    public final Param offset;
    public final Param seed;

    private final Random random = new Random();
    private double currentSeed;

    private double currentPhase;
    private double currentNoiseValue;

    public WhiteNoiseModule() {
        this(440);
    }

    public WhiteNoiseModule(double frequency) {
        this(frequency, 1);
    }

    public WhiteNoiseModule(double frequency, double amplitude) {
        this(frequency, amplitude, 0);
    }

    public WhiteNoiseModule(double frequency, double amplitude, double offset) {
        this(frequency, amplitude, offset, Math.random());
    }

    public WhiteNoiseModule(double frequency, double amplitude, double offset, double seed) {

        this.frequency = param("Frequency", frequency);
        this.amplitude = param("Amplitude", amplitude);
        this.offset = param("Offset", offset);
        this.seed = param("Seed", seed);
    }


    @Override
    protected double calculateValue(double durationSeconds, long sampleCounter) {

        // Update phase in the wave
        currentPhase += durationSeconds * frequency.get();

        // Update noise value if needed
        if (currentPhase >= 1) {
            currentPhase -= 1;

            // Update seed if needed
            final double newSeed = seed.get();
            if (currentSeed != newSeed) {
                random.setSeed(Double.doubleToLongBits(newSeed));
                currentSeed = newSeed;
            }

            // White noise
            currentNoiseValue = random.nextDouble() * 2.0 - 1.0;

            // Bring phase down if it gets too large
            if (currentPhase > 100000) currentPhase -= 100000;
            if (currentPhase > 1000) currentPhase -= 1000;
            if (currentPhase > 100) currentPhase -= 100;
            if (currentPhase > 10) currentPhase -= 10;
        }

        // Apply amplitude and offset
        return amplitude.get() * currentNoiseValue + offset.get();
    }
}

package org.soundrasp;


import org.soundrasp.model.Patch;
import org.soundrasp.outputs.AudioOutput;
import org.soundrasp.outputs.SignalOutput;

/**
 * Generates samples for the specified patch and sends them to the specified SignalOutput.
 */
public class Synth {

    private static final int DEFAUT_SAMPLES_PER_SECOND = 44100;

    private SignalOutput signalOutput;
    private Patch patch;

    private final int samplesPerSecond;
    private final double secondsPerSample;

    private long sampleCounter = 0;

    private boolean quit = false;


    public Synth() {
        this (null);
    }

    public Synth(SignalOutput signalOutput) {
        this(signalOutput, null);
    }

    public Synth(SignalOutput signalOutput, Patch patch) {
        this(signalOutput, patch, DEFAUT_SAMPLES_PER_SECOND);
    }

    public Synth(SignalOutput signalOutput, Patch patch, int samplesPerSecond) {
        this.patch = patch != null ? patch : new Patch();
        this.samplesPerSecond = samplesPerSecond;
        this.secondsPerSample = 1.0 / samplesPerSecond;
        this.signalOutput = signalOutput != null ? signalOutput : new AudioOutput(samplesPerSecond);
    }

    public Patch getPatch() {
        return patch;
    }

    public void setPatch(Patch patch) {
        this.patch = patch;
    }

    public void init() {
        signalOutput.init();
    }

    public void update() {

        // Determine number of samples to calculate
        int samples = signalOutput.getFreeBufferSpace();

        for (int i = 0; i < samples; i++) {
            // Update all modules
            updateRound();

            // Get sample from output module.
            final double sample = patch.getValue();

            // Write it to the output
            signalOutput.writeSample(sample);
        }
    }

    private void updateRound() {

        // Update all modules in the patch.
        patch.update(secondsPerSample, sampleCounter);

        // Keep track of the global sample index we are at
        this.sampleCounter++;
    }

    public void start() {
        while (!quit) {
            update();

            delay(1);
        }

        dispose();
    }

    public void stop() {
        quit = true;
    }

    public void dispose() {
        signalOutput.shutdown();
    }


    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            return;
        }
    }

}

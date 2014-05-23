package org.soundrasp;


import org.soundrasp.modules.Module;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Synth {

    private static final int SAMPLES_PER_SECOND = 44100;
    private static final int CHANNELS_IN_USE = 2;
    private static final int BYTES_PER_CHANNEL = 2;
    private static final int BYTES_PER_SAMPLE = BYTES_PER_CHANNEL * CHANNELS_IN_USE;
    private static final int OUTPUT_BUFFER_SIZE = BYTES_PER_SAMPLE * SAMPLES_PER_SECOND / 10;
    private final SampleData sampleData = new SampleData();

    private List<Module> modules = new ArrayList<Module>();
    private Module masterModule;

    private long sampleCounter = 0;
    private double secondsPerSample = 1.0 / SAMPLES_PER_SECOND;
    private boolean outputToAudioOut = true;
    private final byte[] outputBuffer = new byte[OUTPUT_BUFFER_SIZE];
    private int outputBufferPos = 0;
    private SourceDataLine outputLine;
    private int minSamplesToProcess;

    public void addModule(Module module) {
        modules.add(module);
        masterModule = module;
    }

    public void init() {

        // Specify audio format
        final AudioFormat audioFormat = new AudioFormat(SAMPLES_PER_SECOND, BYTES_PER_CHANNEL * 8, CHANNELS_IN_USE, true, true);

        // Open output line
        try {
            outputLine = AudioSystem.getSourceDataLine(audioFormat);
            outputLine.open(audioFormat);
            outputLine.start();
        } catch (LineUnavailableException e) {
            throw new IllegalStateException("Could not open audio output line: " + e.getMessage(), e);
        }

        // At minimum process at least 1/100 second or 1/8th of the buffer.
        minSamplesToProcess = Math.min(SAMPLES_PER_SECOND / 100, (outputLine.getBufferSize() / BYTES_PER_SAMPLE) / 8);

    }

    public void update() {

        // Determine number of samples to calculate
        int freeBufferSpace = outputLine.available();
        int samples = freeBufferSpace / BYTES_PER_SAMPLE;

        // Handle each sample
        if (samples >= minSamplesToProcess) {
            outputBufferPos = 0;

            for (int i = 0; i < samples; i++) {
                // Calculate sample
                SampleData sampleData = calculateSample(secondsPerSample, sampleCounter);

                // Store it in output buffer
                int right = convertTo16BitInt(sampleData.right);
                int left = convertTo16BitInt(sampleData.left);

                outputBuffer[outputBufferPos++] = (byte) (0xFF & (left >> 8));
                outputBuffer[outputBufferPos++] = (byte) (0xFF & left);
                outputBuffer[outputBufferPos++] = (byte) (0xFF & (right >> 8));
                outputBuffer[outputBufferPos++] = (byte) (0xFF & right);

                sampleCounter++;

                // Check if buffer is full
                if (outputBufferPos >= outputBuffer.length) {
                    // Write buffer to audio device
                    sendSamplesToAudio();

                    // Wrap
                    outputBufferPos = 0;
                }
            }

            // Write buffer to audio device
            sendSamplesToAudio();
        }
    }

    private int convertTo16BitInt(double v) {
        if (v < -1) v = -1;
        if (v > 1) v = 1;

        return (int) (v * Short.MAX_VALUE);
    }

    private void sendSamplesToAudio() {
        if (outputToAudioOut && outputBufferPos > 0) {

            outputLine.write(outputBuffer, 0, outputBufferPos);

        }
    }

    private SampleData calculateSample(double sampleDurationSeconds, long sampleCounter) {
        for (Module module : modules) {
            module.calculateSample(sampleDurationSeconds, sampleCounter);
        }

        return masterModule.getCurrentSample();
    }

    public void dispose() {
        outputLine.flush();
        outputLine.close();
    }

}

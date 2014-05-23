package org.soundrasp.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.utils.Array;

/**
 *
 */
public class Synth {

    private static final double NANOSECONDS_TO_SECONDS = 0.000000001;
    private static final int SAMPLES_PER_SECOND = 44100;
    private static final int SAMPLE_BUFFER_SIZE = SAMPLES_PER_SECOND * 1;
    private final SampleData sampleData = new SampleData();

    private Array<Module> modules = new Array<Module>();
    private Module masterModule;

    private AudioDevice audioDevice;
    private long lastTimestamp_ns;
    private long sampleCounter = 0;
    private long excessSampleTime_ns = 0;
    private long nanosecondsPerSample = 1000000000 / SAMPLES_PER_SECOND;
    private double secondsPerSample = 1.0 / SAMPLES_PER_SECOND;
    private boolean outputToAudioOut = true;
    private float[] sampleBuffer = new float[SAMPLE_BUFFER_SIZE];
    private int sampleBufferPos = 0;

    public void addModule(Module module) {
        modules.add(module);
        masterModule = module;
    }

    public void init() {
        audioDevice = Gdx.audio.newAudioDevice(SAMPLES_PER_SECOND, false);
        lastTimestamp_ns = 0;
    }

    public void update() {
        final long now = System.nanoTime();

        if (lastTimestamp_ns != 0 && now > lastTimestamp_ns) {
            // Determine number of samples
            long duration_ns = now - lastTimestamp_ns;
            long samples = (duration_ns + excessSampleTime_ns) / nanosecondsPerSample;
            long timeForSamples_ns = samples * nanosecondsPerSample;
            excessSampleTime_ns = duration_ns - timeForSamples_ns;

            // Handle each sample
            if (samples > 0) {
                sampleBufferPos = 0;

                for (int i = 0; i < samples; i++) {
                    // Calculate sample
                    SampleData sampleData = calculateSample(secondsPerSample, sampleCounter);

                    // Store it in our buffer
                    sampleBuffer[sampleBufferPos++] = (float) sampleData.left;
                    sampleBuffer[sampleBufferPos++] = (float) sampleData.right;

                    sampleCounter++;

                    // Check if buffer is full
                    if (sampleBufferPos + 1 >= SAMPLE_BUFFER_SIZE) {
                        // Write buffer
                        sendSamplesToAudio();

                        // Wrap
                        sampleBufferPos = 0;
                    }
                }

                // Write to buffer
                sendSamplesToAudio();
            }
        }

        lastTimestamp_ns = now;
    }

    private void sendSamplesToAudio() {
        if (outputToAudioOut && sampleBufferPos > 0) {
            audioDevice.writeSamples(sampleBuffer, 0, sampleBufferPos);
        }
    }

    private SampleData calculateSample(double sampleDurationSeconds, long sampleCounter) {
        for (Module module : modules) {
            module.calculateSample(sampleDurationSeconds, sampleCounter);
        }

        return masterModule.getCurrentSample();
    }

    public void dispose() {
        audioDevice.dispose();
    }

}

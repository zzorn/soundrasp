package org.soundrasp.outputs;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Signal output implementation using the java audio output
 */
public final class AudioOutput extends SignalOutputBase {

    private static final int CHANNELS = 2;
    private static final int BYTES_PER_CHANNEL = 2;
    private final int SAMPLE_SIZE_BYTES = CHANNELS * BYTES_PER_CHANNEL;

    private static final int DEFAULT_FREQUENCY_HZ = 44100;
    private static final double DEFAULT_BUFFER_SIZE_SECONDS = 0.04;

    private final int audioDeviceBufferSizeSamples;
    private final int ownBufferSizeSamples;
    private final int ownBufferSizeBytes;
    private final byte[] buffer;

    private SourceDataLine outputLine;

    private int bufferPos = 0;

    /**
     * Creates an AudioOutput with a frequency of 44.1 kHz and a buffer size of 40 milliseconds.
     */
    public AudioOutput() {
        this(DEFAULT_FREQUENCY_HZ, DEFAULT_BUFFER_SIZE_SECONDS);
    }

    /**
     * Creates an AudioOutput with the specified frequency and a buffer size of 40 milliseconds.
     *
     * @param frequency_Hz frequency to produce sound at, in Hertz.
     */
    public AudioOutput(int frequency_Hz) {
        this(frequency_Hz, DEFAULT_BUFFER_SIZE_SECONDS);
    }


    /**
     * Creates an AudioOutput with the specified frequency and buffer size.
     *
     * @param frequency_Hz frequency to produce sound at.
     * @param audioDeviceBufferSizeSeconds Size of the audio buffer, in seconds.
     *                                     It should be as small as possible,
     *                                     while still avoiding clicks caused by buffer under-runs.
     */
    public AudioOutput(int frequency_Hz, double audioDeviceBufferSizeSeconds) {
        super(frequency_Hz);

        audioDeviceBufferSizeSamples = Math.max(8, (int) (frequency_Hz * audioDeviceBufferSizeSeconds));

        // Keep a small local buffer, to not delay too much, but to reduce the number of function calls a bit.
        ownBufferSizeSamples = Math.max(1, this.audioDeviceBufferSizeSamples / 8);
        ownBufferSizeBytes = ownBufferSizeSamples * SAMPLE_SIZE_BYTES;
        buffer = new byte[ownBufferSizeBytes];
    }


    @Override
    public void doInit() {
        // Specify audio format
        final AudioFormat audioFormat = new AudioFormat(getFrequency_Hz(), BYTES_PER_CHANNEL * 8, CHANNELS, true, true);

        // Open output line
        try {
            outputLine = AudioSystem.getSourceDataLine(audioFormat);
            outputLine.open(audioFormat, SAMPLE_SIZE_BYTES * audioDeviceBufferSizeSamples);
            outputLine.start();
        } catch (LineUnavailableException e) {
            throw new IllegalStateException("Could not open audio output line: " + e.getMessage(), e);
        }
    }

    @Override
    public int getFreeBufferSpace() {
        return outputLine.available() / SAMPLE_SIZE_BYTES;
    }

    private void writeSample(double sample) {

        bufferSample(sample);

        writeBufferIfFullEnough();
    }

    @Override
    public void writeMonoSample(double monoSample) {
        if (CHANNELS == 2) {
            writeSample(monoSample);
            writeSample(monoSample);
        }
        else if (CHANNELS == 1) {
            writeSample(monoSample);
        }
        else {
            throw new IllegalStateException("Unsupported number of channels " + CHANNELS);
        }
    }

    @Override
    public void writeStereoSample(double leftSample, double rightSample) {
        if (CHANNELS == 2) {
            writeSample(leftSample);
            writeSample(rightSample);
        }
        else if (CHANNELS == 1) {
            // Mix
            writeSample(0.5 * (leftSample + rightSample));
        }
        else {
            throw new IllegalStateException("Unsupported number of channels " + CHANNELS);
        }
    }

    /**
     * Writes samples from the specified array to the output.
     * @param sampleBuffer array to write samples from.
     * @param start First index in the array to write from (inclusive.
     * @param end last index in the array to write from (exclusive).
     */
    private void writeSamples(double[] sampleBuffer, int start, int end) {

        // Store the samples in the buffer
        for (int sourcePos = start; sourcePos < end; sourcePos++) {
            bufferSample(sampleBuffer[sourcePos]);
        }

        writeBufferIfFullEnough();
    }

    private void bufferSample(double sample) {

        // Clamp to -1 .. 1 range
        if (sample < -1.0) sample = -1.0;
        else if (sample > 1.0) sample = 1.0;

        // Convert to 16 bit signed integer
        final int sample16bit = (int) (sample * Short.MAX_VALUE);

        // Store sample as bytes, in big endian order
        buffer[bufferPos++] = (byte) (0xFF & (sample16bit >> 8));
        buffer[bufferPos++] = (byte) (0xFF & sample16bit);

        // Check if buffer is full
        if (bufferPos >= ownBufferSizeBytes) {
            writeBufferToDevice();
        }
    }

    private void writeBufferIfFullEnough() {
        // If our buffer is full, write to the audio buffer.
        if (bufferPos >= ownBufferSizeBytes) {
            writeBufferToDevice();
        }
    }

    private void writeBufferToDevice() {
        // Write buffer to audio device
        outputLine.write(buffer, 0, bufferPos);

        // Wrap
        bufferPos = 0;
    }


    @Override
    protected void doShutdown() {
        outputLine.flush();
        outputLine.close();
    }
}

package org.soundrasp.outputs;

import org.flowutils.service.Service;

/**
 * Something to send data to.
 */
public interface SignalOutput extends Service {

    /**
     * @return how many samples can be written to this output without blocking.
     */
    public int getFreeBufferSpace();

    /**
     * Write a single sample to the output.
     */
    public void writeSample(double sample);

    /**
     * Writes samples from the specified array to the output.
     * @param sampleBuffer array to write samples from.
     * @param start First index in the array to write from (inclusive.
     * @param end last index in the array to write from (exclusive).
     */
    public void writeSamples(double sampleBuffer[], int start, int end);

}

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
     * Write a mono sample to the output.
     */
    public void writeMonoSample(double monoSample);

    /**
     * Write a stereo sample to the output.
     */
    public void writeStereoSample(double leftSample, double rightSample);


}

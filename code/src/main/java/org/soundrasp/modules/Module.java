package org.soundrasp.modules;

import org.soundrasp.SampleData;

/**
 *
 */
public interface Module {
    void calculateSample(double durationSeconds, long sampleCounter);

    SampleData getCurrentSample();
}

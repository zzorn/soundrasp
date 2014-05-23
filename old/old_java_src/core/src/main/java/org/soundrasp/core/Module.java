package org.soundrasp.core;

/**
 *
 */
public interface Module {
    void calculateSample(double durationSeconds, long sampleCounter);

    SampleData getCurrentSample();
}

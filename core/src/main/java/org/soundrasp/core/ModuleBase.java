package org.soundrasp.core;

/**
 *
 */
public abstract class ModuleBase implements Module {

    private final SampleData sampleData = new SampleData();

    @Override
    public final void calculateSample(double durationSeconds, long sampleCounter) {
        calculateSample(durationSeconds, sampleCounter, sampleData);
    }

    protected void calculateSample(double durationSeconds, long sampleCounter, SampleData dataOut) {
        final double value = calculateSampleValue(durationSeconds, sampleCounter);
        sampleData.left = value;
        sampleData.right = value;
    }

    protected double calculateSampleValue(double durationSeconds, long sampleCounter) {
        return 0;
    }

    @Override
    public final SampleData getCurrentSample() {
        return sampleData;
    }
}

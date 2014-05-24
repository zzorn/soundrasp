package org.soundrasp.model;

import org.soundrasp.model.Module;

import java.util.List;

/**
 * Base class for modules.  Takes care of common functionality.
 */
public abstract class ModuleBase implements Module {

    private double value = 0;

    @Override
    public final void update(double durationSeconds, long sampleCounter) {
        value = calculateValue(durationSeconds, sampleCounter);
    }

    protected abstract double calculateValue(double durationSeconds, long sampleCounter);

    @Override
    public final double getValue() {
        return value;
    }

    @Override
    public List<Parameter> getParameters() {
        return null; // TODO
    }
}

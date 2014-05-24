package org.soundrasp.model;

import org.soundrasp.model.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for modules.  Takes care of common functionality.
 */
public abstract class ModuleBase implements Module {

    private double value = 0;
    private List<Parameter> parameters = new ArrayList<Parameter>(16);

    @Override
    public final void update(double durationSeconds, long sampleCounter) {
        value = calculateValue(durationSeconds, sampleCounter);
    }

    protected abstract double calculateValue(double durationSeconds, long sampleCounter);

    @Override
    public final double getValue() {
        return value;
    }

    /**
     * Defines a parameter with no description for this module.
     *
     * @param name user readable name of the parameter.  Should be 16 characters or shorter.
     * @param defaultValue initial and default value for the parameter.
     * @return the registered parameter.
     */
    protected Parameter parameter(String name, double defaultValue) {
        return parameter(name, null, defaultValue);
    }

    /**
     * Defines a parameter for this module.
     *
     * @param name user readable name of the parameter.  Should be 16 characters or shorter.
     * @param description user readable description of the parameter.
     * @param defaultValue initial and default value for the parameter.
     * @return the registered parameter.
     */
    protected Parameter parameter(String name, String description, double defaultValue) {
        final Parameter parameter = new Parameter(name, description, defaultValue);
        parameters.add(parameter);
        return parameter;
    }

    @Override
    public List<Parameter> getParameters() {
        return parameters;
    }
}

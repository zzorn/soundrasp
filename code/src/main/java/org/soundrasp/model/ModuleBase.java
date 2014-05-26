package org.soundrasp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for modules.  Takes care of common functionality.
 */
public abstract class ModuleBase implements Module {

    private double value = 0;
    private List<Param> parameters;

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
     * Defines a parameter with no description and a zero default value for this module.
     *
     * @param name user readable name of the parameter.  Should be 16 characters or shorter.
     * @return the registered parameter.
     */
    protected Param param(String name) {
        return param(name, null, 0);
    }

    /**
     * Defines a parameter with no description for this module.
     *
     * @param name user readable name of the parameter.  Should be 16 characters or shorter.
     * @param defaultValue initial and default value for the parameter.
     * @return the registered parameter.
     */
    protected Param param(String name, double defaultValue) {
        return param(name, null, defaultValue);
    }

    /**
     * Defines a parameter for this module.
     *
     * @param name user readable name of the parameter.  Should be 16 characters or shorter.
     * @param description user readable description of the parameter.
     * @param defaultValue initial and default value for the parameter.
     * @return the registered parameter.
     */
    protected Param param(String name, String description, double defaultValue) {
        final Param parameter = new Param(name, description, defaultValue);

        if (parameters == null) {
            parameters = new ArrayList<Param>(16);
        }
        parameters.add(parameter);

        return parameter;
    }

    @Override
    public List<Param> getParameters() {
        return parameters;
    }
}

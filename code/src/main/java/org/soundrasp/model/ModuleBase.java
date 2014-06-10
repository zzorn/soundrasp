package org.soundrasp.model;

import org.flowutils.Check;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base class for modules.  Takes care of common functionality.
 */
public abstract class ModuleBase implements Module {

    private static final int LISTENER_UPDATE_SKIP = 1000;

    private int listenerUpdateStep = 0;

    private double value = 0;
    private List<Param> parameters;

    private String name;
    private String description;

    private final Set<ModuleListener> listeners = new HashSet<>(5);

    protected ModuleBase() {
        this(null);
    }

    protected ModuleBase(String name) {
        this(name, null);
    }

    protected ModuleBase(String name, String description) {
        if (name == null) name = getClass().getSimpleName();

        this.name = name;
        this.description = description;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public final void update(double durationSeconds, long sampleCounter) {
        value = calculateValue(durationSeconds, sampleCounter);

        // Notify listeners now and then
        if (listenerUpdateStep-- <= 0) {
            notifyListenersValueChanged(sampleCounter, value);
            listenerUpdateStep = LISTENER_UPDATE_SKIP;
        }
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
        return param(name, null, 0, -1.0, 1.0);
    }

    /**
     * Defines a parameter with no description for this module.
     *
     * @param name user readable name of the parameter.  Should be 16 characters or shorter.
     * @param defaultValue initial and default value for the parameter.
     * @param minValue
     * @param maxValue
     * @return the registered parameter.
     */
    protected Param param(String name, double defaultValue, double minValue, double maxValue) {
        return param(name, null, defaultValue, minValue, maxValue);
    }

    /**
     * Defines a parameter for this module.
     *
     * @param name user readable name of the parameter.  Should be 16 characters or shorter.
     * @param description user readable description of the parameter.
     * @param defaultValue initial and default value for the parameter.
     * @param minValue
     * @param maxValue
     * @return the registered parameter.
     */
    protected Param param(String name, String description, double defaultValue, double minValue, double maxValue) {
        final Param parameter = new Param(name, description, defaultValue, minValue, maxValue);

        getParameters().add(parameter);

        return parameter;
    }

    @Override
    public List<Param> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<Param>(16);
        }

        return parameters;
    }

    @Override
    public final void addListener(ModuleListener listener) {
        Check.notNull(listener, "listener");

        listeners.add(listener);
    }

    @Override
    public final void removeListener(ModuleListener listener) {
        listeners.remove(listener);
    }

    protected void notifyListenersValueChanged(long sampleCounter, double value) {
        for (ModuleListener listener : listeners) {
            listener.onValueChange(this, sampleCounter, value);
        }
    }
}

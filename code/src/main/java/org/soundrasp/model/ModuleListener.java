package org.soundrasp.model;

/**
 * Listener that gets notified of changes in a module.
 */
public interface ModuleListener {

    /**
     * Called every N times the value of a module changes.
     * (Calling every time the value of a module changes would kill performance).
     * @param module the module whose value changed.
     * @param sampleCounter the sample number that the value is for.
     * @param value the value of the module.
     */
    void onValueChange(Module module, long sampleCounter, double value);

    /**
     * Called when internal configuration of a module is changed
     * (e.g. modules added or removed from a PatchModule, or parameters added or removed).
     *
     * @param module the module whose configuration changed.
     */
    void onConfigurationChanged(Module module);

    /**
     * Called when an unconnected parameter value of a module is changed.
     * Not called when the value of a parameter that is connected to an output of a module changes as a result of the module output changing.
     *
     * @param module the module whose parameter changed.
     * @param changedParameter the parameter that changed.
     */
    void onParameterChanged(Module module, Param changedParameter);

}

package org.soundrasp.model;


import java.util.List;

/**
 * Interface for different types of modules.
 */
// TODO: Support for making copies and linked copies of modules?
// TODO  Linked copies have independent values and internal state, but use the configuration and internal parameter values of their sources
// TODO  Or could we manage with just independent copies, where each copy is an unique clone?
// TODO: Or just use special modules for the case where we need output variables (PatchModule)
public interface Module extends Source {

    String getName();

    String getDescription();

    /**
     * Updates the output value of the module based on its parameters.
     */
    void update(double durationSeconds, long sampleCounter);

    /**
     * @return list of the parameters provided by the module.
     */
    List<Param> getParameters();

    void addListener(ModuleListener listener);

    void removeListener(ModuleListener listener);

}

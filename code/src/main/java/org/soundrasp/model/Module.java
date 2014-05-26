package org.soundrasp.model;


import java.util.List;

/**
 * Interface for different types of modules.
 */
public interface Module extends Source {

    /**
     * Updates the output value of the module based on its parameters.
     */
    void update(double durationSeconds, long sampleCounter);

    /**
     * @return list of the parameters provided by the module.
     */
    List<Param> getParameters();

}

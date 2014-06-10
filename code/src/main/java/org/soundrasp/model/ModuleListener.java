package org.soundrasp.model;

/**
 */
public interface ModuleListener {

    void onValueChange(Module module, long sampleCounter, double value);

}

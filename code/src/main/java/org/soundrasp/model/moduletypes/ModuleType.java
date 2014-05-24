package org.soundrasp.model.moduletypes;

import org.soundrasp.model.Module;

/**
 * Information about a type of available module.
 */
public interface ModuleType {

    String getName();

    String getDescription();

    /**
     * @return a new instance of the module type.
     */
    Module createModule();

}

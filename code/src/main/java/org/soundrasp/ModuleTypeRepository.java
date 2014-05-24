package org.soundrasp;

import org.soundrasp.model.moduletypes.ModuleType;

import java.util.List;

/**
 * Keeps track of available module types.
 */
public interface ModuleTypeRepository {

    List<ModuleType> getModuleTypes();

}

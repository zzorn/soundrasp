package org.soundrasp.model.moduletypes;

import org.flowutils.Check;
import org.soundrasp.model.Module;

/**
 * A ModuleType representing a simple builtin module.
 */
public class BuiltinModuleType<T extends Module> extends ModuleTypeBase {

    private final Class<T> moduleClass;

    public BuiltinModuleType(String name, Class<T> moduleClass) {
        this(name, null, moduleClass);
    }

    public BuiltinModuleType(String name, String description, Class<T> moduleClass) {
        super(name, description);
        Check.notNull(moduleClass, "moduleClass");

        this.moduleClass = moduleClass;
    }

    @Override
    public T createModule() {
        try {
            // Create instance
            return moduleClass.newInstance();

        } catch (Exception e) {
            throw new IllegalStateException("Could not create a new instance of the Module " + moduleClass + ": " + e.getMessage(), e);
        }
    }
}

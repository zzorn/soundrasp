package org.soundrasp.model.moduletypes;

import org.flowutils.Check;

/**
 * Common functionality for ModuleTypes
 */
public abstract class ModuleTypeBase implements ModuleType {

    private final String name;
    private final String description;

    public ModuleTypeBase(String name) {
        this(name, null);
    }

    public ModuleTypeBase(String name, String description) {
        Check.nonEmptyString(name, "name");

        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}

package org.soundrasp;

import org.flowutils.Check;
import org.soundrasp.model.Module;
import org.soundrasp.model.moduletypes.BuiltinModuleType;
import org.soundrasp.model.moduletypes.ModuleType;
import org.soundrasp.modules.SineModule;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ModuleTypeRepositoryImpl implements ModuleTypeRepository {

    private final List<ModuleType> moduleTypes = new ArrayList<ModuleType>();

    @Override
    public List<ModuleType> getModuleTypes() {
        return moduleTypes;
    }

    public ModuleTypeRepositoryImpl() {
        registerBuiltInModuleTypes();
    }

    public void registerModuleType(ModuleType moduleType) {
        Check.notNull(moduleType, "moduleType");

        moduleTypes.add(moduleType);
    }

    public <T extends Module> void registerBuiltinModuleType(String name, Class<T> modelClass) {
        registerBuiltinModuleType(name, null, modelClass);
    }

    public <T extends Module> void registerBuiltinModuleType(String name, String desc, Class<T> modelClass) {
        registerModuleType(new BuiltinModuleType<T>(name, desc, modelClass));
    }

    protected void registerBuiltInModuleTypes() {
        registerBuiltinModuleType("Sine Wave", SineModule.class);
    }
}

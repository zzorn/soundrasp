package org.soundrasp;

import org.flowutils.Check;
import org.flowutils.service.ServiceBase;
import org.soundrasp.model.Module;
import org.soundrasp.model.moduletypes.BuiltinModuleType;
import org.soundrasp.model.moduletypes.ModuleType;
import org.soundrasp.modules.*;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ModuleTypeRepositoryImpl extends ServiceBase implements ModuleTypeRepository {

    private final List<ModuleType> moduleTypes = new ArrayList<ModuleType>();



    @Override
    public List<ModuleType> getModuleTypes() {
        return moduleTypes;
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
        registerBuiltinModuleType("Smooth Noise", SmoothNoiseModule.class);
        registerBuiltinModuleType("White Noise", WhiteNoiseModule.class);
        registerBuiltinModuleType("Inertia", InertiaModule.class);
        registerBuiltinModuleType("Output", OutputModule.class);
    }

    @Override
    protected void doInit() {
        registerBuiltInModuleTypes();
    }

    @Override
    protected void doShutdown() {

    }
}

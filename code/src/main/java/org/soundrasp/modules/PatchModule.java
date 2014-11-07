package org.soundrasp.modules;

import org.flowutils.Check;
import org.soundrasp.model.Module;
import org.soundrasp.model.ModuleBase;
import org.soundrasp.model.Param;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A module that contains a number of other modules (a patch).
 *
 * One of the modules functions as the default output.
 */
// TODO: Add support for input (and output?) parameters.
// TODO: Create output module for each parameter added, so modules can use the parameters
public final class PatchModule extends ModuleBase {

    private List<Module> modules = new ArrayList<>();
    private Module outputModule = null;

    private transient List<Module> readOnlyModules;


    /**
     * Adds a module to this PatchModule.
     *
     * @param module the module to add.  Should not be null or already added.
     * @return the added module.
     */
    public final <T extends Module> T addModule(T module) {
        Check.notNull(module, "module");
        Check.notContained(module, modules, "modules");

        // Store module
        modules.add(module);

        // Set default output module if it is not yet set
        if (outputModule == null) {
            outputModule = module;
        }

        // Notify listeners
        notifyListenersConfigurationChanged();

        return module;
    }

    /**
     * Removes a module from this PatchModule.
     *
     * @param module the module to remove.  Should not be null and should have been added earlier.
     */
    public final <T extends Module> void removeModule(T module) {
        Check.notNull(module, "module");
        Check.contained(module, modules, "modules");

        // Remove module
        modules.remove(module);

        // If the removed module was the output module, set the output module to the first found module
        if (module == outputModule) {
            if (!modules.isEmpty()) {
                outputModule = modules.get(0);
            }
            else {
                outputModule = null;
            }
        }

        // Notify listeners
        notifyListenersConfigurationChanged();
    }

    /**
     * @return the module used as output for the whole PatchModule, or null if there are no modules in the patch.
     */
    public final Module getOutputModule() {
        return outputModule;
    }

    /**
     * Selects what module should be used as output of this patch.
     * The selected output module must already have been added to this patch.
     *
     * @param outputModule the module to use as output, must exist in the patch.
     */
    public final void setOutputModule(Module outputModule) {
        Check.contained(outputModule, modules, "modules");

        if (this.outputModule != outputModule) {
            // Update output module
            this.outputModule = outputModule;

            // Notify listeners
            notifyListenersConfigurationChanged();
        }
    }

    /**
     * @return a read only list with the modules in this PatchModule.
     */
    public final List<Module> getModules() {
        if (readOnlyModules == null) readOnlyModules = Collections.unmodifiableList(modules);

        return readOnlyModules;
    }


    @Override protected double doUpdate(double durationSeconds, long sampleCounter) {
        // Update all modules
        for (Module module : modules) {
            module.update(durationSeconds, sampleCounter);
        }

        // Return value of the default output module (or zero if we have no default output module).
        if (outputModule != null) {
            return outputModule.getValue();
        }
        else {
            return 0;
        }
    }

    @Override protected Param param(String name,
                                    String description,
                                    double defaultValue,
                                    double minValue,
                                    double maxValue) {
        final Param param = super.param(name, description, defaultValue, minValue, maxValue);

        // TODO: Add custom module passing the param to this patch
        // TODO: remove the module if the param is removed (keep references in a map?)

        return param;
    }
}

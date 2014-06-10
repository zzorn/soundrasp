package org.soundrasp.model;

import java.util.HashSet;
import java.util.Set;

/**
 * A slot that a module can be placed in.
 * The module in the slot can be changed on the fly.
 */
public final class Slot implements Source {

    private Module module;

    private final String name;

    private final Set<SlotListener> listeners = new HashSet<>();

    public Slot(String name) {
        this.name = name;
    }

    public Slot(Module module, String name) {
        this.name = name;
        setModule(module);
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    @Override
    public String getName() {
        if (module != null) {
            return name + " " +module.getName();
        }
        else {
            return name + " Empty";
        }
    }

    @Override
    public double getValue() {
        if (module != null) return module.getValue();
        else return 0;
    }

    public void update(double secondsPerSample, long sampleCounter) {
        if (module != null) module.update(secondsPerSample, sampleCounter);
    }

    public void addListener(SlotListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SlotListener listener) {
        listeners.remove(listener);
    }

    public interface SlotListener {
        void onModuleChanged(Slot slot);
    }


}

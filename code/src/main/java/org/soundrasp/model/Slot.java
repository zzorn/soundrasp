package org.soundrasp.model;

/**
 * A slot that a module can be placed in.
 * The module in the slot can be changed on the fly.
 */
public final class Slot implements Source {

    private Module module;

    public Slot() {
    }

    public Slot(Module module) {
        setModule(module);
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    @Override
    public double getValue() {
        if (module != null) return module.getValue();
        else return 0;
    }

    public void update(double secondsPerSample, long sampleCounter) {
        if (module != null) module.update(secondsPerSample, sampleCounter);
    }
}

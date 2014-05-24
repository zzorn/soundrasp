package org.soundrasp.outputs;

import org.flowutils.service.ServiceBase;

/**
 * Base class for signal output services.
 */
public abstract class SignalOutputBase extends ServiceBase implements SignalOutput {

    private final int frequency_Hz;

    protected SignalOutputBase(int frequency_Hz) {
        this.frequency_Hz = frequency_Hz;
    }

    protected int getFrequency_Hz() {
        return frequency_Hz;
    }
}

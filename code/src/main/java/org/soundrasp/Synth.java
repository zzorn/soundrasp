package org.soundrasp;


import org.flowutils.service.ServiceBase;
import org.soundrasp.model.Module;
import org.soundrasp.outputs.AudioOutput;
import org.soundrasp.outputs.SignalOutput;

/**
 * Generates samples from the specified module and sends them to the specified signalOutput.
 */
// TODO: Add support for multichannel outputs, so that we do not need to know about the output inside a module, and can just output data from a module
public class Synth extends ServiceBase implements Updating {

    private static final int DEFAUT_SAMPLES_PER_SECOND = 44100;

    private SignalOutput signalOutput;
    private Module module;

    private final int samplesPerSecond;
    private final double secondsPerSample;

    private long sampleCounter = 0;


    public Synth() {
        this (null);
    }

    public Synth(SignalOutput signalOutput) {
        this(signalOutput, null);
    }

    public Synth(SignalOutput signalOutput, Module module) {
        this(signalOutput, module, DEFAUT_SAMPLES_PER_SECOND);
    }

    public Synth(SignalOutput signalOutput, Module module, int samplesPerSecond) {
        this.module = module;
        this.samplesPerSecond = samplesPerSecond;
        this.secondsPerSample = 1.0 / samplesPerSecond;
        this.signalOutput = signalOutput != null ? signalOutput : new AudioOutput(samplesPerSecond);
    }

    /**
     * @return the module used to generate the signal to send to the output.
     */
    public Module getModule() {
        return module;
    }

    /**
     * @param module the module used to generate the signal to send to the output.
     */
    public void setModule(Module module) {
        this.module = module;
    }

    public void update() {

        // Determine number of samples to calculate
        int samples = signalOutput.getFreeBufferSpace();

        for (int i = 0; i < samples; i++) {
            // Update the module and calculate the sample
            if (module != null) {
                module.update(secondsPerSample, sampleCounter);

                // TODO: Take the single or multichannel output of the module and send it to the signalOutput
                // TODO For this we first need support for multichannel outputs, or several outputs

            }

            // Keep track of the global sample index we are at
            this.sampleCounter++;
        }
    }


    @Override
    protected void doInit() {

    }

    @Override
    protected void doShutdown() {

    }
}

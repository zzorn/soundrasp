package org.soundrasp.modules;

import org.soundrasp.SoundRasp;
import org.soundrasp.model.ModuleBase;
import org.soundrasp.model.Param;

import static org.flowutils.MathUtils.*;

/**
 * Outputs sound.
 */
public class OutputModule extends ModuleBase {

    public final Param centerChannel = param("Center Channel", "This channel is audible in both the left and right channels", 0, -1.0, 1.0);
    public final Param leftChannel = param("Left Channel");
    public final Param rightChannel = param("Right Channel");
    public final Param centerVolume = param("Center Volume", 1, 0, 2.0);
    public final Param leftVolume = param("Left Volume", 1, 0, 2.0);
    public final Param rightVolume = param("Right Volume", 1, 0, 2.0);
    public final Param blendAmount = param("Blend", "How much to mix the two channels together", 0, 0, 1.0);
    public final Param masterVolume = param("Master Volume", 0.5, 0, 2.0);
    public final Param masterBalance = param("Master Balance", "Balance between right and left channel volume", 0, -1.0, 1.0);

    public OutputModule() {
        super("Output");
    }

    @Override
    protected double calculateValue(double durationSeconds, long sampleCounter) {
        double left =  centerChannel.get() * centerVolume.get() +
                       leftChannel.get() * leftVolume.get();
        double right = centerChannel.get() * centerVolume.get() +
                       rightChannel.get() * rightVolume.get();

        final double balance = masterBalance.get();
        right *= mapAndClamp(balance, -1, 0, 0, 1);
        left  *= mapAndClamp(balance, 0, 1, 1, 0);

        final double blend = clamp0To1(blendAmount.get());
        if (blend > 0) {
            final double average = 0.5 * (left + right);
            left = mix(blend, left, average);
            right = mix(blend, right, average);
        }

        final double volume = masterVolume.get();
        right *= volume;
        left  *= volume;

        SoundRasp.getContext().signalOutput.writeStereoSample(left, right);

        return 0.5 * (left + right);
    }
}

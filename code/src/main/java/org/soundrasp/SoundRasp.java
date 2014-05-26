package org.soundrasp;


import org.flowutils.service.Service;
import org.soundrasp.model.Patch;
import org.soundrasp.modules.*;
import org.soundrasp.outputs.AudioOutput;
import org.soundrasp.outputs.SignalOutput;

import java.util.ArrayList;
import java.util.List;

public final class SoundRasp  {

    private final List<Service> services = new ArrayList<Service>();
    private final List<Updating> updatingServices = new ArrayList<Updating>();

    private static SoundRasp soundRasp;

    public static SoundRasp getContext() {
        if (soundRasp == null) soundRasp = new SoundRasp();
        return soundRasp;
    }

    public final ModuleTypeRepository moduleTypeRepository = service(new ModuleTypeRepositoryImpl());
    public final SignalOutput signalOutput = service(new AudioOutput());
    public final Synth synth = service(new Synth(signalOutput));

    private boolean quit = false;

    public static void main(String[] args) {
        getContext().start();
    }

    public void init() {
        // Init
        for (Service service : services) {
            service.init();
        }

        configureTestSynth();
	}

    private void configureTestSynth() {
        final Patch patch = synth.getPatch();

        final WhiteNoiseModule melody = patch.addSlotWithModule(new WhiteNoiseModule(0.5, 100, 200));

        final InertiaModule inertiaPitch = patch.addSlotWithModule(new InertiaModule(0.3, 0.01, 0.1, 0));
        inertiaPitch.target.set(melody);

        final SineModule simpleMelody = patch.addSlotWithModule(new SineModule(220));
        simpleMelody.frequency.set(inertiaPitch);

        final SineModule hfo = patch.addSlotWithModule(new SineModule(220));
        final SineModule lfo = patch.addSlotWithModule(new SineModule(21));
        final SineModule lfo2 = patch.addSlotWithModule(new SineModule(1.1, 0.7));
        final SineModule vlfo = patch.addSlotWithModule(new SineModule(0.131, 1, 1.1));
        final SineModule vlfo2 = patch.addSlotWithModule(new SineModule(0.13, 500, 500));
        hfo.amplitude.set(lfo);
        lfo.frequency.set(vlfo);
        hfo.frequency.set(vlfo2);

        inertiaPitch.attraction.set(vlfo);

        final SmoothNoiseModule windAmp = patch.addSlotWithModule(new SmoothNoiseModule(0.1, 0.3, 0.8));
        final SmoothNoiseModule windFreq = patch.addSlotWithModule(new SmoothNoiseModule(0.21, 300, 800));
        final SmoothNoiseModule wind = patch.addSlotWithModule(new SmoothNoiseModule(400, 0.5, 0));
        wind.amplitude.set(windAmp);
        wind.frequency.set(windFreq);


        final OutputModule outputModule = patch.addSlotWithModule(new OutputModule());
        outputModule.leftChannel.set(simpleMelody);
        outputModule.rightChannel.set(wind);
        outputModule.leftVolume.set(0.6);
        outputModule.rightVolume.set(0.8);
        outputModule.blendAmount.set(0.6);
    }

    public void start() {

        init();

        // Loop
        while (!quit) {
            for (Updating updatingService : updatingServices) {
                updatingService.update();
            }

            delay(1);
        }

        // Shutdown (in reverse order)
        for (int i = services.size() - 1; i >= 0; i--) {
            services.get(i).shutdown();
        }
    }

    public void stop() {
        quit = true;
    }

    private <T extends Service> T service(T serviceToAdd) {
        services.add(serviceToAdd);

        if (serviceToAdd instanceof Updating) {
            updatingServices.add((Updating) serviceToAdd);
        }

        return serviceToAdd;
    }

    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            return;
        }
    }

}

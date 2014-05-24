package org.soundrasp;


import org.soundrasp.modules.SineModule;

public class SoundRasp  {

    private ModuleTypeRepository moduleTypeRepository;
    private Synth synth;

    public static void main(String[] args) {
        SoundRasp soundRasp = new SoundRasp();
        soundRasp.init();
        soundRasp.start();
    }

    public void init() {

        moduleTypeRepository = new ModuleTypeRepositoryImpl();

        synth = new Synth();

        final SineModule hfo = synth.getPatch().addSlotWithModule(new SineModule(200));
        final SineModule lfo = synth.getPatch().addSlotWithModule(new SineModule(2));
        final SineModule vlfo = synth.getPatch().addSlotWithModule(new SineModule(0.1, 2, 2));
        hfo.amplitude.setFrom(lfo);
        lfo.frequency.setFrom(vlfo);

        synth.init();
	}

    public void start() {
        synth.start();
    }

}

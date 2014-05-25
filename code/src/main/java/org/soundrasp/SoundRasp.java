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

        final SineModule hfo = synth.getPatch().addSlotWithModule(new SineModule(220));
        final SineModule lfo = synth.getPatch().addSlotWithModule(new SineModule(21));
        final SineModule vlfo = synth.getPatch().addSlotWithModule(new SineModule(0.78, 2, 2));
        final SineModule vlfo2 = synth.getPatch().addSlotWithModule(new SineModule(0.13, 100, 200));
        hfo.amplitude.setFrom(lfo);
        lfo.frequency.setFrom(vlfo);
        hfo.frequency.setFrom(vlfo2);

        synth.init();
	}

    public void start() {
        synth.start();
    }

}

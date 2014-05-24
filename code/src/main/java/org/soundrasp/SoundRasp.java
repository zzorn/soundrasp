package org.soundrasp;


import org.soundrasp.modules.SineModule;

public class SoundRasp  {

    private Synth synth;

    public static void main(String[] args) {
        SoundRasp soundRasp = new SoundRasp();
        soundRasp.init();
        soundRasp.start();
    }

    public void init() {

        synth = new Synth();

        synth.getPatch().addSlotWithModule(new SineModule(200));
        //synth.getPatch().addSlotWithModule(new SineModule(2));

        synth.init();
	}

    public void start() {
        synth.start();
    }

}

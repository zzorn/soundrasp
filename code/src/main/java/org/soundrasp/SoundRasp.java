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
        synth.addModule(new SineModule(2));
        synth.addModule(new SineModule(400));

        synth.init();
	}

    public void start() {

        while (true) {
            synth.update();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                return;
            }
        }

        //synth.dispose();
    }

}

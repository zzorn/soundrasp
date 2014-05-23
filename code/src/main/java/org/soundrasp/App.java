package org.soundrasp;

import javax.sound.sampled.*;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final int sampleRate = 44100;
    private static final int samplesToBuffer = sampleRate / 10;
    private static final byte[] buffer = new byte[samplesToBuffer];

    private static boolean quit = false;


    private static long sampleIndex = 0;

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );



        final AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, true);
        try {
            final SourceDataLine line = AudioSystem.getSourceDataLine(af);



            line.open(af);
            line.start();


            while (!quit) {
                int freeBufferSpaceSamples =  line.available() / 2;

                if ( !line.isActive() || freeBufferSpaceSamples >= line.getBufferSize() / 2 / 4 ) {

                    generateSineWavefreq(line, freeBufferSpaceSamples);

                    System.out.println("line.available() = " + line.available());
                }
                else {
                   Thread.sleep(1);
                }

            }

            line.drain();
            line.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateSineWavefreq(SourceDataLine line, int freeBufferSpaceSamples) {
        // total samples = (duration in second) * (samples per second)
        int bufPos = 0;
        int samples = Math.min(buffer.length/2, freeBufferSpaceSamples);

        for (int i = 0; i < samples; i++) {

            //double freq = (Math.sin(sampleIndex / 10000.0) / 2.0 + 0.5) * 2 + 100;
            double freq = 233;
            double samplingInterval = 1.0 * sampleRate / freq;
            double angle = (2.0 * Math.PI * sampleIndex) / samplingInterval;
            int val = (int) (Math.sin(angle) * Short.MAX_VALUE);
            buffer[bufPos++] = (byte) (0xFF & (val >> 8));
            buffer[bufPos++] = (byte) (0xFF & val);
            sampleIndex++;
        }

        line.write(buffer, 0, bufPos);
    }

}

#ifdef __cplusplus
#include <cstdlib>
#else
#include <stdlib.h>
#endif

#include <string>
#include <SDL/SDL.h>
#include <SDL/SDL_audio.h>
#include <SDL/SDL_gfxPrimitives.h>

#include <ctime>

#include <math.h>

#include "ModuleType.h"
#include "ModuleTypes.h"
#include "Module.h"
#include "Display.h"

#include "constants.h"
#include "utils.h"


using namespace std;


long timeStep = 0;

double elapsedTime = 0.0;

const int viewH = DISPLAY_H / 4;
SequenceDisplay testDisplay1 = SequenceDisplay(0, viewH*0, DISPLAY_W, viewH, 1, 10);
SequenceDisplay testDisplay2 = SequenceDisplay(0, viewH*1, DISPLAY_W, viewH, 0.01, 10);
SequenceDisplay testDisplay3 = SequenceDisplay(0, viewH*2, DISPLAY_W, viewH, 0.01, 10);


Module* modules[MODULE_COUNT];


void generateSample(double deltaTime, double time, long step) {
    for (int i = 0; i < MODULE_COUNT; i++) {
        modules[i]->calculate(modules, deltaTime, time, step);
    }

    for (int i = 0; i < MODULE_COUNT; i++) {
        modules[i]->updateValue();
    }

    testDisplay1.addDataPoint(modules[0]->moduleValue);
    testDisplay2.addDataPoint(modules[1]->moduleValue);
    testDisplay3.addDataPoint(modules[2]->moduleValue);
}


void generateSamples(Sint16 *stream, int length, double freq) {
    for (int i = 0; i < length; i++) {

        // Update time
        timeStep++;
        elapsedTime = SECONDS_PER_SAMPLE * timeStep;

        // Generate signals for all modules for this time step
        generateSample(SECONDS_PER_SAMPLE, elapsedTime, timeStep);

        // Get the sound to play from the currently selected output module
        double audibleValue = modules[OUTPUT_MODULE]->moduleValue;

        // Scale the sound to correct amplitude and feed to sound card
        stream[i] = (0.5 + 0.5 * audibleValue) * MAX_SAMPLE;
    }
}

void audio_callback(void *userdata, Uint8 *_stream, int _length) {
    Sint16 *stream = (Sint16*) _stream;
    int length = _length / 2;

    generateSamples(stream, length, 440);
}

void setupSound() {

    SDL_AudioSpec desiredSpec;

    desiredSpec.freq = FREQUENCY;
    desiredSpec.format = AUDIO_S16SYS;
    desiredSpec.channels = 1;
    desiredSpec.samples = 2048;
    desiredSpec.callback = audio_callback;
    desiredSpec.userdata = NULL;

    SDL_AudioSpec obtainedSpec;

    // Open audio for playing with desired parameters
    if (SDL_OpenAudio(&desiredSpec, &obtainedSpec) != 0) {
        printf("Could not open audio device.");
        exit(1);
    }

    // Start playing audio
    SDL_PauseAudio(0);
}




void beginChangeParams() {
    SDL_LockAudio();
}

void endChangeParams() {
    SDL_UnlockAudio();
}


int main ( int argc, char** argv ) {
    printf("Starting soundrasp\n");

    srand((unsigned)time(0));

    // Create modules
    for (int i = 0; i < MODULE_COUNT; i++) {
        modules[i] = new Module();
    }

    modules[0]->setType(OSCILLATOR_TYPE);
    //modules[0]->setParameterSource(AMP, 2);
    modules[0]->setParameterSource(FREQ, 1);
    //modules[0]->setParameterValue(FREQ, 220);
    modules[0]->setParameterValue(AMP, 1);
    modules[0]->setParameterValue(OFFS, 0);

    modules[1]->setType(OSCILLATOR_TYPE);
    modules[1]->setParameterValue(FREQ, 1);
    modules[1]->setParameterValue(AMP, 50);
    modules[1]->setParameterValue(OFFS, 200);

    modules[2]->setType(OSCILLATOR_TYPE);
    modules[2]->setParameterValue(FREQ, 1);
    modules[2]->setParameterValue(AMP, 0.5);
    modules[2]->setParameterValue(OFFS, 0.5);


    // initialize SDL video
    if ( SDL_Init( SDL_INIT_VIDEO ) < 0 ) {
        printf( "Unable to init SDL: %s\n", SDL_GetError() );
        return 1;
    }

    // Initialize audio
    SDL_Init(SDL_INIT_AUDIO);

    // make sure SDL cleans up before exit
    atexit(SDL_Quit);

    // create a new window
    SDL_Surface* screen = SDL_SetVideoMode(DISPLAY_W, DISPLAY_H, DISPLAY_BPP, (SDL_HWSURFACE | SDL_DOUBLEBUF) );
    if (screen == NULL) {
        printf("Unable to set %d x %d video with %d bits per pixel: %s\n", DISPLAY_W, DISPLAY_H, DISPLAY_BPP, SDL_GetError());
        return 1;
    }

    // Start playing
    setupSound();

    // program main loop
    bool done = false;
    while (!done) {
        // message processing loop
        SDL_Event event;
        while (SDL_PollEvent(&event)) {
            // check for messages
            switch (event.type) {
                // exit if the window is closed
            case SDL_QUIT:
                done = true;
                break;

                // check for keypresses
            case SDL_KEYDOWN: {
                // exit if ESCAPE is pressed
                if (event.key.keysym.sym == SDLK_ESCAPE)
                    done = true;
                break;
            }
            } // end switch
        } // end of message processing

        // DRAWING STARTS HERE

        // clear screen
        //SDL_FillRect(screen, 0, SDL_MapRGB(screen->format, 0, 0, 0));

        testDisplay1.draw(screen);
        testDisplay2.draw(screen);
        testDisplay3.draw(screen);

        // DRAWING ENDS HERE

        // Show changes on the screen
        SDL_Flip(screen);

        // Delay before next frame
        SDL_Delay(20);

    } // end main loop


    SDL_CloseAudio();

    printf("Soundrasp closed\n");
    return 0;
}


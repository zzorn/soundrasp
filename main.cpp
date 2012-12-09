#ifdef __cplusplus
#include <cstdlib>
#else
#include <stdlib.h>
#endif

#include <iostream>

#include <SDL/SDL.h>
#include <SDL/SDL_audio.h>

#include "sound.cpp"

using namespace std;

const int AMPLITUDE = 28000;
const int FREQUENCY = 44100;


double vValue = 0.0;



void generateSamples(Sint16 *stream, int length, double freq) {
    int i = 0;
    while (i < length) {
        stream[i] = AMPLITUDE * std::sin(vValue * 2 * M_PI / FREQUENCY);
        i++;
        vValue += freq;
    }
}

void audio_callback(void *userdata, Uint8 *_stream, int _length) {
    Sint16 *stream = (Sint16*) _stream;
    int length = _length / 2;

    generateSamples(stream, length, 440);
}

void setupSound() {
    vValue = 0.0;

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
        cerr << "Could not open audio device." << flush;
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
    cout << "Starting soundrasp\n" << flush;

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
    SDL_Surface* screen = SDL_SetVideoMode(640, 480, 16,
                                           SDL_HWSURFACE|SDL_DOUBLEBUF);
    if ( !screen ) {
        printf("Unable to set 640x480 video: %s\n", SDL_GetError());
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
        SDL_FillRect(screen, 0, SDL_MapRGB(screen->format, 0, 0, 0));

        // DRAWING ENDS HERE

        // Show changes on the screen
        SDL_Flip(screen);

        // Delay before next frame
        SDL_Delay(20);

    } // end main loop


    SDL_CloseAudio();

    cout << "Soundrasp closed\n" << flush;
    return 0;
}


#ifdef __cplusplus
#include <cstdlib>
#else
#include <stdlib.h>
#endif

#include <string>
#include <SDL/SDL.h>
#include <SDL/SDL_audio.h>

#include <ctime>

#include <math.h>

using namespace std;


const int OUTPUT_MODULE = 0;

const int MAX_SAMPLE = 28000;
const int FREQUENCY = 44100;

const int MODULE_COUNT = 16;
const int PARAM_COUNT = 8;
const int DATA_COUNT = 8;

const double SECONDS_PER_SAMPLE = 1.0 / (double)FREQUENCY;

const double Tau = M_PI * 2;

long timeStep = 0;

double elapsedTime = 0.0;

// Parameter indexes
const int FREQ = 0;
const int AMP = 1;
const int PHASE = 2;
const int OFFS = 3;

// Utils

/** NOTE: For audio applications this one sucks, too much repeating harmonics. */
long xorshiftRandomLong(long seed) {
    long x = seed ^ 2463534242UL;
    x ^= (x << 21);
    x ^= (x >> 35);
    x ^= (x << 4);
    return x;
};

// Retrns random double in range 0..1
double randomDouble() {
    long x = rand();
    return (1.0 / 1000000.0) * (double)(x % 1000000);
};




// Module functions
double sineWaveFunc(double *params, double *data, double delta, double time, long step) {
  return params[OFFS] + params[AMP] * sin(params[PHASE] * Tau + time * Tau * params[FREQ]);
}

double whiteNoiseFunc(double *params, double *data, double delta, double time, long step) {
    // Get previous value and the time it was calculated at
    double value      = data[0];
    double changeTime = data[1];

    // Recalculate value if enough time has passed
    // TODO: Make sure the phase is applied correctly
    if (time > changeTime + 1.0 / params[FREQ] + params[PHASE]) {
        value = randomDouble() * 2.0 - 1.0;
        data[0] = value;
        data[1] = time + params[PHASE];
    }

    // Determine value based on seed
    return params[OFFS] + params[AMP] * value;
}

class ModuleType {
  public:
    string name;

    // Pass in parameters of module, temporary data values, delta time, current time, time step, return module value
    double (*calculator)(double *, double *, double, double, long );

    int parameterCount;
    string* parameterNames;
    double* parameterDefaultValues;


    ModuleType(string name_, double (*calculator_)(double *, double *, double, double, long ), int parameterCount_, string* parameterNames_, double* parameterDefaultValues_) {
        name = name_;
        calculator = calculator_;
        parameterCount = parameterCount_;
        parameterNames = parameterNames_;
        parameterDefaultValues = parameterDefaultValues_;
    };


};

int normalParameterCount = 4;
string normalParameters[] = {"Freq", "Amp", "Phase", "Offs"};
double normalParamDefaults[] = {130, 1.0, 0, 0};

//ModuleType OSCILLATOR = ModuleType("Oscil", &sineWaveFunc, normalParameterCount, normalParameters, normalParamDefaults);

ModuleType MODULE_TYPES[] = {
        ModuleType("Oscil", &sineWaveFunc, normalParameterCount, normalParameters, normalParamDefaults),
        ModuleType("Noise", &whiteNoiseFunc, normalParameterCount, normalParameters, normalParamDefaults),
    };

class Module;

Module* modules[MODULE_COUNT];

class Module {
  public:
    double parameterValues[PARAM_COUNT];
    int parameterSourceModules[PARAM_COUNT];
    double dataValues[DATA_COUNT];
    ModuleType &moduleType;
    double moduleValue;
    double newModuleValue;

    Module() : moduleType(MODULE_TYPES[0]) {

        // Init

        for (int i = 0; i < PARAM_COUNT; i++) {
            parameterValues[i] = 0.0;
            parameterSourceModules[i] = -1;
        }

        for (int i = 0; i < DATA_COUNT; i++) {
            dataValues[i] = 0.0;
        }

        setType(MODULE_TYPES[0]);
    }

    void setType(ModuleType &moduleType_) {
        moduleType = moduleType_;

        for (int i = 0; i < DATA_COUNT; i++) {
            dataValues[i] = 0.0;
        }

        for (int i = 0; i < moduleType.parameterCount; i++) {
            parameterValues[i] = moduleType.parameterDefaultValues[i];
            parameterSourceModules[i] = -1;
        }

        moduleValue = 0.0;
        newModuleValue = 0.0;
    }

    double calculate(double delta, double time, long step) {
        // Read parameter values coming from other modules
        for (int i = 0; i < moduleType.parameterCount; i++) {
            int sourceModule = parameterSourceModules[i];
            if (sourceModule >= 0 && sourceModule < MODULE_COUNT) {
                parameterValues[i] = modules[sourceModule]->moduleValue;
            }
        }

        // Calculate new value for this module
        newModuleValue = moduleType.calculator(parameterValues, dataValues, delta, time, step);
        return newModuleValue;
    }

    void updateValue() {
        moduleValue = newModuleValue;
    }

    void setParameterValue(int param, double value) {
        if (param >= 0 && param < PARAM_COUNT) {
            parameterValues[param] = value;
        }
    }

    void setParameterSource(int param, int sourceModule) {
        if (param >= 0 && param < PARAM_COUNT) {
            parameterSourceModules[param] = sourceModule;
        }
    }

};



void generateSample(double deltaTime, double time, long step) {
    for (int i = 0; i < MODULE_COUNT; i++) {
        modules[i]->calculate(deltaTime, time, step);
    }

    for (int i = 0; i < MODULE_COUNT; i++) {
        modules[i]->updateValue();
    }
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

    modules[1]->setType(MODULE_TYPES[0]);
    modules[1]->setParameterValue(FREQ, 1);
    modules[1]->setParameterValue(AMP, 50);
    modules[1]->setParameterValue(OFFS, 150);

    modules[0]->setType(MODULE_TYPES[0]);
    modules[0]->setParameterSource(FREQ, 1);
    //modules[0]->setParameterValue(FREQ, 3);
    modules[0]->setParameterValue(AMP, 0.5);
    modules[0]->setParameterValue(OFFS, 0);

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

    printf("Soundrasp closed\n");
    return 0;
}


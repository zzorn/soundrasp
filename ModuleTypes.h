#ifndef SOUNDRASP_MODULETYPES_H
#define SOUNDRASP_MODULETYPES_H

#include <string>
#include <ctime>
#include <math.h>

#include "ModuleType.h"
#include "constants.h"
#include "utils.h"

using namespace std;



// Common parameter indexes
const int FREQ  = 0;
const int AMP   = 1;
const int PHASE = 2;
const int OFFS  = 3;



// Module functions
double sineWaveFunc(double *params, double *data, double delta, double time, long step) {
  //return params[OFFS] + params[AMP] * sin(params[PHASE] * Tau + time * Tau * params[FREQ]);
  return params[AMP] * sin(/*params[PHASE] * Tau + */ time * Tau * params[FREQ]);
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


// Module parameter data

const int normalParameterCount = 4;
string normalParameters[] = {"Freq", "Amp", "Phase", "Offs"};
double normalParamDefaults[] = {130, 1.0, 0, 0};

// Module types
ModuleType OSCILLATOR_TYPE = ModuleType("Oscil", &sineWaveFunc,   normalParameterCount, normalParameters, normalParamDefaults);
ModuleType NOISE_TYPE      = ModuleType("Noise", &whiteNoiseFunc, normalParameterCount, normalParameters, normalParamDefaults);

// Array with all modules
ModuleType MODULE_TYPES[] = {OSCILLATOR_TYPE, NOISE_TYPE};
const int MODULE_TYPE_COUNT = 2;


#endif // SOUNDRASP_MODULETYPES_H

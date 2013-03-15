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
const int SINE_WAVE_PHASE_DATA = 0;
double sineWaveFunc(double *params, double *data, double delta, double time, long step) {
  double phase = data[SINE_WAVE_PHASE_DATA];

  // Update phase with time delta * frequency
  phase += delta * params[FREQ];

  // Keep phase in a limited range to preserve the floating point accuracy
  if (phase > 1.0) phase -= 1.0;
  if (phase > 10.0) phase -= 10.0;
  if (phase > 1000.0) phase -= 1000.0;

  // Store phase for next time
  data[SINE_WAVE_PHASE_DATA] = phase;

  // Calculate signal using the current phase and the phase offset, then amplify and offset it
  return params[OFFS] + params[AMP] * sin(Tau * (phase + params[PHASE]));
}

const int WHITE_NOISE_DURATION_DATA = 0;
double whiteNoiseFunc(double *params, double *data, double delta, double time, long step) {

/* TODO: Similar fix to white noise as the phase in sinewave.
    double phase = data[WHITE_NOISE_DURATION_DATA];

    // Update phase with time delta * frequency
    phase += delta * params[FREQ];

    // Keep phase in a limited range to preserve the floating point accuracy
    if (phase > 1.0) phase -= 1.0;
    if (phase > 10.0) phase -= 10.0;
    if (phase > 1000.0) phase -= 1000.0;

    // Store phase for next time
    data[WHITE_NOISE_DURATION_DATA] = phase;
*/

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

#ifndef SOUNDRASP_CONSTANTS_H
#define SOUNDRASP_CONSTANTS_H

/*
  Program wide constants that alter the number of modules and such things.
*/

/** Sample frequency */
static const int FREQUENCY = 44100;

/** Maximum sample value??  TODO: Figure this out. */
static const int MAX_SAMPLE = 28000;

/** Number of seconds per one sample */
static const double SECONDS_PER_SAMPLE = 1.0 / (double)FREQUENCY;


/** Number of modules */
static const int MODULE_COUNT = 16;

/** Max number of parameters any module type can have */
static const int PARAM_COUNT = 8;

/** Max number of extra module specific data values any module type can use */
static const int DATA_COUNT = 8;

/** The index of the module used to produce the audible output */
static const int OUTPUT_MODULE = 0;


/** Width of window */
static const int DISPLAY_W = 800;

/** Height of window */
static const int DISPLAY_H = 600;

/** Bits per pixel used for the display. */
static const int DISPLAY_BPP = 32;

static const int DISPLAY_COLUMNS = 4;


#endif


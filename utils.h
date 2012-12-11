#ifndef SOUNDRASP_UTILS_H
#define SOUNDRASP_UTILS_H

#include <string>
#include <ctime>
#include <math.h>


/** One turn == 360 degrees == 2Pi.  See tauday.com */
const double Tau = M_PI * 2;

/** Returns random double in range 0..1 */
double randomDouble() {
    long x = rand();
    return (1.0 / 1000000.0) * (double)(x % 1000000);
};

/**
  Random number generated with xorshift algorithm.
  NOTE: For audio applications this one sucks, too much repeating harmonics.
*/
long xorshiftRandomLong(long seed) {
    long x = seed ^ 2463534242UL;
    x ^= (x << 21);
    x ^= (x >> 35);
    x ^= (x << 4);
    return x;
};


int clampInt(int v, int minValue, int maxValue) {
    if (v < minValue) return minValue;
    else if (v > maxValue) return maxValue;
    return v;
}

double clampZeroToOne(double v) {
    if (v < 0) return 0;
    else if (v > 1) return 1;
    return v;
}

Uint32 rgbaInt(int r, int g, int b, int a) {
    return ((r & 0xFF) << 24) |
           ((g & 0xFF) << 16) |
           ((b & 0xFF) << 8) |
           ((a & 0xFF) << 0);
}

Uint32 rgbaColor(double r, double g, double b, double a) {
    return rgbaInt(255*clampZeroToOne(r),
                   255*clampZeroToOne(g),
                   255*clampZeroToOne(b),
                   255*clampZeroToOne(a));
}


#endif

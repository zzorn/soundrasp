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


#endif

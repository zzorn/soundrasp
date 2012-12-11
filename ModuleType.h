#ifndef SOUNDRASP_MODULETYPE_H
#define SOUNDRASP_MODULETYPE_H

#include <string>
#include <ctime>
#include <math.h>

#include "utils.h"
#include "constants.h"

using namespace std;


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
#endif // SOUNDRASP_MODULETYPE_H

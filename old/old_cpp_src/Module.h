#ifndef SOUNDRASP_MODULE_H
#define SOUNDRASP_MODULE_H

#include <string>
#include <ctime>
#include <math.h>
#include <iostream>
#include <sstream>

#include "ModuleTypes.h"
#include "utils.h"
#include "constants.h"



using namespace std;

class Module {
  public:
    int moduleNumber;
    double parameterValues[PARAM_COUNT];
    int parameterSourceModules[PARAM_COUNT];
    double dataValues[DATA_COUNT];
    ModuleType &moduleType;
    double moduleValue;
    double newModuleValue;
    string moduleName;

    Module(int moduleNumber_) : moduleType(OSCILLATOR_TYPE) {

        moduleNumber = moduleNumber_;
        moduleName = "";

        // Init

        for (int i = 0; i < PARAM_COUNT; i++) {
            parameterValues[i] = 0.0;
            parameterSourceModules[i] = -1;
        }

        for (int i = 0; i < DATA_COUNT; i++) {
            dataValues[i] = 0.0;
        }

        setType(moduleType);
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

        moduleName.clear();
    }

    double calculate(Module **allModules, double delta, double time, long step) {
        // Read parameter values coming from other modules
        for (int i = 0; i < moduleType.parameterCount; i++) {
            int sourceModule = parameterSourceModules[i];
            if (sourceModule >= 0 && sourceModule < MODULE_COUNT) {
                parameterValues[i] = allModules[sourceModule]->moduleValue;
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

    string getName() {
        if (moduleName.length() <= 0) {
            std::stringstream sstm;
            sstm << moduleType.name << " " << moduleNumber;
            moduleName = sstm.str();
        }

        return moduleName;
    }

};



#endif // SOUNDRASP_MODULE_H

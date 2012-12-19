#ifndef SOUNDRASP_DISPLAY_H
#define SOUNDRASP_DISPLAY_H

#include <string>
#include <ctime>
#include <math.h>
#include <SDL/SDL.h>
#include <SDL/SDL_gfxPrimitives.h>

#include "utils.h"
#include "constants.h"
#include "ModuleType.h"
#include "ModuleTypes.h"
#include "Module.h"


double displayScale = 1.0;
int displaySpeed = 1;
bool autoScale = false;


const Uint32 textColor = rgbaColor(1, 1, 0, 1);;


class SequenceDisplay {
  public:
    int x;
    int y;
    int w;
    int h;

    int marginX;
    int marginY;

    int dataSize;
    double *data;
    int dataStart;
    int dataCount;

    double dataScale;
    long updateSkip;
    long stepsToUpdate;

    Uint32 bgColor;
    Uint32 fgColor;
    Uint32 frameColor;

    SequenceDisplay(int x_, int y_, int w_, int h_, double dataScale_ = 1.0, long updateStep_ = 1) {
        x = x_;
        y = y_;
        w = w_;
        h = h_;

        marginX = 2;
        marginY = 2;

        setLineColor(1, 0, 0);
        setBackgroundColor(0.2, 0.2, 0.2);
        frameColor = rgbaColor(0, 0, 0, 1);

        dataSize = w;
        data = new double[dataSize];
        dataStart = 0;
        dataCount = w;

        dataScale = dataScale_;
        updateSkip = updateStep_;
        stepsToUpdate = 0;
    }

    void addDataPoint(double dataPoint) {
        // Check if we should add it
        if (stepsToUpdate <= 0) {
            stepsToUpdate = updateSkip;

            // Get next point to store data at
            dataStart--;
            if (dataStart < 0) dataStart = dataSize - 1;

            // Store data.
            data[dataStart] = dataPoint;

            // Increase amount of available data, if we still have room left in buffer.
            dataCount++;
            if (dataCount > dataSize) dataCount = dataSize;
        }
        else {
            stepsToUpdate--;
        }
    }

    double getDataAt(int dataIndex) {
        if (dataIndex < 0 || dataIndex >= dataCount) return 0.0;
        else return data[(dataStart + dataIndex) % dataSize];
    }

    /** Return a data point based on a relative position between 0 and 1. */
    double getDataAtRelativePos(double relativePosition) {
        if (dataCount <= 0) return 0.0;
        else {
            int index = relativePosition * dataCount;
            if (index < 0) index = 0;
            else if (index >= dataCount) index = dataCount - 1;

            return getDataAt(index);
        }
    }

    void setLineColor(double r, double g, double b) {
        fgColor = rgbaColor(r, g, b, 1.0);
    }

    void setBackgroundColor(double r, double g, double b) {
        bgColor = rgbaColor(r, g, b, 1.0);
    }

    int getYPosAt(int x) {
        //double relativePos = (double)x / w;
        double data = dataScale * getDataAt(x);
        int vh = h - marginY * 2;
        int y = marginY + 0.5 * vh + data * 0.5 * vh;
        return clampInt(y, 0, h);
    }

    void draw(SDL_Surface* surface) {
        // Draw background
        boxColor(surface, x, y, x + w, y + h, bgColor);

        // Draw line
        int prevY = y + getYPosAt(0);
        for (int tx = 1; tx < w; tx++) {
            int currentY = y + getYPosAt(tx);

            aalineColor(surface, x + tx - 1, prevY, x + tx, currentY, fgColor);
            prevY = currentY;
        }

        // Draw background
        rectangleColor(surface, x, y, x + w-1, y + h-1, frameColor);
    }

};


class ModuleDisplay {
  public:

    Module* module;
    int x;
    int y;
    int w;
    int h;

    static const int topMargin = 10;

    SequenceDisplay *sequenceDisplay;

    ModuleDisplay(Module* module_, int x_, int y_, int w_, int h_) {
        module = module_;
        x = x_;
        y = y_;
        w = w_;
        h = h_;

        sequenceDisplay = new SequenceDisplay(x, y + topMargin, w, h - topMargin, 1.0, 1);
    }


    void update() {
        // Update data
        sequenceDisplay->addDataPoint(module->moduleValue);
    }

    void draw(SDL_Surface* surface) {
        // Draw histogram
        sequenceDisplay->draw(surface);

        // Draw title
        stringColor(surface, x, y, module->getName().c_str(), textColor );
    }
};


class Display {
  public:

    Module** modules;
    int moduleCount;

    ModuleDisplay** moduleDisplays;
    int rows;
    int columns;

    Display(Module** modules_, int moduleCount_, int w, int h, int columns_) {
        modules = modules_;
        moduleCount = moduleCount_;

        columns = columns_;

        // Calculate number of rows to use
        int rows = moduleCount / columns;
        if ((moduleCount % columns) > 0) rows++;

        moduleDisplays = new ModuleDisplay*[rows*columns];

        int moduleW = w / columns;
        int moduleH = h / rows;

        int index = 0;
        int x = 0;
        int y = 0;
        for (int row = 0; row < rows; row++) {
            x = 0;
            for (int column = 0; column < columns; column++) {
                if (index < moduleCount) {
                    moduleDisplays[row * columns + column] = new ModuleDisplay(modules[index], x, y, moduleW, moduleH);
                }
                index++;
                x += moduleW;
            }
            y += moduleH;
        }
    }

    void update() {
        for (int i = 0; i < moduleCount; i++) {
            moduleDisplays[i]->update();
        }
    }

    void draw(SDL_Surface* surface) {
        for (int i = 0; i < moduleCount; i++) {
            moduleDisplays[i]->draw(surface);
        }
    }

};

#endif // SOUNDRASP_DISPLAY_H

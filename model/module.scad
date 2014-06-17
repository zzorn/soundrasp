
pcbColor=[0.2, 0.7, 0.1];
epsilon = 0.01;

ioModule();



module ioModule(w = 86, h = 100, depth = 35, wall = 5, lidLen = 65, slopeLen = 50, angle=45) {

    module disp(h = 0) {
        translate([0, lidLen, depth])
            rotate([angle, 0,0])
                translate([0, slopeLen / 2 - 12, -13+h])
                    displayPcb();
    }

    upperDepth = 32;
    upperH = 58;

    pcbD = 7 + wall;

    disp(-2);
       
    translate([0,34,0]) {
        translate([0, 0, pcbD])
           controlPcb();
           
        translate([10,-3,depth])
            valueKnob();    
    }   
       


    plane(w, h, wall, centerY = false, centerZ=false);

    plane(w, lidLen, wall, z = depth - wall, y = 0, centerY = false, centerY = false, centerZ=false);

    difference() {
        plane(w, slopeLen, wall, y = lidLen, z = depth - wall, centerY = false, centerZ=false, angleX=angle);
        disp();
    }

    %plane(w, depth*2, wall, y = h, centerY = false, centerZ=false, angleX = 90);

/*
    %box(w, h, depth, wall, centerXY=true, zWallEnd=true);

    translate([0,h/2-upperH/2,depth])
        translate([0,-upperH/2,0])
            rotate(angle, [1,0,0])
                translate([0,upperH/2,-upperDepth])
                    %box(w, upperH, upperDepth, wall, centerXY=true, zWallEnd=true);
*/
}

/*
module box(xSize, ySize, zSize, wallThickness = 1, center=false, centerXY = false, xWallStart=true, xWallEnd=true,yWallStart=true,yWallEnd=true,zWallStart=true,zWallEnd=true) {

    halfWT = wallThickness/2;

    translate([(center || centerXY)? 0 : xSize/2, 
               (center || centerXY)? 0 : ySize/2, 
               center? 0 : zSize/2]) {
               
        if (xWallStart) plane(-xSize/2+halfWT,0,0, wallThickness, ySize, zSize);
        if (xWallEnd)   plane(xSize/2-halfWT,0,0,  wallThickness, ySize, zSize);
        if (yWallStart) plane(0, -ySize/2+halfWT,0, xSize, wallThickness, zSize);
        if (yWallEnd)   plane(0, ySize/2-halfWT,0,  xSize, wallThickness, zSize);
        if (zWallStart) plane(0,0,-zSize/2+halfWT, xSize, ySize, wallThickness);
        if (zWallEnd)   plane(0,0, zSize/2-halfWT, xSize, ySize, wallThickness);
    }


}
*/

module plane(sizeX, sizeY, sizeZ, x = 0, y = 0, z = 0, centerX=true, centerY=true, centerZ=true, angleX = 0, angleY = 0, angleZ = 0) {
    translate([x, y, z])
        rotate([angleX, angleY, angleZ])         
            translate([centerX ? -sizeX/2 : 0, 
                       centerY ? -sizeY/2 : 0, 
                       centerZ ? -sizeZ/2 : 0])
                cube([sizeX, sizeY, sizeZ]);
}

module displayPcb() {

    translate([0, 7+5, 7])
        charDisplay16x2();

}


module controlPcb(pcbThickness = 1.0) {

    color(pcbColor)
        plane(80, 40, pcbThickness, 0,0,pcbThickness/2);

    translate([-25,10,pcbThickness]) {
        quadEncoder();
        translate([0,0,23])
            parameterKnob();    
    }

    translate([25,10,-6.5])
        quadEncoder();
        
}


module valueKnob(diam=37.5, h = 10, depth = 15, baseH = 7, $fn=60) {
    tubeR = diam/2 - 5;
    tubeInnerR = tubeR - 4;
    innerTubeInnerR = tubeInnerR - 3;

    glassH = 3;
    glueEdgeR = 2;
    knobH = 2;
    

    translate([0,0,knobH])
        difference() {
            knob(diam, h);

            color([0.8, 0.8, 0.8]) {
                translate([0,0,h-glassH])
                    cylinder(r=tubeR, h=glassH+epsilon);
                translate([0,0,-epsilon])
                    cylinder(r1=tubeInnerR, r2 = tubeR-glueEdgeR, h=h-glassH+2*epsilon);
            }
        }
    
    // Diffusor
    color([0.4, 0.4, 0.4, 0.7]) 
        translate([0,0,h+knobH-glassH])
            %cylinder(r=tubeR, h=glassH+epsilon);

    // Turn tube
    color([1,1,1])
        translate([0,0,-depth])
            tube(tubeR, tubeInnerR, depth+knobH+epsilon);
            
    valueKnobBase(depth, baseH, tubeInnerR, innerTubeInnerR);     
}

module valueKnobBase(depth, baseH, tubeInnerR, innerTubeInnerR) {
    fasteningH = 3;
    fasteningX = tubeInnerR*2;
    fasteningY = tubeInnerR*2 + 16;
    fasteningHoleDiam = 3;
    fasteningHoleEdgeDistance = 1.5;

    // Base
    color([1,1,1])
        translate([0,0,-depth-baseH])
            difference() {
                union() {
                // Fastening
                        difference() {
                            translate([0,0,fasteningH/2-epsilon])
                                cube([fasteningX, fasteningY, fasteningH+epsilon*2], center=true);       
                            for (x = [-1, 1], y = [-1,1]) 
                                translate([x*(fasteningX/2-fasteningHoleDiam/2-fasteningHoleEdgeDistance),
                                           y*(fasteningY/2-fasteningHoleDiam/2-fasteningHoleEdgeDistance),
                                           -epsilon])
                                    cylinder(r=fasteningHoleDiam/2, h=fasteningH+epsilon*3); 
                        }
                    tube(tubeInnerR, innerTubeInnerR, baseH + depth + epsilon);
                }
                translate([0,0,(baseH + depth)/2-epsilon])
                    cylinder(r1=innerTubeInnerR, r2=tubeInnerR-2, (baseH + depth)/2 + epsilon*3);                    
                translate([0,0,-epsilon])
                    cylinder(r=innerTubeInnerR, baseH + depth + epsilon*3);                    
            }
}

module tube(r, innerR, h) {
    difference() {
        cylinder(r = r, h = h);
        translate([0,0,-epsilon])
            cylinder(r = innerR, h = h+epsilon*2);
    }
}

module parameterKnob(diam=15, h = 10) {
    knob(diam, h);
}

module knob(diam=15, h = 10) {
    r = diam/2;
    color([0.2, 0.2, 0.2])
        cylinder(r1 = r, r2 = 0.85*r, h = h, $fn=60);
}


module charDisplay(pcb = [80.0, 36.0, 1.6],
                   bottomBlock = [70.7, 23.8, 12.0-7.0-1.6], 
                   displayFrame = [70.7, 23.8, 7.0],
                   displayArea = [64.5, 14.5, 0.01],
                   backlight = [3.5, 16.9, 2.75],
                   mountingHoleDistanceX = 75.0,
                   mountingHoleDistanceY = 31.0,
                   mountingHoleDiameter = 2.9) {


    WIDTH = 0;
    HEIGHT = 1;
    DEPTH = 2;    
    
    // Base
    difference() {
        color(pcbColor)
            translate([-pcb[WIDTH]/2, -pcb[HEIGHT]/2, 0])
                cube(pcb);
            
        // Mounting holes    
        for (x = [-1, 1], y = [-1, 1]) {
            translate([x * mountingHoleDistanceX/2, 
                       y * mountingHoleDistanceY/2, -0.01])
                cylinder(h = pcb[DEPTH] + 0.02, r = mountingHoleDiameter/2, $fn=20);
        }
    }
        
    // Backlight
    color([0.7, 0.7, 0.7])
       translate([displayFrame[WIDTH]/2, -backlight[HEIGHT]/2, pcb[DEPTH]])
           cube(backlight);        

    // Bottom things
    color([0.1, 0.1, 0.1])
       translate([-bottomBlock[WIDTH]/2, -bottomBlock[HEIGHT]/2, -bottomBlock[DEPTH]])
           cube(bottomBlock);        
    
    difference() {
        // Display block
        color([0.1, 0.1, 0.1])
            translate([-displayFrame[WIDTH]/2, -displayFrame[HEIGHT]/2, pcb[DEPTH]])
                cube(displayFrame);
                
        // Display area
        color([0.2, 0.2, 0.9])
            translate([-displayArea[WIDTH]/2, -displayArea[HEIGHT]/2, pcb[DEPTH]+displayFrame[DEPTH]-displayArea[DEPTH]])
                cube(displayArea + [0,0,0.1]);
    }

}

module charDisplay8x2() {

    charDisplay(pcb = [58.36, 32.58, 1.6],
                   bottomBlock = [33.5, 28.0, 3.5], 
                   displayFrame = [44.65, 26.2, 9.0],
                   displayArea = [37.9, 16.0, 0.01],
                   backlight = [6.25, 16.9, 2.75],
                   mountingHoleDistanceX = 53.5,
                   mountingHoleDistanceY = 27.5,
                   mountingHoleDiameter = 2.75);    

}

module charDisplay16x2() {

    charDisplay(pcb = [80.0, 36.0, 1.6],
                   bottomBlock = [70.7, 23.8, 12.0-7.0-1.6], 
                   displayFrame = [70.7, 23.8, 7.0],
                   displayArea = [64.5, 14.5, 0.01],
                   backlight = [3.5, 16.9, 2.75],
                   mountingHoleDistanceX = 75.0,
                   mountingHoleDistanceY = 31.0,
                   mountingHoleDiameter = 2.9);    


}


module quadEncoder() {
    epsilon = 0.01;
    w = 14;
    h = 12;
    d = 6.5;
    bdiam = 7;
    bh = 14;
    diam = 6;
    th = 26.5;
    
    color([0.1, 0.3, 0.5])
       translate([-w/2, -h/2, 0])    
           cube([w, h, d]);

    translate([0,0,d-epsilon]) {
        color([0.7, 0.7, 0.7])
            cylinder(r=bdiam/2, h = bh-d+epsilon, $fn=10);   
        
        color([0.7, 0.7, 0.7])
            difference() {
                cylinder(r=diam/2, h = th-d+epsilon, $fn=20);    
                translate([diam-4.5, -5, th-10-d+epsilon])
                cube([5, 10, 12]);
            } 
    }

}


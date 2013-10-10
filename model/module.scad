
pcbColor=[0.2, 0.7, 0.1];
epsilon = 0.01;

ioModule();

module ioModule(w = 86, h = 100, depth = 32, wall = 2, angle=45) {

    upperDepth = 32;
    upperH = 58;

    pcbD = 7+wall;

    translate([0, -2, pcbD+8])
        rotate(angle, [1, 0,0])
            translate([0, 25, 0])
                displayPcb();
       
    translate([0, -22, pcbD])
       controlPcb();
       
    translate([10,-25,depth])
        valueKnob();    
       

    %box(w, h, depth, wall, centerXY=true, zWallEnd=true);

    translate([0,h/2-upperH/2,depth])
        translate([0,-upperH/2,0])
            rotate(angle, [1,0,0])
                translate([0,upperH/2,-upperDepth])
                    %box(w, upperH, upperDepth, wall, centerXY=true, zWallEnd=true);

}

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

module plane(centerX, centerY, centerZ, sizeX, sizeY, sizeZ) {
    translate([centerX - sizeX/2, 
               centerY - sizeY/2, 
               centerZ - sizeZ/2]) 
        cube([sizeX, sizeY, sizeZ]);
}

module displayPcb() {

    headerH = 5;

    color(pcbColor)
        plane(0,0,0.5, 80, 60, 1);
    translate([0, 7+5, headerH])
        charDisplay8();

}


module controlPcb(pcbThickness = 1.0) {


    color(pcbColor)
        plane(0,0,pcbThickness/2, 80, 40, pcbThickness);

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


module charDisplay8() {

    bdw = 33.5;
    bdh = 28.0;
    bdd = 3.5;

    bw = 58.36;
    bh = 32.58;
    bd = 1.6;
    
    dw = 44.65;
    dh = 26.2;
    dd = 9.0;
    
    aw = 37.9;
    ah = 16.0;
    ad = 0.75;
    
    bld = 2.75;
    blh = 16.9;
    blw = 6.25;
    
    mhxd = 53.5;
    mhyd = 27.5;
    mhdiam = 2.75;
    
    
    // Base
    difference() {
        color(pcbColor)
            translate([-bw/2, -bh/2, 0])
                cube([bw, bh, bd]);
            
        // Mounting holes    
        for (x = [-1, 1], y = [-1, 1]) {
            translate([x * mhxd/2, y * mhyd/2, -0.01])
                cylinder(h = bd + 0.02, r = mhdiam/2, $fn=15);
        }
    }
        
    // Backlight
    color([0.7, 0.7, 0.7])
       translate([dw/2, -blh/2, bd])
           cube([blw, blh, bld]);        

    // Bottom things
    color([0.1, 0.1, 0.1])
       translate([-bdw/2, -bdh/2, -bdd])
           cube([bdw, bdh, bdd]);        
    
    difference() {
        // Display block
        color([0.1, 0.1, 0.1])
            translate([-dw/2, -dh/2, bd])
                cube([dw, dh, dd]);
                
        // Display area
        color([0.2, 0.2, 0.9])
            translate([-aw/2, -ah/2, bd+dd-ad])
                cube([aw, ah, ad+0.01]);
    }

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


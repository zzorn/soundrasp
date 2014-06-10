package org.soundrasp.modules;

import org.flowutils.MathUtils;
import org.soundrasp.model.ModuleBase;
import org.soundrasp.model.Param;

/**
 * Updates the value as if it was a particle with mass, driving towards a target, with some drag, and a braking distance where it starts to slow down.
 */
public class InertiaModule extends ModuleBase {

    public final Param target = param("Target", "Target value that the module aims for", 0, -10.0, 10.0);
    public final Param attraction = param("Attraction", "Force that the value moves towards the target value.", 1, 0, 1.0);
    public final Param inertia = param("Inertia", "How slowly the velocity changes (how 'massive' the moving object is)", 1, 0, 1.0);
    public final Param drag = param("Drag", "How much resistance there is to movement", 0.1, 0, 1.0);
    public final Param brakingDistance = param("Braking Distance", "At what distance from the target value to start reducing the attraction force", 0.1, 0, 10.0);

    private double velocity;
    private double value;

    public InertiaModule() {
        this(1);
    }

    public InertiaModule(double attraction) {
        this(attraction, 1);
    }

    public InertiaModule(double attraction, double inertia) {
        this(attraction, inertia, 0.1);
    }

    public InertiaModule(double attraction, double inertia, double drag) {
        this(attraction, inertia, drag, 0.1);
    }

    public InertiaModule(double attraction, double inertia, double drag, double brakingDistance) {
        super("Inertia");

        this.attraction.set(attraction);
        this.inertia.set(inertia);
        this.drag.set(drag);
        this.brakingDistance.set(brakingDistance);
    }


    @Override
    protected double calculateValue(double durationSeconds, long sampleCounter) {
        final double targetValue = target.get();

        // Determine braking amount
        final double distanceToTarget = Math.abs(value - targetValue);
        double brakingFactor = MathUtils.mapAndClamp(distanceToTarget, brakingDistance.get(), 0, 1, 0);

        // Calculate attraction force
        double attractionForce = attraction.get() * brakingFactor;
        if (value > targetValue) attractionForce *= -1;

        // Calculate drag force (in opposite direction of velocity
        double frictionForce = -Math.signum(velocity) * velocity * velocity * drag.get();

        // Update velocity
        velocity += durationSeconds * (attractionForce + frictionForce) / Math.max(inertia.get(), 0.0001); // Avoid divide by zero which puts a value to NaN

        // Update value
        value += velocity;

        return value;
    }
}

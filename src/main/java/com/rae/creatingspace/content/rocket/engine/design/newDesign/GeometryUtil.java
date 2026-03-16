package com.rae.creatingspace.content.rocket.engine.design.newDesign;

import net.minecraft.world.phys.Vec2;

public class GeometryUtil {
    public static Vec2 lineIntersection(
            double x1, double y1, double m1,
            double x2, double y2, double m2
    ) {
        double x = (y2 - y1 + m1 * x1 - m2 * x2) / (m1 - m2);
        double y = y1 + m1 * (x - x1);
        return new Vec2((float) x, (float) y);
    }
}

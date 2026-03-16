package com.rae.creatingspace.content.rocket.engine.design.newDesign;

import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class AerospikeGeometry {
    /**
     * Generates the aerospike expansion contour using a Prandtl-Meyer expansion fan construction.
     *
     *
     * <p>The algorithm starts from the throat point and iteratively
     * computes intersection points between:
     * <ul>
     *   <li>a characteristic line originating from the external lip</li>
     *   <li>a streamline originating from the previous point</li>
     * </ul>
     *
     * <p>The Mach number is increased incrementally and the corresponding
     * Prandtl–Meyer expansion angle is used to update the local flow angle.
     * Each iteration produces one point of the aerospike contour.</p>
     *
     * <p>The iteration stops when either:
     * <ul>
     *   <li>the flow angle reaches the specified {@code finalAngle}</li>
     *   <li>the maximum number of iterations is reached</li>
     * </ul>
     *
     * <p>The returned list represents the spike contour starting from
     * the throat and progressing downstream.</p>
     *
     * @param Rt         throat radius (distance from axis to throat wall)
     * @param Ac         throat critical area used to compute the external lip position
     * @param thetaT     initial wall angle at the throat (radians)
     * @param finalAngle final target flow angle where the expansion stops (radians)
     * @param machStep   Mach increment used between iterations (controls resolution)
     * @param maxIter    maximum number of expansion steps
     * @return a list of {@link Vec2} points representing the aerospike contour
     */
    public static List<Vec2> generateExpansion(
            double Rt,
            double Ac,
            double thetaT,
            double finalAngle,
            double machStep,
            int maxIter
    ) {

        double Xt = 0;

        double Re = Math.sqrt(Ac * Math.sin(thetaT + Math.PI / 2) / Math.PI + Rt * Rt);
        double Xe = Math.cos(thetaT + Math.PI / 2) * (Re - Rt);

        double currentMach = 1;
        double currentAngle = thetaT;

        Vec2 currentPoint = new Vec2((float) Xt, (float) Rt);

        List<Vec2> points = new ArrayList<>();
        points.add(currentPoint);

        for (int i = 0; i < maxIter; i++) {

            double nextMach = currentMach + machStep;

            double mu = Math.asin(1.0 / nextMach);
            double nu = currentMach > 1
                    ? PrandtlMeyer.prandtlMeyer(currentMach, 1.4)
                    : 0;

            Vec2 nextPoint = GeometryUtil.lineIntersection(
                    Xe, Re, Math.tan(currentAngle - mu),
                    currentPoint.x, currentPoint.y, Math.tan(thetaT + nu)
            );

            if (thetaT + nu > finalAngle)
                break;

            points.add(nextPoint);

            currentPoint = nextPoint;
            currentMach = nextMach;
            currentAngle = thetaT + nu;
        }

        return points;
    }

}
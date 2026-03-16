package com.rae.creatingspace.content.rocket.engine.design.newDesign;


/**
 * Utility class implementing Prandtl–Meyer and isentropic flow relations
 * for compressible perfect-gas dynamics.
 *
 * <p>The methods provide dimensionless relations commonly used in the
 * analysis and design of supersonic flows, including:</p>
 * <ul>
 *   <li>Prandtl–Meyer expansion angle</li>
 *   <li>Area ratio (A/A*)</li>
 *   <li>Temperature, pressure, and density ratios</li>
 *   <li>Velocity ratio relative to stagnation conditions</li>
 * </ul>
 *
 * <p>A numerical solver is also provided to compute the supersonic Mach
 * number corresponding to a given area ratio.</p>
 *
 * <p>All angles are expressed in radians.</p>
 */
public class PrandtlMeyer {

    /**
     * Prandtl–Meyer expansion angle (radians)
     */
    public static double prandtlMeyer(double M, double gamma) {

        double term1 = Math.sqrt((gamma + 1) / (gamma - 1));
        double term2 = Math.atan(Math.sqrt((gamma - 1) * (M * M - 1) / (gamma + 1)));
        double term3 = Math.atan(Math.sqrt(M * M - 1));

        return term1 * term2 - term3;
    }

    /**
     * Isentropic pressure ratio P / P0
     */
    public static double pressureRatio(double M, double gamma) {
        return Math.pow(1.0 + (gamma - 1) / 2.0 * M * M, -gamma / (gamma - 1));
    }

    /**
     * Isentropic density ratio ρ / ρ0
     */
    public static double densityRatio(double M, double gamma) {
        return Math.pow(1.0 + (gamma - 1) / 2.0 * M * M, -1.0 / (gamma - 1));
    }

    /**
     * Velocity ratio V / a0
     * (a0 = stagnation speed of sound)
     */
    public static double velocityRatio(double M, double gamma) {
        return M * Math.sqrt(temperatureRatio(M, gamma));
    }

    /**
     * Isentropic temperature ratio T / T0
     */
    public static double temperatureRatio(double M, double gamma) {
        return 1.0 / (1.0 + (gamma - 1) / 2.0 * M * M);
    }

    /**
     * Computes Mach number from area ratio A/A*
     * using a bisection solver (supersonic branch).
     *
     * @param areaRatio A/A*
     * @param gamma     ratio of specific heats
     * @return Mach number
     */
    public static double machFromAreaRatio(double areaRatio, double gamma) {

        double low = 1.0;
        double high = 20.0;
        double tol = 1e-6;
        double M = 2.0;

        for (int i = 0; i < 100; i++) {

            M = 0.5 * (low + high);
            double f = areaRatio(M, gamma);

            if (Math.abs(f - areaRatio) < tol)
                break;

            if (f > areaRatio)
                high = M;
            else
                low = M;
        }

        return M;
    }

    /**
     * Area ratio A / A*
     */
    public static double areaRatio(double M, double gamma) {

        double term = (2.0 / (gamma + 1.0)) *
                (1.0 + (gamma - 1.0) / 2.0 * M * M);

        return (1.0 / M) *
                Math.pow(term, (gamma + 1.0) / (2.0 * (gamma - 1.0)));
    }
}
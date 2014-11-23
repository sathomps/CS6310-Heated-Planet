package PlanetSim.common.util;

import static PlanetSim.common.util.JulianCalendarUtil.julianDay;
import PlanetSim.common.SimulationSettings;
import PlanetSim.model.PlanetPosition;

public final class PlanetPositionUtil
{

    /** Period, tropical years */
    private static double T       = 1.00004;

    /** Longitude in epoch, degrees */
    private static double epsilon = 98.833540;

    /** Longitude of perihelion, degrees */
    private static double omega   = 102.596403;

    /** Semimajor axis, a.u. */
    private static double a       = 1.000000;

    /** Inclination, degrees */
    private static double i       = 0;

    /** Longitude of ascending node, degrees */
    private static double OMEGA   = 0;

    /** Angle diameter on 1 a.u., seconds of arc */
    private static double theta0  = 0;

    private PlanetPositionUtil()
    {
    }

    public static void calculatePlanetPosition(final SimulationSettings settings)
    {
        final PlanetPosition planetPosition = new PlanetPosition();

        final double oe = settings.getPlanetsOrbitalEccentricity();

        final double D = julianDay(settings.getSimulationTimestamp());

        double M = ((((360.0 / 365.2422) * D) / T) + epsilon) - omega;
        M = Math2Util.to360(M);

        double v = M + ((360.0 / Math.PI) * oe * Math.sin(Math.toRadians(M)));
        v = Math2Util.to360(v);

        double l = v + omega;
        l = Math2Util.to360(l);

        final double r = (a * (1 - (oe * oe))) / (1 + (oe * Math.cos(Math.toRadians(v))));

        final double angle_l_OMEGA = Math.toRadians(l - OMEGA);
        final double angle_i = Math.toRadians(i);

        final double psi = Math.toDegrees(Math2Util.asin(Math.sin(angle_l_OMEGA) * Math.sin(angle_i)));

        final double y = Math.sin(angle_l_OMEGA) * Math.cos(angle_i);
        final double x = Math.cos(angle_l_OMEGA);

        final double l_ = Math.toDegrees(Math2Util.atan2(y, x)) + OMEGA;

        final double r_ = r * Math.cos(Math.toRadians(psi));

        planetPosition.setMeanAnomaly(M);
        planetPosition.setTrueAnomaly(v);
        planetPosition.setHelioLongitude(l);
        planetPosition.setHelioLatitude(psi);
        planetPosition.setProjectedHelioLongitude(l_);
        planetPosition.setRadiusVector(r);
        planetPosition.setProjectedRadiusVector(r_);

        settings.setPlanetPosition(planetPosition);
    }
}

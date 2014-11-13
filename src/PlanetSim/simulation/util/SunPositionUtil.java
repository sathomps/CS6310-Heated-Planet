package PlanetSim.simulation.util;

import static PlanetSim.common.util.JulianCalendarUtil.julianDay;

import java.util.Date;

import PlanetSim.model.GridCell;
import PlanetSim.model.SunPosition;

public class SunPositionUtil
{
    private static final double DARK           = Double.NaN;

    /**
     * http://en.wikipedia.org/wiki/Twilight#Civil_twilight
     * {@linkplain #getElevation Elevation angle} of civil twilight, in degrees.
     * Civil twilight is the time of morning or evening when the sun is 6° below
     * the horizon (solar elevation angle of -6°).
     */
    private static final double CIVIL_TWILIGHT = -6;

    /**
     * Calculate the equation of center for the sun. This value is a correction
     * to add to the geometric mean longitude in order to get the "true"
     * longitude of the sun.
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Equation of center in degrees.
     */
    private static double sunEquationOfCenter(final double t)
    {
        final double m = Math.toRadians(sunGeometricMeanAnomaly(t));
        return (Math.sin(1 * m) * (1.914602 - (t * (0.004817 + (0.000014 * t))))) + (Math.sin(2 * m) * (0.019993 - (t * (0.000101))))
                + (Math.sin(3 * m) * (0.000289));
    }

    /**
     * Calculate the Geometric Mean Longitude of the Sun. This value is close to
     * 0° at the spring equinox, 90° at the summer solstice, 180° at the autumn
     * equinox and 270° at the winter solstice.
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Geometric Mean Longitude of the Sun in degrees, in the range 0°
     *         (inclusive) to 360° (exclusive).
     */
    private static double sunGeometricMeanLongitude(final double t)
    {
        double L0 = 280.46646 + (t * (36000.76983 + (0.0003032 * t)));
        L0 = L0 - (360 * Math.floor(L0 / 360));
        return L0;
    }

    /**
     * Calculate the true longitude of the sun. This the geometric mean
     * longitude plus a correction factor ("equation of center" for the sun).
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Sun's true longitude in degrees.
     */
    private static double sunTrueLongitude(final double t)
    {
        return sunGeometricMeanLongitude(t) + sunEquationOfCenter(t);
    }

    /**
     * Calculate the apparent longitude of the sun.
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Sun's apparent longitude in degrees.
     */
    private static double sunApparentLongitude(final double t)
    {
        final double omega = Math.toRadians(125.04 - (1934.136 * t));
        return sunTrueLongitude(t) - 0.00569 - (0.00478 * Math.sin(omega));
    }

    /**
     * Calculate the Geometric Mean Anomaly of the Sun.
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Geometric Mean Anomaly of the Sun in degrees.
     */
    private static double sunGeometricMeanAnomaly(final double t)
    {
        return 357.52911 + (t * (35999.05029 - (0.0001537 * t)));
    }

    /**
     * Calculate the true anamoly of the sun.
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Sun's true anamoly in degrees.
     */
    private static double sunTrueAnomaly(final double t)
    {
        return sunGeometricMeanAnomaly(t) + sunEquationOfCenter(t);
    }

    /**
     * Calculate the eccentricity of the p orbit. This is the ratio
     * {@code (a-b)/a} where <var>a</var> is the semi-major axis length and
     * <var>b</var> is the semi-minor axis length. Value is 0 for a circular
     * orbit.
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return The unitless eccentricity.
     */
    private static double eccentricityEarthOrbit(final double t)
    {
        return 0.016708634 - (t * (0.000042037 + (0.0000001267 * t)));
    }

    /**
     * Calculate the distance to the sun in Astronomical Units (AU).
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Sun radius vector in AUs.
     */
    public static double sunRadiusVector(final double t)
    {
        final double v = Math.toRadians(sunTrueAnomaly(t));
        final double e = eccentricityEarthOrbit(t);
        return (1.000001018 * (1 - (e * e))) / (1 + (e * Math.cos(v)));
    }

    /**
     * Calculate the mean obliquity of the ecliptic.
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Mean obliquity in degrees.
     */
    private static double meanObliquityOfEcliptic(final double t)
    {
        final double seconds = 21.448 - (t * (46.8150 + (t * (0.00059 - (t * (0.001813))))));
        return 23.0 + ((26.0 + (seconds / 60.0)) / 60.0);
    }

    /**
     * Calculate the corrected obliquity of the ecliptic.
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Corrected obliquity in degrees.
     */
    private static double obliquityCorrected(final double t)
    {
        final double e0 = meanObliquityOfEcliptic(t);
        final double omega = Math.toRadians(125.04 - (1934.136 * t));
        return e0 + (0.00256 * Math.cos(omega));
    }

    /**
     * Calculate the right ascension of the sun. Similar to the angular system
     * used to define latitude and longitude on Earth's surface, right ascension
     * is roughly analogous to longitude, and defines an angular offset from the
     * meridian of the vernal equinox.
     *
     * <P align="center">
     * <img src="doc-files/CelestialSphere.png">
     * </P>
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Sun's right ascension in degrees.
     */
    public static double sunRightAscension(final double t)
    {
        final double e = Math.toRadians(obliquityCorrected(t));
        final double b = Math.toRadians(sunApparentLongitude(t));
        final double y = Math.sin(b) * Math.cos(e);
        final double x = Math.cos(b);
        final double alpha = Math.atan2(y, x);
        return Math.toDegrees(alpha);
    }

    /**
     * Calculate the declination of the sun. Declination is analogous to
     * latitude on Earth's surface, and measures an angular displacement north
     * or south from the projection of Earth's equator on the celestial sphere
     * to the location of a celestial body.
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Sun's declination in degrees.
     */
    private static double sunDeclination(final double t)
    {
        final double e = Math.toRadians(obliquityCorrected(t));
        final double b = Math.toRadians(sunApparentLongitude(t));
        final double sint = Math.sin(e) * Math.sin(b);
        final double theta = Math.asin(sint);
        return Math.toDegrees(theta);
    }

    /**
     * Calculate the Universal Coordinated Time (UTC) of solar noon for the
     * given day at the given location on earth.
     *
     * @param lon
     *            longitude of observer in degrees.
     * @param eqTime
     *            Equation of time.
     * @return Time in minutes from beginnning of day in UTC.
     */
    private static double solarNoonTime(final double lon, final double eqTime)
    {
        return (720.0 + (lon * 4.0)) - eqTime;
    }

    /**
     * Calculate the difference between true solar time and mean. The "equation
     * of time" is a term accounting for changes in the time of solar noon for a
     * given location over the course of a year. Earth's elliptical orbit and
     * Kepler's law of equal areas in equal times are the culprits behind this
     * phenomenon. See the <A
     * HREF="http://www.analemma.com/Pages/framesPage.html">Analemma page</A>.
     * Below is a plot of the equation of time versus the day of the year.
     *
     * <P align="center">
     * <img src="doc-files/EquationOfTime.png">
     * </P>
     *
     * @param t
     *            number of Julian centuries since J2000.
     * @return Equation of time in minutes of time.
     */
    private static double equationOfTime(final double t)
    {
        final double eps = Math.toRadians(obliquityCorrected(t));
        final double l0 = Math.toRadians(sunGeometricMeanLongitude(t));
        final double m = Math.toRadians(sunGeometricMeanAnomaly(t));
        final double e = eccentricityEarthOrbit(t);
        double y = Math.tan(eps / 2);
        y *= y;

        final double sin2l0 = Math.sin(2 * l0);
        final double cos2l0 = Math.cos(2 * l0);
        final double sin4l0 = Math.sin(4 * l0);
        final double sin1m = Math.sin(m);
        final double sin2m = Math.sin(2 * m);

        final double etime = (((y * sin2l0) - (2 * e * sin1m)) + (4 * e * y * sin1m * cos2l0)) - (0.5 * y * y * sin4l0) - (1.25 * e * e * sin2m);

        return Math.toDegrees(etime) * 4.0;
    }

    /**
     * Computes the refraction correction angle. The effects of the atmosphere
     * vary with atmospheric pressure, humidity and other variables. Therefore
     * the calculation is approximate. Errors can be expected to increase the
     * further away you are from the equator, because the sun rises and sets at
     * a very shallow angle. Small variations in the atmosphere can have a
     * larger effect.
     *
     * @param zenith
     *            The sun zenith angle in degrees.
     * @return The refraction correction in degrees.
     */
    private static double refractionCorrection(final double zenith)
    {
        final double exoatmElevation = 90 - zenith;
        if (exoatmElevation > 85)
        {
            return 0;
        }
        final double refractionCorrection; // In minute of degrees
        final double te = Math.tan(Math.toRadians(exoatmElevation));
        if (exoatmElevation > 5.0)
        {
            refractionCorrection = ((58.1 / te) - (0.07 / (te * te * te))) + (0.000086 / (te * te * te * te * te));
        }
        else
        {
            if (exoatmElevation > -0.575)
            {
                refractionCorrection = 1735.0 + (exoatmElevation * (-518.2 + (exoatmElevation * (103.4 + (exoatmElevation * (-12.79 + (exoatmElevation * 0.711)))))));
            }
            else
            {
                refractionCorrection = -20.774 / te;
            }
        }
        return refractionCorrection / 3600;
    }

    /**
     * Calculates solar position for the current date, time and location.
     * Results are reported in azimuth and elevation in degrees.
     */
    public static SunPosition compute(final GridCell gridCell, final long time)
    {
        final SunPosition sunPosition = new SunPosition();
        double latitude = gridCell.getLatitudeTop();
        double longitude = gridCell.getLongitudeRight();

        // NOAA convention use positive longitude west, and negative east.
        // Inverse the sign, in order to be closer to OpenGIS convention.
        longitude = -longitude;

        // Compute: 1) Julian day (days ellapsed since January 1, 4723 BC at
        // 12:00 GMT).
        // 2) Time as the centuries ellapsed since January 1, 2000 at 12:00 GMT.
        final double julianDay = julianDay(time);

        double solarDec = sunDeclination(time);
        final double eqTime = equationOfTime(time);

        // Formula below use longitude in degrees. Steps are:
        // 1) Extract the time part of the date, in minutes.
        // 2) Apply a correction for longitude and equation of time.
        // 3) Clamp in a 24 hours range (24 hours == 1440 minutes).
        double trueSolarTime = ((julianDay + 0.5) - Math.floor(julianDay + 0.5)) * 1440;
        trueSolarTime += (eqTime - (4.0 * longitude)); // Correction in minutes.
        trueSolarTime -= 1440 * Math.floor(trueSolarTime / 1440);

        // Convert all angles to radians. From this point until
        // the end of this method, local variables are always in
        // radians. Output variables ('azimuth' and 'elevation')
        // will still computed in degrees.
        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        solarDec = Math.toRadians(solarDec);

        double csz = (Math.sin(latitude) * Math.sin(solarDec))
                + (Math.cos(latitude) * Math.cos(solarDec) * Math.cos(Math.toRadians((trueSolarTime / 4) - 180)));
        if (csz > +1)
        {
            csz = +1;
        }
        if (csz < -1)
        {
            csz = -1;
        }

        final double zenith = Math.acos(csz);
        final double azDenom = Math.cos(latitude) * Math.sin(zenith);

        // ////////////////////////////////////////
        // // Compute azimuth in degrees ////
        // ////////////////////////////////////////
        double azimuth = 0.0;
        if (Math.abs(azDenom) > 0.001)
        {
            double azRad = ((Math.sin(latitude) * Math.cos(zenith)) - Math.sin(solarDec)) / azDenom;
            if (azRad > +1)
            {
                azRad = +1;
            }
            if (azRad < -1)
            {
                azRad = -1;
            }

            azimuth = 180 - Math.toDegrees(Math.acos(azRad));
            if (trueSolarTime > 720)
            { // 720 minutes == 12 hours
                azimuth = -azimuth;
            }
        }
        else
        {
            azimuth = (latitude > 0) ? 180 : 0;
        }
        azimuth -= 360 * Math.floor(azimuth / 360);

        // //////////////////////////////////////////
        // // Compute elevation in degrees ////
        // //////////////////////////////////////////
        double elevation = 0.0;

        final double refractionCorrection = refractionCorrection(Math.toDegrees(zenith));
        final double solarZen = Math.toDegrees(zenith) - refractionCorrection;

        elevation = 90 - solarZen;
        if (elevation < CIVIL_TWILIGHT)
        {
            // do not report azimuth & elevation after twilight
            azimuth = DARK;
            elevation = DARK;
        }

        sunPosition.setAzimuth(azimuth);
        sunPosition.setElevation(elevation);
        sunPosition.setLatitude(latitude);
        sunPosition.setLongitude(longitude);

        return sunPosition;
    }

    public static void main(final String[] args)
    {
        final GridCell gridCell = new GridCell();
        gridCell.setLatitudeTop(25);
        gridCell.setLongitudeRight(50);

        final SunPosition sunPosition = SunPositionUtil.compute(gridCell, new Date().getTime());

        System.out.println("getAzimuth: " + sunPosition.getAzimuth());
        System.out.println("getElevation: " + sunPosition.getElevation());
        System.out.println("getLatitude: " + sunPosition.getLatitude());
        System.out.println("getLongitude: " + sunPosition.getLongitude());
    }
}

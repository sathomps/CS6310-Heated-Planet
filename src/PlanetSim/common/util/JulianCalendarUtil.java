package PlanetSim.common.util;

import java.util.Date;

public final class JulianCalendarUtil
{
    public static double MILLIS_IN_DAY   = 1000 * 60 * 60 * 24;
    public static double JULIAN_DAY_1970 = 2451544.5 - 10957;

    private JulianCalendarUtil()
    {
    }

    public static double julianDay(final Date time)
    {
        return julianDay(time.getTime());
    }

    /**
     * Computes the {@linkplain #julianDay(Date) julian day}.
     *
     * @param time
     *            The date in milliseconds ellapsed since January 1st, 1970.
     */
    public static double julianDay(final long time)
    {
        return (time / MILLIS_IN_DAY) + JULIAN_DAY_1970;
    }

    private static double julianCentury(final Date time)
    {
        return ((time.getTime() / MILLIS_IN_DAY) + (JULIAN_DAY_1970 - 2451545.0)) / 36525;
    }

    public static double tropicalYearLength(final Date time)
    {
        final double T = julianCentury(time);
        return 365.2421896698 + (T * (-0.00000615359 + (T * (-7.29E-10 + (T * (2.64E-10))))));
    }

    public static double synodicMonthLength(final Date time)
    {
        final double T = julianCentury(time);
        return 29.5305888531 + (T * (0.00000021621 + (T * (-3.64E-10))));
    }
}

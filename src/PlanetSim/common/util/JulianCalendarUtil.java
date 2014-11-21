package PlanetSim.common.util;

import java.util.Calendar;
import java.util.Date;

public final class JulianCalendarUtil
{
    public static double MILLIS_IN_DAY   = 1000 * 60 * 60 * 24;
    public static double JULIAN_DAY_1970 = 2451544.5 - 10957;

    private JulianCalendarUtil()
    {
    }

    public static double julianDay(final Calendar time)
    {
        return julianDay(time.getTimeInMillis());
    }

    /**
     * Computes the {@linkplain #julianDay(Date) julian day}.
     *
     * @param time
     *            The date in milliseconds ellapsed since January 1st, 1970.
     */
    private static double julianDay(final long time)
    {
        return (time / MILLIS_IN_DAY) + JULIAN_DAY_1970;
    }

    public static double julianCentury(final Calendar time)
    {
        return ((time.getTimeInMillis() / MILLIS_IN_DAY) + (JULIAN_DAY_1970 - 2451545.0)) / 36525;
    }
}

package EarthSim.common;

import static EarthSim.common.SimulationSettings.EARTH_ROTATION_DEGREES_PER_HOUR;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class Util
{
    private Util()
    {
    }

    /**
     * Computes the circumference of the circle at the given latitude.
     * 
     * @param latitude
     *            in degrees
     * @param earthRadius
     *            radius of the earth
     * 
     * @return the circumference of the circle at the given latitude in the
     *         units given by <code>earthRadius</code> (i.e. if
     *         <code>earthRadius</code> is in pixels so is the calculated
     *         distance)
     */
    public static float calculateLatitudeCircum(final double latitude, final double earthRadius)
    {
        final double latRadius = earthRadius * Math.sin(Math.toRadians(90d - latitude));
        return (float) (2d * latRadius * Math.PI);
    }

    /**
     * Computes the area of a trapezoid. All the lengths should be of the same
     * unit of measure.
     * 
     * @param topLength
     * @param bottomLength
     * @param height
     * 
     * @return the area of the trapezoid
     */
    public static float calculateTrapezoidArea(final double topLength, final double bottomLength, final double height)
    {
        return (float) ((.5 * height) * (topLength + bottomLength));
    }

    /**
     * Computes the length of the non-parallel sides of a trapezoid. All the
     * lengths should be of the same unit of measure.
     * 
     * @param topLength
     * @param bottomLength
     * @param height
     * 
     * @return the length of the non-parallel sides of the trapezoid
     */
    public static float calculateTrapezoidSideLen(final double topLength, final double bottomLength, final double height)
    {
        return (float) Math.sqrt(Math.pow((Math.abs(topLength - bottomLength) / 2), 2) + Math.pow(height, 2));
    }

    /**
     * Computes the distance from a latitudal degree to the equator.
     * 
     * @param latitude
     *            in degrees
     * @param earthRadius
     *            radius of the earth
     * 
     * @return the distance to the equator in the units given by
     *         <code>earthRadius</code> (i.e. if <code>earthRadius</code> is in
     *         pixels so is the calculated distance)
     */
    public static float calculateDistanceToEquator(final double latitude, final double earthRadius)
    {
        return (float) (earthRadius * Math.sin(Math.toRadians(latitude)));
    }

    public static float calculateEarthRotationalDegreePerSecond()
    {
        return ((float) EARTH_ROTATION_DEGREES_PER_HOUR) / SECONDS.convert(1, HOURS);
    }
}

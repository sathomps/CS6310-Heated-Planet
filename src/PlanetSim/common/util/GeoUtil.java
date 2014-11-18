package PlanetSim.common.util;

public final class GeoUtil
{
    private GeoUtil()
    {
    }

    public static double calculateDistanceToEquator(final double latitude, final double radius)
    {
        return (radius * Math.sin(Math.toRadians(latitude)));
    }

    /**
     * Computes the circumference of the circle at the given latitude.
     * 
     * @param latitude
     *            in degrees
     * @param planetRadius
     *            radius of the planet
     * 
     * @return the circumference of the circle at the given latitude in the
     *         units given by <code>planetRadius</code> (i.e. if
     *         <code>planetRadius</code> is in pixels so is the calculated
     *         distance)
     */
    public static double calculateLatitudeCircum(final double latitude, final double planetRadius)
    {
        final double latRadius = planetRadius * Math.sin(Math.toRadians(90d - latitude));
        return (2d * latRadius * Math.PI);
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
    public static double calculateTrapezoidArea(final double topLength, final double bottomLength, final double height)
    {
        return ((.5 * height) * (topLength + bottomLength));
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
    public static double calculateTrapezoidSideLen(final double topLength, final double bottomLength, final double height)
    {
        return Math.sqrt(Math.pow((Math.abs(topLength - bottomLength) / 2), 2) + Math.pow(height, 2));
    }
}

package PlanetSim.model;

/**
 * http://www.stjarnhimlen.se/comp/tutorial.html
 * 
 * @author sthomps
 *
 */
public class PlanetPosition
{

    private double meanAnomaly;

    private double trueAnomaly;

    private double helioLongitude;

    private double radiusVector;

    private double helioLatitude;

    private double projectedHelioLongitude;

    private double projectedRadiusVector;

    private double rotationalPostion;

    public double getRotationalPostion()
    {
        return rotationalPostion;
    }

    public void setRotationalPostion(final double rotationalPostion)
    {
        this.rotationalPostion = rotationalPostion;
    }

    public double getMeanAnomaly()
    {
        return meanAnomaly;
    }

    public void setMeanAnomaly(final double meanAnomaly)
    {
        this.meanAnomaly = meanAnomaly;
    }

    public double getTrueAnomaly()
    {
        return trueAnomaly;
    }

    public void setTrueAnomaly(final double trueAnomaly)
    {
        this.trueAnomaly = trueAnomaly;
    }

    public double getHelioLongitude()
    {
        return helioLongitude;
    }

    public void setHelioLongitude(final double helioLongitude)
    {
        this.helioLongitude = helioLongitude;
    }

    public double getHelioLatitude()
    {
        return helioLatitude;
    }

    public void setHelioLatitude(final double helioLatitude)
    {
        this.helioLatitude = helioLatitude;
    }

    public double getProjectedHelioLongitude()
    {
        return projectedHelioLongitude;
    }

    public void setProjectedHelioLongitude(final double projectedHelioLongitude)
    {
        this.projectedHelioLongitude = projectedHelioLongitude;
    }

    public double getRadiusVector()
    {
        return radiusVector;
    }

    public void setRadiusVector(final double radiusVector)
    {
        this.radiusVector = radiusVector;
    }

    public double getProjectedRadiusVector()
    {
        return projectedRadiusVector;
    }

    public void setProjectedRadiusVector(final double projectedRadiusVector)
    {
        this.projectedRadiusVector = projectedRadiusVector;
    }
}

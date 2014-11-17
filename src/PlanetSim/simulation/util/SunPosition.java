package PlanetSim.simulation.util;

public class SunPosition
{
    private double longitude;
    private double latitude;
    private double elevation;
    private double azimuth;
    private double distanceToSun;
    private double declination;

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(final double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(final double latitude)
    {
        this.latitude = latitude;
    }

    public double getElevation()
    {
        return elevation;
    }

    public void setElevation(final double elevation)
    {
        this.elevation = elevation;
    }

    public double getAzimuth()
    {
        return azimuth;
    }

    public void setAzimuth(final double azimuth)
    {
        this.azimuth = azimuth;
    }

    public double getDistanceToSun()
    {
        return distanceToSun;
    }

    public void setDistanceToSun(final double distanceToSun)
    {
        this.distanceToSun = distanceToSun;
    }

    public double getDeclination()
    {
        return declination;
    }

    public void setDeclination(double declination)
    {
        this.declination = declination;
    }
}

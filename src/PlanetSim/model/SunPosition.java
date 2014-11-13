package PlanetSim.model;

public class SunPosition
{
    private double longitude;
    private double latitude;
    private double elevation;
    private double azimuth;

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

    @Override
    public String toString()
    {
        return "SunPosition [longitude=" + longitude + ", latitude=" + latitude + ", elevation=" + elevation + ", azimuth=" + azimuth + "]";
    }
}

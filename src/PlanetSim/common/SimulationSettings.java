package PlanetSim.common;

import java.util.LinkedList;

import PlanetSim.display.planet.Planet;
import PlanetSim.display.sun.Sun;
import PlanetSim.model.GridCell;

public class SimulationSettings
{
    private String       simulationName            = "";

    // //-t #: The temporal precision of the temperature data to be stored, as
    // an integer percentage of the number of time periods
    // saved versus the number computed. The default is 100%; that is, all
    // computed values should be stored.
    private double       axialTilt                 = 23.44;

    // //Orbital eccentricity: non-negative real number less than one; default
    // is .0167.
    private double       orbitalEccentricity       = 0.0167;

    // Simulation length: non-negative integer (Solar) months between 1 and
    // 1200; default 12 (one Solar year).
    private int          simulationLength          = 0;

    // -p #: The precision of the data to be stored, in decimal digits after the
    // decimal point. The default is to use the
    // number of digits storable in a normalized float variable. The maximum is
    // the number of digits storable in a
    // normalized double variable. The minimum is zero.
    private int          datastoragePrecision      = 7;

    // -g #: The geographic precision (sampling rate) of the temperature data to
    // be stored, as an integer percentage of the
    // number of grid cells saved versus the number simulated. The default is
    // 100%; that is, a value is stored for each grid cell.
    private int          geographicPrecision       = 100;

    // -t #: The temporal precision of the temperature data to be stored, as an
    // integer percentage of the number of time periods
    // saved versus the number computed. The default is 100%; that is, all
    // computed values should be stored.
    private int          temporalPrecision         = 100;

    private int          simulationTimeStepMinutes = 1;
    private int          gridSpacing               = 15;

    private Sun          sun;
    private Planet       planet;

    private GridSettings gridSettings;

    public SimulationSettings setGridSettings(final GridSettings gridSettings)
    {
        this.gridSettings = gridSettings;
        return this;
    }

    public LinkedList<LinkedList<GridCell>> getGrid()
    {
        return gridSettings.getGrid();
    }

    public SimulationSettings setSun(final Sun sun)
    {
        this.sun = sun;
        return this;
    }

    public Sun getSun()
    {
        return sun;
    }

    public SimulationSettings setPlanet(final Planet planet)
    {
        this.planet = planet;
        return this;
    }

    public Planet getPlanet()
    {
        return planet;
    }

    public int getSimulationTimeStepMinutes()
    {
        return simulationTimeStepMinutes;
    }

    public SimulationSettings setSimulationTimeStepMinutes(final int simulationTimeStepMinutes)
    {
        this.simulationTimeStepMinutes = simulationTimeStepMinutes;
        validateSimulationTimeStepMinutes();
        return this;
    }

    private void validateSimulationTimeStepMinutes()
    {
        simulationTimeStepMinutes = ((simulationTimeStepMinutes >= 1) && (simulationTimeStepMinutes <= 1440)) ? simulationTimeStepMinutes : 1;
    }

    public int getGridSpacing()
    {
        return gridSpacing;
    }

    public SimulationSettings setGridSpacing(final int gridSpacing)
    {
        this.gridSpacing = gridSpacing;
        validateGridSpacing();
        return this;
    }

    private void validateGridSpacing()
    {
        gridSpacing = ((gridSpacing >= 1) && (gridSpacing <= 180)) ? gridSpacing : 15;
        while ((180 % gridSpacing) != 0)
        {
            --gridSpacing;
        }
    }

    public int getEarthWidth()
    {
        return planet.getWidth();
    }

    public int getPlanetRadius()
    {
        return planet.getRadius();
    }

    public void reset()
    {
        planet.reset();
        sun.reset();
    }

    public String getName()
    {
        return simulationName;
    }

    public void setName(final String name)
    {
        simulationName = name;
    }

    public String getSimulationName()
    {
        return simulationName;
    }

    public void setSimulationName(final String simulationName)
    {
        this.simulationName = simulationName;
    }

    public double getAxialTilt()
    {
        return axialTilt;
    }

    public void setAxialTilt(final double axialTilt)
    {
        this.axialTilt = axialTilt;
    }

    public double getOrbitalEccentricity()
    {
        return orbitalEccentricity;
    }

    public void setOrbitalEccentricity(final double orbitalEccentricity)
    {
        this.orbitalEccentricity = orbitalEccentricity;
    }

    public int getSimulationLength()
    {
        return simulationLength;
    }

    public void setSimulationLength(final int simulationLength)
    {
        this.simulationLength = simulationLength;
    }

    public int getDatastoragePrecision()
    {
        return datastoragePrecision;
    }

    public void setDatastoragePrecision(final int datastoragePrecision)
    {
        this.datastoragePrecision = datastoragePrecision;
    }

    public int getGeographicPrecision()
    {
        return geographicPrecision;
    }

    public void setGeographicPrecision(final int geographicPrecision)
    {
        this.geographicPrecision = geographicPrecision;
    }

    public int getTemporalPrecision()
    {
        return temporalPrecision;
    }

    public void setTemporalPrecision(final int temporalPrecision)
    {
        this.temporalPrecision = temporalPrecision;
    }

    public GridSettings getGridSettings()
    {
        return gridSettings;
    }

    // query only related properties
    // Reading date: simulated date at which the temperature reading was taken
    // in terms of years and days since
    // the start of the simulation
    // Reading time: hours and minutes since the start of the Reading Date
    // need a start and end value for both of those
    private int startDate = 0;
    private int startTime = 0;
    private int endDate   = 0;
    private int endTime   = 0;

    public int getStartDate()
    {
        return startDate;
    }

    public void setStartDate(final int startDate)
    {
        this.startDate = startDate;
    }

    public int getStartTime()
    {
        return startTime;
    }

    public void setStartTime(final int startTime)
    {
        this.startTime = startTime;
    }

    public int getEndDate()
    {
        return endDate;
    }

    public void setEndDate(final int endDate)
    {
        this.endDate = endDate;
    }

    public int getEndTime()
    {
        return endTime;
    }

    public void setEndTime(final int endTime)
    {
        this.endTime = endTime;
    }

}

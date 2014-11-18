package PlanetSim.common;

import java.util.Date;
import java.util.LinkedList;

import PlanetSim.display.planet.Planet;
import PlanetSim.display.sun.Sun;
import PlanetSim.model.GridCell;

public class SimulationSettings implements Cloneable
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
        simulationTimeStepMinutes = ((simulationTimeStepMinutes >= 1) && (simulationTimeStepMinutes <= 525600)) ? simulationTimeStepMinutes : 1;
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

    public String getSimulationName()
    {
        return simulationName;
    }

    public SimulationSettings setSimulationName(final String simulationName)
    {
        this.simulationName = simulationName;
        validateSimulationName();
        return this;
    }

    private void validateSimulationName()
    {
        simulationName = ((simulationName != null) && (simulationName.length() > 0)) ? simulationName : new Date().toString();
    }

    public double getAxialTilt()
    {
        return axialTilt;
    }

    public SimulationSettings setAxialTilt(final double axialTilt)
    {
        this.axialTilt = axialTilt;
        validateAxialTilt();
        return this;
    }

    private void validateAxialTilt()
    {
        axialTilt = ((axialTilt >= 1) && (axialTilt <= 180)) ? axialTilt : 23.44;
    }

    public double getOrbitalEccentricity()
    {
        return orbitalEccentricity;
    }

    public SimulationSettings setOrbitalEccentricity(final double orbitalEccentricity)
    {
        this.orbitalEccentricity = orbitalEccentricity;
        validatOrbitalEccentricity();
        return this;
    }

    private void validatOrbitalEccentricity()
    {
        orbitalEccentricity = ((orbitalEccentricity >= 0) && (orbitalEccentricity <= 1)) ? orbitalEccentricity : .0167;
    }

    public int getSimulationLength()
    {
        return simulationLength;
    }

    public SimulationSettings setSimulationLength(final int simulationLength)
    {
        this.simulationLength = simulationLength;
        validateSimulationLength();
        return this;
    }

    private void validateSimulationLength()
    {
        simulationLength = ((simulationLength >= 1) && (simulationLength <= 1200)) ? simulationLength : 12;
    }

    public int getDatastoragePrecision()
    {
        return datastoragePrecision;
    }

    public SimulationSettings setDatastoragePrecision(final int datastoragePrecision)
    {
        this.datastoragePrecision = datastoragePrecision;
        validateDatastoragePrecision();
        return this;
    }

    private void validateDatastoragePrecision()
    {
        datastoragePrecision = ((datastoragePrecision >= 0) && (datastoragePrecision <= 7)) ? datastoragePrecision : 7;
    }

    public int getGeographicPrecision()
    {
        return geographicPrecision;
    }

    public SimulationSettings setGeographicPrecision(final int geographicPrecision)
    {
        this.geographicPrecision = geographicPrecision;
        validateGeographicPrecision();
        return this;
    }

    private void validateGeographicPrecision()
    {
        geographicPrecision = ((geographicPrecision >= 1) && (geographicPrecision <= 100) ? geographicPrecision : 100);
    }

    public int getTemporalPrecision()
    {
        return temporalPrecision;
    }

    public SimulationSettings setTemporalPrecision(final int temporalPrecision)
    {
        this.temporalPrecision = temporalPrecision;
        validaTemporalPrecision();
        return this;
    }

    private void validaTemporalPrecision()
    {
        temporalPrecision = ((temporalPrecision >= 1) && (temporalPrecision <= 100) ? temporalPrecision : 100);
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
    private long startDate = 0;
    //private int startTime = 0;
    private long endDate   = 0;
    //private int endTime   = 0;

    public long getStartDate()
    {
        return startDate;
    }

    public void setStartDate(final long startDate)
    {
        this.startDate = startDate;
    }

//    public int getStartTime()
//    {
//        return startTime;
//    }

//    public void setStartTime(final int startTime)
//    {
//        this.startTime = startTime;
//    }

    public long getEndDate()
    {
        return endDate;
    }

    public void setEndDate(final long endDate)
    {
        this.endDate = endDate;
    }

//    public int getEndTime()
//    {
//        return endTime;
//    }

//    public void setEndTime(final int endTime)
//    {
//        this.endTime = endTime;
//    }

    @Override
    public SimulationSettings clone() throws CloneNotSupportedException
    {
        return (SimulationSettings) super.clone();
    }
}

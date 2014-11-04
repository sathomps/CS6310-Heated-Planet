package EarthSim.common;

import java.util.LinkedList;

import EarthSim.display.earth.Earth;
import EarthSim.display.sun.Sun;

public class SimulationSettings
{
    private int               simulationTimeStepMinutes       = 1;
    private int               gridSpacing                     = 15;

    private Sun               sun;
    private Earth             earth;

    public static final int   EARTH_ROTATION_DEGREES_PER_HOUR = 15;
    public static final float DEFAULT_SUN_POSITION            = 180f;

    private GridSettings      gridSettings;

    private Status            status                          = Status.STOPPED;

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

    public SimulationSettings setEarth(final Earth earth)
    {
        this.earth = earth;
        return this;
    }

    public Earth getEarth()
    {
        return earth;
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
        return earth.getWidth();
    }

    public int getEarthRadius()
    {
        return earth.getRadius();
    }

    public SimulationSettings setStatus(final Status status)
    {
        this.status = status;
        return this;
    }

    public Status getStatus()
    {
        return status;
    }

    public void reset()
    {
        earth.reset();
        sun.reset();
    }
}

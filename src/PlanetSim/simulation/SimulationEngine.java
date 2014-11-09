package PlanetSim.simulation;

import static PlanetSim.common.SimulationSettings.EARTH_ROTATION_DEGREES_PER_HOUR;

import java.util.LinkedList;

import PlanetSim.common.GridCell;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.display.DisplayEvent;

public class SimulationEngine
{
    private final SimulationSettings settings;
    private final EventBus           eventBus;

    public SimulationEngine(final EventBus eventBus, final SimulationSettings settings)
    {
        this.settings = settings;
        this.eventBus = eventBus;
        eventBus.subscribe(this);
    }

    public void run()
    {
        final LinkedList<LinkedList<GridCell>> grid = settings.getGrid();
        for (int row = 0; row < grid.size(); row++)
        {
            for (int cell = 0; cell < grid.get(0).size(); cell++)
            {
                calculateTemp(grid.get(row).get(cell));
            }
        }
        moveSun();
        eventBus.publish(new DisplayEvent(settings));
    }

    private void moveSun()
    {
        settings.getSun().movePosition(EARTH_ROTATION_DEGREES_PER_HOUR);
    }

    private void calculateTemp(final GridCell cell)
    {
        final float initialTemp = cell.getTemp();
        final float sunTemp = calculateSunHeat(cell);
        final float coolingTemp = calculateTemperatureDueToCooling(cell);
        final float neighborTemp = calculateNeighborHeat(cell) / 2;

        cell.setTemp(initialTemp + sunTemp + coolingTemp + neighborTemp);
    }

    private float calculateSunHeat(final GridCell cell)
    {
        return settings.getSun().calculateSunHeat(cell);
    }

    private float calculateTemperatureDueToCooling(final GridCell cell)
    {
        final float relativeTempFactor = cell.getTemp() / settings.getEarth().calculateAverageTemperature();

        final float timeOffset = settings.getSimulationTimeStepMinutes() / 60f;
        final float attenuationConstant = .406867508241966f / 2;

        return (float) -1 * 4 * attenuationConstant * timeOffset * relativeTempFactor;
    }

    private float calculateNeighborHeat(final GridCell cell)
    {
        return (cell.getNorthTemp() + cell.getSouthTemp() + cell.getEastTemp() + cell.getWestTemp()) / 4f;
    }
}

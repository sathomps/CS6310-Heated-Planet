package PlanetSim.simulation;

import java.util.LinkedList;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.display.DisplayEvent;
import PlanetSim.model.GridCell;

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
        // settings.getSun().movePosition(Constants.EARTH_ROTATION_DEGREES_PER_HOUR);
    }

    private void calculateTemp(final GridCell cell)
    {
        final double initialTemp = cell.getTemp();
        final double sunTemp = calculateSunHeat(cell);
        final double coolingTemp = calculateTemperatureDueToCooling(cell);
        final double neighborTemp = calculateNeighborHeat(cell) / 2;

        cell.setTemp(initialTemp + sunTemp + coolingTemp + neighborTemp);
    }

    private double calculateSunHeat(final GridCell cell)
    {
        return settings.getSun().calculateSunHeat(cell);
    }

    private double calculateTemperatureDueToCooling(final GridCell cell)
    {
        final double relativeTempFactor = cell.getTemp() / settings.getPlanet().calculateAverageTemperature();

        final double timeOffset = settings.getSimulationTimeStepMinutes() / 60f;
        final double attenuationConstant = .406867508241966f / 2;

        return (double) -1 * 4 * attenuationConstant * timeOffset * relativeTempFactor;
    }

    private double calculateNeighborHeat(final GridCell cell)
    {
        return (cell.getNorthTemp() + cell.getSouthTemp() + cell.getEastTemp() + cell.getWestTemp()) / 4f;
    }
}

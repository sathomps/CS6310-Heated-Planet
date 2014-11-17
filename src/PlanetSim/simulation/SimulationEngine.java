package PlanetSim.simulation;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;

import java.util.Calendar;
import java.util.LinkedList;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.display.DisplayEvent;
import PlanetSim.model.GridCell;

public class SimulationEngine
{
    private final SimulationSettings settings;
    private final EventBus           eventBus;

    private Calendar                 currentSimulationTime;

    private int                      simulationTimeMinutes;

    private static int               MINUTES_IN_A_MONTH = 43829;

    public SimulationEngine(final EventBus eventBus, final SimulationSettings settings)
    {
        this.settings = cloneSettings(settings);
        setSimulationTime();

        this.eventBus = eventBus;
        eventBus.subscribe(this);
    }

    private void setSimulationTime()
    {
        currentSimulationTime = Calendar.getInstance();
        currentSimulationTime.set(MONTH, 1);
        currentSimulationTime.set(DAY_OF_MONTH, 4);
    }

    private SimulationSettings cloneSettings(final SimulationSettings settings)
    {
        try
        {
            return settings.clone();
        }
        catch (final Exception e)
        {
        }
        return settings;
    }

    public boolean run()
    {
        if (!hasSimulationFinished())
        {
            calculateSimulationTime();

            final LinkedList<LinkedList<GridCell>> grid = settings.getGrid();
            for (int row = 0; row < grid.size(); row++)
            {
                for (int cell = 0; cell < grid.get(0).size(); cell++)
                {
                    // calculateTemp(grid.get(row).get(cell));
                }
            }
            eventBus.publish(new DisplayEvent(settings));

            return true;
        }
        return false;
    }

    private void calculateSimulationTime()
    {
        simulationTimeMinutes += settings.getSimulationTimeStepMinutes();
        currentSimulationTime.add(MINUTE, simulationTimeMinutes);
    }

    private boolean hasSimulationFinished()
    {
        return calculateMonthsPassed() >= settings.getSimulationLength();
    }

    private int calculateMonthsPassed()
    {
        return (MINUTES_IN_A_MONTH / simulationTimeMinutes);
    }
}

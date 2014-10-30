package EarthSim.simulation;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.LinkedList;

import EarthSim.common.GridCell;
import EarthSim.common.SimulationSettings;
import EarthSim.common.Status;

public class SimulationEngine implements Runnable
{
    private final SimulationSettings settings;

    public SimulationEngine(final SimulationSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                while (Status.RUN.equals(settings.getStatus()))
                {
                    for (final LinkedList<GridCell> cells : settings.getGrid())
                    {
                        for (final GridCell cell : cells)
                        {
                            cell.calculateTemp();
                        }
                    }
                    settings.moveSun();
                    Thread.sleep(MILLISECONDS.convert(settings.getSimulationTimeStepMinutes(), SECONDS));
                }
                if (Status.STOP.equals(settings.getStatus()))
                {
                    settings.reset();
                }
                Thread.sleep(100);
            }
        }
        catch (final InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}

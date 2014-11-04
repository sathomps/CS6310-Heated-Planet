package EarthSim.simulation;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import EarthSim.common.SimulationSettings;
import EarthSim.common.Status;

public class SimulationEngineDaemon implements Runnable
{
    private final SimulationSettings settings;
    private final SimulationEngine   engine;

    public SimulationEngineDaemon(final SimulationSettings settings)
    {
        this.settings = settings;
        this.engine = new SimulationEngine(settings);
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
                    engine.runSimulation();
                    Thread.sleep(MILLISECONDS.convert(settings.getSimulationTimeStepMinutes(), SECONDS));
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

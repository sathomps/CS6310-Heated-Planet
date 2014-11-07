package EarthSim.simulation;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import EarthSim.common.SimulationSettings;
import EarthSim.common.event.EventBus;
import EarthSim.common.event.Status;
import EarthSim.common.event.Subscribe;

public class SimulationEngineDaemon implements Runnable
{
    private final SimulationSettings settings;
    private final SimulationEngine   engine;

    private final EventBus           eventBus;

    private boolean                  run = false;

    public SimulationEngineDaemon(final EventBus eventBus, final SimulationSettings settings)
    {
        this.settings = settings;
        this.eventBus = eventBus;
        eventBus.subscribe(this);
        this.engine = new SimulationEngine(eventBus, settings);
    }

    @Override
    public void run()
    {
        try
        {
            while (run)
            {
                engine.run();
                Thread.sleep(MILLISECONDS.convert(settings.getSimulationTimeStepMinutes(), SECONDS));
            }
        }
        catch (final InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void stop(final Status stop)
    {
        run = false;
    }
}

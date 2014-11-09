package PlanetSim.simulation;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.Status;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;

public class SimulationEngineDaemon
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
    public void process(final Status status)
    {
        switch (status)
        {
        case STOP:
            settings.reset();
            run = false;
            eventBus.publish(new DisplayEvent(settings));
            break;

        case RUN:
            run = true;
            this.run();
            break;

        case PAUSE:
            run = false;
            break;

        default:
            break;
        }
    }
}

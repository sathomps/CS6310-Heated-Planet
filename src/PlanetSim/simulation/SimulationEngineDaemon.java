package PlanetSim.simulation;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.PauseEvent;
import PlanetSim.common.event.RunEvent;
import PlanetSim.common.event.StopEvent;
import PlanetSim.common.event.Subscribe;
import PlanetSim.db.PersistEvent;
import PlanetSim.display.DisplayEvent;

public class SimulationEngineDaemon
{
    private final EventBus      eventBus;

    private SimulationEngine    engine;

    private final AtomicBoolean run           = new AtomicBoolean();

    private final AtomicLong    persistEvents = new AtomicLong();

    public SimulationEngineDaemon(final EventBus eventBus)
    {
        this.eventBus = eventBus;
        eventBus.subscribe(this);
    }

    private void run()
    {
        try
        {
            while (run.get() && engine.run())
            {
                display();
                persist();
                Thread.sleep(MILLISECONDS.convert(1, SECONDS));
            }
        }
        catch (final InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void display()
    {
        eventBus.publish(new DisplayEvent(engine.getSimulationSettings()));
    }

    private void persist()
    {
        if (shouldPersist())
        {
            eventBus.publish(new PersistEvent(engine.getSimulationSettings()));
        }
    }

    private boolean shouldPersist()
    {
        final long numPersistEvents = persistEvents.getAndIncrement();
        return (numPersistEvents % engine.getSimulationSettings().getTimePeriodSampleInterval()) == 0;
    }

    @Subscribe
    public void start(final RunEvent event)
    {
        run.set(true);

        if (engine == null)
        {
            engine = new SimulationEngine(event.getSettings());
        }
        run();
    }

    @Subscribe
    public void stop(final StopEvent event)
    {
        engine = null;
        run.set(false);
    }

    @Subscribe
    public void pause(final PauseEvent event)
    {
        run.set(false);
    }
}

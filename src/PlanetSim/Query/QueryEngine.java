package PlanetSim.Query;

import PlanetSim.Query.db.MySqlConnection;
import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;
import PlanetSim.simulation.InterpolateEvent;
import PlanetSim.simulation.SimulateEvent;

public class QueryEngine
{
    private EventBus eventBus = null;

    public QueryEngine(final EventBus eventBus)
    {
        this.eventBus = eventBus;
        eventBus.subscribe(this);
    }

    @Subscribe
    public void save(final PersistEvent event)
    {
        try
        {
            final MySqlConnection con = new MySqlConnection();
            final SimulationSettings settings = event.getSettings();
            con.save(settings);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void query(final QueryEvent event)
    {
        try
        {
            final MySqlConnection con = new MySqlConnection();
            final SimulationSettings settings = event.getSettings();
            final GridSettings gs = con.query(settings);
            if (gs == null)
            {
                eventBus.publish(new SimulateEvent(settings));
            }
            else if (gs.getHeight() != settings.getGridSpacing())
            {
                settings.setGridSettings(gs);
                eventBus.publish(new InterpolateEvent(settings));
            }
            else
            {
                eventBus.publish(new DisplayEvent(settings));
            }
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}

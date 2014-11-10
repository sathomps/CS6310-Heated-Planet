package PlanetSim.Query;

import java.util.ArrayList;

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
            ArrayList<SimulationSettings> ss = con.queryHeader(settings.getName(), settings.getGridSpacing()
            		, settings.getSimulationTimeStepMinutes(), settings.getSimulationLength()
            		, settings.getAxialTilt(), settings.getOrbitalEccentricity()
            		, settings.getDatastoragePrecision(), settings.getGeographicPrecision(), settings.getTemporalPrecision());
            if (ss.isEmpty())
            {
                eventBus.publish(new SimulateEvent(settings));
            }
            else
            {
            	boolean interpolate = false;
            	if (ss.size() > 1)
            	{
            		//just pick the first one for now.  this needs to be more complicated
            		settings.setSimulationName(ss.get(0).getName());
            	}
                final GridSettings gs = con.query(settings);
                settings.setGridSettings(gs);
            	if (interpolate)
            		eventBus.publish(new InterpolateEvent(settings));
            	else
            		eventBus.publish(new DisplayEvent(settings));
            }
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}

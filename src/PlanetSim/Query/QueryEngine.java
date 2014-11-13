package PlanetSim.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import PlanetSim.Query.db.MySqlConnection;
import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;
import PlanetSim.model.GridCell;
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
    /**
     * Lists the simulation names so they can be put in a GUI widget (or whatever)
     * @return ArrayList<String>.  It will always be non-null.  It will be empty if nothing existed
     * @throws SQLException
     */
	public ArrayList<String> listSimulationNames() throws SQLException
	{
		return (new MySqlConnection()).listSimulationNames();
	}
    @Subscribe
    public void save(final PersistEvent event)
    {
        try
        {
            final SimulationSettings settings = event.getSettings();
            //sanity checks
            String simName = settings.getSimulationName();
            if (simName == null || simName.length() == 0)
            	throw new IllegalArgumentException();
            int gridSpacing = settings.getGridSpacing();
            double orbitalEcc = settings.getOrbitalEccentricity();
            double axialTilt = settings.getAxialTilt();
            int simLength = settings.getSimulationLength();
            int simTimeStep = settings.getSimulationTimeStepMinutes();
            int dsPrecision = settings.getDatastoragePrecision();
            int geoPrecision = settings.getGeographicPrecision();
            double temporalPrecision = settings.getTemporalPrecision();
            MySqlConnection con = new MySqlConnection();
            con.saveHeader(simName, gridSpacing, orbitalEcc, axialTilt, simLength, simTimeStep, dsPrecision, geoPrecision, temporalPrecision);
            final LinkedList<LinkedList<GridCell>> grid = settings.getGrid();
            for (int row = 0; row < grid.size(); row++)
            {
                for (int cell = 0; cell < grid.get(0).size(); cell++)
                {
                	GridCell c = grid.get(row).get(cell);
                    con.saveCell(simName, row, cell, c.getTemp()
                    		, c.getLatitudeTop(), c.getLongitudeLeft(), c.getLatitudeBottom(), c.getLongitudeRight()
                    		, c.getDate(), c.getTime(), dsPrecision);
                }
            }
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

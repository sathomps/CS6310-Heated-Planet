package PlanetSim.Query;

import java.sql.SQLException;

import EarthSim.common.GridSettings;
import EarthSim.common.SimulationSettings;
import EarthSim.common.event.EventBus;
import EarthSim.common.event.EventType;
import EarthSim.common.event.Subscribe;
import EarthSim.display.DisplayEvent;
import EarthSim.simulation.InterpolateEvent;
import EarthSim.simulation.SimulateEvent;
import PlanetSim.Query.db.MySqlConnection;

public class QueryEngine {
	private EventBus eventBus = null;
	public QueryEngine(final EventBus eventBus)
	{
		this.eventBus = eventBus;
		eventBus.subscribe(this);
	}
	@Subscribe
	public void save(EventType event)
	{
		if (event instanceof PersistEvent)
		{
			PersistEvent new_name = (PersistEvent) event;
			try {
				MySqlConnection con = new MySqlConnection();
				SimulationSettings settings = new_name.result();
				con.save(settings);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	@Subscribe
	public void query(EventType event)
	{
		if (event instanceof QueryEvent) {
			QueryEvent new_name = (QueryEvent) event;
			try {
				MySqlConnection  con = new MySqlConnection();
				SimulationSettings settings = new_name.result();
				GridSettings gs = con.query(settings);
				if (gs == null) //didn't find anything that matched.  full simulation required
					eventBus.publish(new SimulateEvent(settings));
				else if (gs.getHeight() != settings.getGridSpacing())
				{
					settings.setGridSettings(gs);
					eventBus.publish(new InterpolateEvent(settings));
				}
				else
					eventBus.publish(new DisplayEvent(settings));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}

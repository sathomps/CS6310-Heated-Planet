package PlanetSim.Query;

import EarthSim.common.SimulationSettings;
import EarthSim.common.event.EventType;

public class QueryEvent implements EventType {

	SimulationSettings settings; 
	public QueryEvent(SimulationSettings settings)
	{
		this.settings = settings;
	}
	@Override
	public SimulationSettings result() {
		return settings;
	}

}

package PlanetSim.Query;

import EarthSim.common.SimulationSettings;
import EarthSim.common.event.EventType;

public class PersistEvent implements EventType {
	SimulationSettings settings;
	public PersistEvent(SimulationSettings settings) {
		this.settings = settings;
	}

	@Override
	public SimulationSettings result() {
		return settings;
	}
}

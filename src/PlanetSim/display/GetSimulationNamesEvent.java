package PlanetSim.display;

import java.util.ArrayList;

public class GetSimulationNamesEvent {
	private ArrayList<String> names = null;
	public GetSimulationNamesEvent()
	{
	}
	public ArrayList<String> getNames() {
		return names;
	}
	public void setNames(ArrayList<String> names) {
		this.names = names;
	}
}

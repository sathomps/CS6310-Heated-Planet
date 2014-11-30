package PlanetSim.display;

import java.util.ArrayList;

public class SetSimulationNamesEvent {
	private ArrayList<String> names = null;
	public SetSimulationNamesEvent()
	{
	}
	public ArrayList<String> getNames() {
		return names;
	}
	public void setNames(ArrayList<String> names) {
		this.names = names;
	}
}

package PlanetSim.simulation;

import PlanetSim.common.SimulationSettings;

public class SimulateEvent
{
    private SimulationSettings settings;

    public SimulateEvent(final SimulationSettings settings)
    {
        this.setSettings(settings);
    }

    public SimulationSettings getSettings()
    {
        return settings;
    }

    public void setSettings(final SimulationSettings settings)
    {
        this.settings = settings;
    }

}

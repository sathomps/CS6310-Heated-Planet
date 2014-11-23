package PlanetSim.db;

import PlanetSim.common.SimulationSettings;

public class PersistEvent
{
    private SimulationSettings settings;

    public PersistEvent(final SimulationSettings settings)
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

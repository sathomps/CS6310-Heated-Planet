package PlanetSim.db;

import PlanetSim.common.SimulationSettings;

public class QueryEvent
{
    private SimulationSettings settings;

    public QueryEvent(final SimulationSettings settings)
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

package PlanetSim.common.event;

import PlanetSim.common.SimulationSettings;

public class RunEvent
{
    private SimulationSettings settings;

    public RunEvent(final SimulationSettings settings)
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

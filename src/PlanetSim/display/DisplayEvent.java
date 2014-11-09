package PlanetSim.display;

import PlanetSim.common.SimulationSettings;

public class DisplayEvent
{
    private SimulationSettings settings;

    public DisplayEvent(final SimulationSettings settings)
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

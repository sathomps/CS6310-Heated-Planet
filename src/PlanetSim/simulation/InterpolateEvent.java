package PlanetSim.simulation;

import PlanetSim.common.SimulationSettings;

public class InterpolateEvent
{
    private SimulationSettings settings;

    public InterpolateEvent(final SimulationSettings settings)
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

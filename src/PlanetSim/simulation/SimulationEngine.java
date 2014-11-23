package PlanetSim.simulation;

import static PlanetSim.common.util.PlanetPositionUtil.calculatePlanetPosition;
import static PlanetSim.common.util.PlanetTemperatureUtil.calculatePlanetTemperature;
import PlanetSim.common.SimulationSettings;

public class SimulationEngine
{
    private final SimulationSettings settings;

    public SimulationEngine(final SimulationSettings settings)
    {
        this.settings = settings;
    }

    public boolean run()
    {
        if (!settings.hasSimulationFinished())
        {
            calculatePlanetTemperature(settings);
            calculatePlanetPosition(settings);

            settings.calculateSimulationTimestamp();
            return true;
        }
        return false;
    }

    public SimulationSettings getSimulationSettings()
    {
        return settings;
    }
}

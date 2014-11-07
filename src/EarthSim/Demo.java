package EarthSim;

import javax.swing.SwingUtilities;

import EarthSim.common.SimulationSettings;
import EarthSim.common.event.EventBus;
import EarthSim.display.SimulationUI;
import EarthSim.metrics.MetricsEngine;
import EarthSim.simulation.SimulationEngineDaemon;

public class Demo
{
    private static SimulationSettings settings;
    private static EventBus           eventBus;

    public static void main(final String[] args)
    {
        init();
        startUI();
        startSimulation();
        startMetrics();
    }

    private static void init()
    {
        settings = new SimulationSettings();
        eventBus = new EventBus();
    }

    private static void startSimulation()
    {
        new Thread(new SimulationEngineDaemon(eventBus, settings)).start();
    }

    private static void startUI()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new SimulationUI(eventBus, settings);
            }
        });
    }

    private static void startMetrics()
    {
        new Thread(new MetricsEngine(eventBus)).start();
    }

}

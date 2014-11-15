package PlanetSim;

import javax.swing.SwingUtilities;

import PlanetSim.Query.QueryEngine;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.display.SimulationUI;
import PlanetSim.metrics.MetricsEngine;
import PlanetSim.simulation.SimulationEngineDaemon;

public class Demo
{
    private static SimulationSettings settings;
    private static EventBus           eventBus;
    private static QueryEngine        queryEngine;

    public static void main(final String[] args)
    {
        init();
        startUI();
        startSimulation();
        startMetrics();
        createQueryEngine();
    }

    private static void createQueryEngine() {
		queryEngine = new QueryEngine(eventBus);
		
	}

	private static void init()
    {
        settings = new SimulationSettings();
        eventBus = new EventBus();
    }

    private static void startSimulation()
    {
        new SimulationEngineDaemon(eventBus, settings);
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

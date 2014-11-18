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
        setCommandLineArgs(args);
        startUI();
        startSimulation();
        startMetrics();
        createQueryEngine();
    }

    private static void setCommandLineArgs(final String[] args)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-t"))
            {
                settings.setTemporalPrecision(Integer.parseInt(args[i + 1]));
            }
            else if (args[i].equals("-g"))
            {
                settings.setGeographicPrecision(Integer.parseInt(args[i + 1]));
            }
            else if (args[i].equals("-p"))
            {
                settings.setDatastoragePrecision(Integer.parseInt(args[i + 1]));
            }
        }
    }

    private static void createQueryEngine()
    {
        queryEngine = new QueryEngine(eventBus);

    }

    private static void init()
    {
        settings = new SimulationSettings();
        eventBus = new EventBus();
    }

    private static void startSimulation()
    {
        new SimulationEngineDaemon(eventBus);
    }

    private static void startUI()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                // these are required because the cmd line has values that have
                // to make it into the UI
                new SimulationUI(eventBus, settings);
            }
        });
    }

    private static void startMetrics()
    {
        new Thread(new MetricsEngine(eventBus)).start();
    }

}

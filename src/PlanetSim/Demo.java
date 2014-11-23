package PlanetSim;

import javax.swing.SwingUtilities;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.db.DBEngine;
import PlanetSim.display.SimulationUI;
import PlanetSim.metrics.MetricsEngine;
import PlanetSim.simulation.SimulationEngineDaemon;

public class Demo
{
    private static SimulationSettings settings;
    private static EventBus           eventBus;
    private static DBEngine        queryEngine;

    public static void main(final String[] args)
    {
        init();
        setCommandLineArgs(args);
        initSettings();
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

    private static void initSettings()
    {
        settings.setGridSpacing(15);
        settings.setLatitudeTop(-90);
        settings.setLatitudeBottom(90);
        settings.setLongitudeLeft(-180);
        settings.setLongitudeRight(180);
        settings.setPlanetsAxialTilt(23.44); // default from the project page
        settings.setPlanetsOrbitalEccentricity(0.167);
        settings.setSimulationTimeStepMinutes(1440);
        settings.setSimulationLength(12);
        settings.setUIRefreshRate(1);
    }

    private static void createQueryEngine()
    {
        queryEngine = new DBEngine(eventBus);

    }

    private static void init()
    {
        settings = new SimulationSettings();
        eventBus = EventBus.getInstance();
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
        new MetricsEngine(eventBus);
    }

}

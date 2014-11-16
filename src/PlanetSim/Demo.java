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
        settings.setTemporalPrecision(100);
        settings.setDatastoragePrecision(7);
        settings.setGeographicPrecision(100);
    	for (int i = 0; i < args.length; i++)
    	{
    		if (args[i].equals("-t"))
    	    	//-t #: The temporal precision of the temperature data to be stored, as an integer percentage of the number 
    	        //of time periods saved versus the number computed. The default is 100%; that is, all computed values 
    	        //should be stored. 
    			settings.setTemporalPrecision(Integer.parseInt(args[i+1]));
    		else if (args[i].equals("-g"))
    	    	//-g #: The geographic precision (sampling rate) of the temperature data to be stored, as an integer 
    	        //percentage of the number of grid cells saved versus the number simulated. The default is 100%; that 
    	        //is, a value is stored for each grid cell.
    			settings.setGeographicPrecision(Integer.parseInt(args[i+1]));
    		else if (args[i].equals("-p"))
    	    	//-p #: The precision of the data to be stored, in decimal digits after the decimal point. The default is 
    	        //to use the number of digits storable in a normalized float variable. The maximum is the number of digits
    	        //storable in a normalized double variable. The minimum is zero.
    			settings.setDatastoragePrecision(Integer.parseInt(args[i+1]));
    			
    	}
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
    	//probably don't need to send in settings anymore since the UI will put them on the
    	//bus and these will end up in here eventually.  there isn't a need to seed this data
    	//anymore since it is an incomplete set of settings
        new SimulationEngineDaemon(eventBus, settings);
    }

    private static void startUI()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
            	//these are required because the cmd line has values that have to make it into the UI
                new SimulationUI(eventBus, settings);
            }
        });
    }

    private static void startMetrics()
    {
        new Thread(new MetricsEngine(eventBus)).start();
    }

}

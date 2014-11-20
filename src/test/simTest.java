package test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import PlanetSim.Query.QueryEngine;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.RunEvent;
import PlanetSim.metrics.MetricsEngine;
import PlanetSim.simulation.SimulationEngineDaemon;
import junit.framework.TestCase;

public class simTest extends TestCase {
	private static SimulationSettings settings;
    private static EventBus           eventBus;
    private static QueryEngine        queryEngine;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
        settings = new SimulationSettings();
        eventBus = new EventBus();
        queryEngine = new QueryEngine(eventBus);
        new SimulationEngineDaemon(eventBus);
        new Thread(new MetricsEngine(eventBus)).start();
        
		settings.setTemporalPrecision(7);
        settings.setGeographicPrecision(7);
        settings.setDatastoragePrecision(7);
		settings.setGridSpacing(15);
		settings.setLatitudeTop(-90);
		settings.setLatitudeBottom(90);
		settings.setLongitudeLeft(-180);
		settings.setLongitudeRight(180);
		settings.setPlanetsAxialTilt(23.0);
		settings.setPlanetsOrbitalEccentricity(0.167);
		settings.setSimulationTimeStepMinutes(1440);
		settings.setSimulationLength(12);
		settings.setUIRefreshRate(1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
        settings.setSimulationName("simulation1");
        try {
			eventBus.publish(new RunEvent(settings.clone()));
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

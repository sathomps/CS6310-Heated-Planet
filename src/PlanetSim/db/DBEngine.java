package PlanetSim.db;

import static PlanetSim.db.DataSource.INTERPOLATION;
import static PlanetSim.db.DataSource.QUERY;
import static PlanetSim.db.DataSource.SIMULATION;

import java.util.ArrayList;
import java.util.LinkedList;

import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.RunEvent;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;
import PlanetSim.display.GetSimulationNamesEvent;
import PlanetSim.metrics.MetricEvent;
import PlanetSim.model.GridCell;

public class DBEngine
{
    // used by save method so it can keep up with transactions.
    private final MySqlConnection con;

    private final EventBus        eventBus;

    public DBEngine(final EventBus eventBus)
    {
        this.eventBus = eventBus;
        eventBus.subscribe(this);
        con = new MySqlConnection();
    }

    /**
     * Lists the simulation names so they can be put in a GUI widget (or
     * whatever)
     * 
     */
    @Subscribe
    public void listSimulationNames(final GetSimulationNamesEvent event)
    {
        eventBus.publish(new GetSimulationNamesEvent(con.listSimulationNames()));
    }

    @Subscribe
    public void persist(final PersistEvent event)
    {
        final SimulationSettings settings = event.getSettings();
        // sanity checks
        final String simName = settings.getSimulationName();
        if ((simName == null) || (simName.length() == 0))
        {
            throw new IllegalArgumentException(); // this should be an event
            // on the bus
        }
        final int gridSpacing = settings.getGridSpacing();
        final double orbitalEcc = settings.getPlanetsOrbitalEccentricity();
        final double axialTilt = settings.getPlanetsAxialTilt();
        final int simLength = settings.getSimulationLength();
        final int simTimeStep = settings.getSimulationTimeStepMinutes();
        final int dsPrecision = settings.getDatastoragePrecision();
        final int geoPrecision = settings.getGeographicPrecision();
        final int temporalPrecision = settings.getTemporalPrecision();
        // if the header already exists, then this is more grid data of an
        // existing and likely concurrently running
        // simulation. Only save the grid data this time around.
        if (con.queryHeader(settings.getSimulationName(), 0, 0, 0, 0, 0, 0, 0, 0).isEmpty())
        {
            con.saveHeader(simName, gridSpacing, orbitalEcc, axialTilt, simLength, simTimeStep, dsPrecision, geoPrecision, temporalPrecision);
        }
        final LinkedList<LinkedList<GridCell>> grid = settings.getGrid();
        for (int row = 0; row < grid.size(); row++)
        {
            for (int cell = 0; cell < grid.get(0).size(); cell++)
            {
                final GridCell c = grid.get(row).get(cell);
                con.saveCell(simName, row, cell, c.getTemp(), c.getLatitudeTop(), c.getLongitudeLeft(), c.getLatitudeBottom(), c.getLongitudeRight(),
                        c.getDate(), dsPrecision);

            }
        }

        eventBus.publish(new MetricEvent().setDatabaseSize(con.getDatabaseSize()).setSettings(settings));
    }

    private SimulationSettings query(final SimulationSettings settings)
    {
        try
        {
            final long start = System.nanoTime();
            final MySqlConnection con = new MySqlConnection();
            final ArrayList<SimulationSettings> ss = con.queryHeader(settings.getSimulationName(), settings.getGridSpacing(),
                    settings.getSimulationTimeStepMinutes(), settings.getSimulationLength(), settings.getPlanetsAxialTilt(),
                    settings.getPlanetsOrbitalEccentricity(), settings.getDatastoragePrecision(), settings.getGeographicPrecision(),
                    settings.getTemporalPrecision());
            boolean interpolate = true;
            // if the user wanted data for longer than what was saved then force
            // a simulation
            // even if the physical factors match (according to the project
            // page)
            // remove the simulations that matched on name and/or physical
            // factors but that didn't
            // match based on the length of the simulation
            ArrayList<SimulationSettings> s1 = new ArrayList<SimulationSettings>();
            if (settings.getSimulationTimestamp().getTimeInMillis() > 0)
            {
                for (final SimulationSettings s : ss)
                {
                    if (s.getSimulationTimestamp().getTimeInMillis() <= settings.getSimulationTimestamp().getTimeInMillis())
                    {
                        s1.add(s);
                    }
                }
            }
            else
            {
                s1 = ss;
            }
            // at this point s1 is the list of Simulations that is decided upon
            // whether to send them to the
            // the bus as a DisplayEvent or the SE as a Simulate or Interpolate
            // event
            SimulationSettings chosenOne = null; // the golden child - this this
            // one that gets picked.
            // if there is more than one in the list iterate over them and pick
            // the best. The best is defined
            // as the one that matches the query settings the closest. If none
            // match the pick the first
            // one in the list.
            // this is some of the ugliest code i have ever written. It could be
            // wrapped in a function but
            // there is a side effect here that if one in the list is a perfect
            // match that the interpolate
            // variable is set to false. Since a function returns a scalar i
            // just left it this way but it is
            // hideous
            if (s1.size() > 1)
            {
                for (final SimulationSettings s : s1)
                {
                    if ((settings.getGeographicPrecision() == s.getGeographicPrecision()) && (settings.getTemporalPrecision() == s.getTemporalPrecision())
                            && (settings.getDatastoragePrecision() == s.getDatastoragePrecision()) && (settings.getGridSpacing() == s.getGridSpacing())
                            && (settings.getSimulationTimeStepMinutes() == s.getSimulationTimeStepMinutes()))
                    {
                        interpolate = false;
                        chosenOne = s;
                        break;
                    }
                    else if (
                    // settings.getGeographicPrecision() ==
                    // s.getGeographicPrecision() &&
                    (settings.getTemporalPrecision() == s.getTemporalPrecision()) && (settings.getDatastoragePrecision() == s.getDatastoragePrecision())
                            && (settings.getGridSpacing() == s.getGridSpacing())
                            && (settings.getSimulationTimeStepMinutes() == s.getSimulationTimeStepMinutes()))
                    {
                        chosenOne = s;
                        break;
                    }
                    else if (
                    // settings.getGeographicPrecision() ==
                    // s.getGeographicPrecision() &&
                    // settings.getTemporalPrecision() ==
                    // s.getTemporalPrecision() &&
                    (settings.getDatastoragePrecision() == s.getDatastoragePrecision()) && (settings.getGridSpacing() == s.getGridSpacing())
                            && (settings.getSimulationTimeStepMinutes() == s.getSimulationTimeStepMinutes()))
                    {
                        chosenOne = s;
                        break;
                    }
                    else if (
                    // settings.getGeographicPrecision() ==
                    // s.getGeographicPrecision() &&
                    // settings.getTemporalPrecision() ==
                    // s.getTemporalPrecision() &&
                    // settings.getDatastoragePrecision() ==
                    // s.getDatastoragePrecision() &&
                    (settings.getGridSpacing() == s.getGridSpacing()) && (settings.getSimulationTimeStepMinutes() == s.getSimulationTimeStepMinutes()))
                    {
                        chosenOne = s;
                        break;
                    }
                    else if (
                    // settings.getGeographicPrecision() ==
                    // s.getGeographicPrecision() &&
                    // settings.getTemporalPrecision() ==
                    // s.getTemporalPrecision() &&
                    // settings.getDatastoragePrecision() ==
                    // s.getDatastoragePrecision() &&
                    // settings.getGridSpacing() == s.getGridSpacing() &&
                    settings.getSimulationTimeStepMinutes() == s.getSimulationTimeStepMinutes())
                    {
                        chosenOne = s;
                        break;
                    }
                    else
                    {
                        chosenOne = s;
                        break;
                    }

                }
            }
            // nothing in the list so a simulation has to be run
            else if (s1.size() == 0)
            {
                chosenOne = null;
            }
            // only one in the list so get it and determine the correct value of
            // interpolate
            else
            {
                chosenOne = s1.get(0);
                interpolate = !((settings.getGeographicPrecision() == chosenOne.getGeographicPrecision())
                        && (settings.getTemporalPrecision() == chosenOne.getTemporalPrecision())
                        && (settings.getDatastoragePrecision() == chosenOne.getDatastoragePrecision())
                        && (settings.getGridSpacing() == chosenOne.getGridSpacing()) && (settings.getSimulationTimeStepMinutes() == chosenOne
                        .getSimulationTimeStepMinutes()));
            }
            if (chosenOne != null)
            {
                final GridSettings gs = con.query(chosenOne);
                chosenOne.setGridSettings(gs);
                chosenOne.setDataSource(interpolate ? INTERPOLATION : QUERY);
            }
            final long end = System.nanoTime();

            eventBus.publish(new MetricEvent().setQueryTime(end - start).setDatabaseSize(con.getDatabaseSize()).setSettings(settings));
            return chosenOne;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void query(final QueryEvent event)
    {
        try
        {
            SimulationSettings chosenOne = query(event.getSettings());
            if (chosenOne == null)
            {
                chosenOne = event.getSettings();
                chosenOne.setDataSource(SIMULATION);
                eventBus.publish(new RunEvent(chosenOne));
            }
            else if (chosenOne.getDataSource().equals(QUERY))
            {
                eventBus.publish(new DisplayEvent(chosenOne));
            }
            else
            {
                eventBus.publish(new RunEvent(chosenOne));
            }
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args)
    {
        final EventBus bus = EventBus.getInstance();
        final DBEngine qe = new DBEngine(bus);

        final SimulationSettings ss = new SimulationSettings();
        ss.setDataSource(QUERY);
        ss.setDatastoragePrecision(8);
        ss.setGeographicPrecision(100);
        ss.setGridSpacing(15);
        ss.setSimulationLength(122);
        ss.setSimulationName("sim1");
        ss.setSimulationTimeStepMinutes(1);
        ss.setTemporalPrecision(100);
        // ss.setPlanet(new Planet(ss));
        ss.getSimulationTimestamp().setTimeInMillis(0);
        // GridSettings gridSettings = new GridSettings(ss);
        // for (int row = 0; row < 360 /15; row++)
        // for(int col = 0; col < 180/15;col++)
        // gridSettings.addCell(row, col, 5, row+col, col, row, col+1, row+1,
        // col*10);
        // ss.setGridSettings(gridSettings);
        // PersistEvent event = new PersistEvent(ss);
        // bus.publish(event);
        // SimulationSettings s1 = qe.query(ss);
        final QueryEvent qevent = new QueryEvent(ss);
        final SimulationSettings s1 = qe.query(ss);
        s1.getSimulationName();
        System.out.println(s1.getSimulationName());
    }
}
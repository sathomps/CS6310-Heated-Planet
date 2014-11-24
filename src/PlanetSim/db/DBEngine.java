package PlanetSim.db;

import static PlanetSim.db.DataSource.INTERPOLATION;
import static PlanetSim.db.DataSource.QUERY;
import static PlanetSim.db.DataSource.SIMULATION;

import java.util.ArrayList;

import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.RunEvent;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;
import PlanetSim.display.GetSimulationNamesEvent;
import PlanetSim.metrics.MetricEvent;

public class DBEngine
{
    private static MySqlConnection con;

    private final EventBus         eventBus;

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

        con.save(settings);

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
                    }
                }
                // didn't find one that matched so pick the first one in the
                // list. They all match by name
                // and physical factors so this doesn't break that rule
                if (chosenOne == null)
                {
                    chosenOne = s1.get(0);
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
}
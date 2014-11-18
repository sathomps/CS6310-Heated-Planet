package PlanetSim.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import PlanetSim.Query.db.MySqlConnection;
import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.RunEvent;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;
import PlanetSim.model.GridCell;

public class QueryEngine
{
    private EventBus eventBus = null;

    public QueryEngine(final EventBus eventBus)
    {
        this.eventBus = eventBus;
        eventBus.subscribe(this);
    }

    /**
     * Lists the simulation names so they can be put in a GUI widget (or
     * whatever)
     * 
     * @return ArrayList<String>. It will always be non-null. It will be empty
     *         if nothing existed
     * @throws SQLException
     */
    public ArrayList<String> listSimulationNames() throws SQLException
    {
        return (new MySqlConnection()).listSimulationNames();
    }

    @Subscribe
    public void save(final PersistEvent event)
    {
        try
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
            final double temporalPrecision = settings.getTemporalPrecision();
            final MySqlConnection con = new MySqlConnection();
            con.saveHeader(simName, gridSpacing, orbitalEcc, axialTilt, simLength, simTimeStep, dsPrecision, geoPrecision, temporalPrecision);
            final LinkedList<LinkedList<GridCell>> grid = settings.getGrid();
            for (int row = 0; row < grid.size(); row++)
            {
                for (int cell = 0; cell < grid.get(0).size(); cell++)
                {
                    final GridCell c = grid.get(row).get(cell);
                    con.saveCell(simName, row, cell, c.getTemp(), c.getLatitudeTop(), c.getLongitudeLeft(), c.getLatitudeBottom(), c.getLongitudeRight(),
                            c.getDate(), c.getTime(), dsPrecision);

                }
            }
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
            final MySqlConnection con = new MySqlConnection();
            final SimulationSettings settings = event.getSettings();
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
                settings.setDataSourceProcess(SimulationSettings.DATASOURCE_PROCESS_SIMULATE);
                eventBus.publish(new RunEvent(settings));
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

            final GridSettings gs = con.query(chosenOne);
            chosenOne.setGridSettings(gs);
            if (interpolate)
            {
                chosenOne.setDataSourceProcess(SimulationSettings.DATASOURCE_PROCESS_INTERPOLATE);
                eventBus.publish(new RunEvent(chosenOne));
            }
            else
            {
                chosenOne.setDataSourceProcess(SimulationSettings.DATASOURCE_PROCESS_QUERY);
                eventBus.publish(new DisplayEvent(chosenOne));
            }
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is intended to be part of the metrics for the design study.
     * It may be returned on the event bus that isn't known yet but the data is
     * now available when we need it.
     * 
     * @return - long value that is the database size in bytes
     */
    public long getDataStoreSize()
    {
        return (new MySqlConnection()).getDatabaseSize();
    }
}

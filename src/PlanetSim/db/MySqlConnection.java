package PlanetSim.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

import org.h2.jdbcx.JdbcConnectionPool;

import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.model.GridCell;

public class MySqlConnection
{
    private final String             url      = "jdbc:h2:file:~/heated_planet";
    private final String             user     = "sa";
    private final String             password = "";

    private final JdbcConnectionPool cp;

    public MySqlConnection()
    {
        cp = JdbcConnectionPool.create(url, user, password);
        cp.setMaxConnections(50);
        createSchema();
    }

    private void createSchema()
    {
        Connection con = null;
        try
        {
            con = cp.getConnection();
            con.createStatement().execute("RUNSCRIPT FROM 'classpath:heated_planet.sql'");
        }
        catch (final SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            close(con);
        }
    }

    /**
     * @return - ArrayList<SimulationSettings>. Will never be null. A length of
     *         zero means nothing was found. More than one indicates a 'best
     *         choice' has to be made. One means that will be displayed or
     *         interpolated.
     * @param - all parameters that are to be ignored should be zero or zero
     *        length
     * 
     */
    public ArrayList<SimulationSettings> queryHeader(final String name, final int gridSpacing, final int timeStep, final int simLength, final double axialTilt,
            final double orbitalEccentricity, final int tempPrecision, final int geoPrecision, final int temporalPrecision)
    {
        Connection con = null;

        try
        {
            con = cp.getConnection();
            final ArrayList<SimulationSettings> result = new ArrayList<SimulationSettings>();
            String sql = "SELECT * FROM simulations WHERE 1=1 "; // 1=1 is
            // stupid
            // trick to not
            // have to figure
            // out when an AND
            // should be used.
            if ((name != null) && (name.length() > 0))
            {
                sql += String.format(" AND name = '%s'", name);
            }
            // piazza 497
            // if (timeStep > 0)
            // {
            // sql += String.format(" AND simulation_time_step = %d", timeStep);
            // }
            if (axialTilt > 0)
            {
                sql += String.format(" AND axial_tilt = %f", axialTilt);
            }
            if (orbitalEccentricity > 0)
            {
                sql += String.format(" AND orbital_eccentricity = %f", orbitalEccentricity);
            }

            final ResultSet rs = createResultSet(con, sql);

            while (rs.next())
            {
                final SimulationSettings ss = new SimulationSettings();
                ss.setPlanetsAxialTilt(rs.getDouble("axial_tilt"));
                ss.setGridSpacing(rs.getInt("grid_spacing"));
                ss.setSimulationName(rs.getString("name"));
                ss.setPlanetsOrbitalEccentricity(rs.getDouble("orbital_eccentricity"));
                ss.setGeographicPrecision(rs.getInt("geographic_precision"));
                ss.setSimulationLength(rs.getInt("simulation_length"));
                ss.setSimulationTimeStepMinutes(rs.getInt("simulation_time_step"));
                ss.setTemporalPrecision(rs.getInt("temporal_precision"));
                result.add(ss);
            }
            return result;
        }
        catch (final SQLException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            close(con);
        }
    }

    public ArrayList<String> listSimulationNames()
    {
        Connection con = null;

        final ArrayList<String> result = new ArrayList<String>();
        try
        {
            con = cp.getConnection();

            final ResultSet rs = createResultSet(con, "SELECT name FROM simulations");
            while (rs.next())
            {
                result.add(rs.getString(1));
            }
            return result;
        }
        catch (final SQLException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            close(con);
        }
    }

    private boolean isInRectangle(final double longitudeLeft, final double longitudeRight, final double latitudeTop, final double latitudeBottom,
            final double longLeft, final double latTop)
    {
        // if zero was supplied for all four corners then the whole grid is what
        // we want - return true
        if ((longitudeLeft != 0) && (latitudeBottom != 0) && (longitudeRight != 0) && (latitudeTop != 0))
        {
            return (longitudeLeft <= longLeft) && (longLeft <= longitudeRight) && (latitudeTop >= latTop) && (latitudeBottom <= latTop);
        }
        else
        {
            return true;
        }
    }

    public GridSettings query(final SimulationSettings settings)
    {
        Connection con = null;

        try
        {
            con = cp.getConnection();

            final ResultSet simulationRS = createResultSet(con, String.format("SELECT * FROM simulations WHERE name = '%s'", settings.getSimulationName()));

            if (simulationRS.next())
            {
                final GridSettings gs = new GridSettings(settings);
                final ResultSet settingsRS = createResultSet(con, String.format("SELECT row_position, column_position, temperature, simulation_date"
                        + ", longitudeLeft, longitudeRight, latitudeTop, latitudeBottom " + " FROM simulation_grid_data WHERE simulation_name = '%s'",
                        settings.getSimulationName()));

                while (settingsRS.next())
                {
                    // final int read_tm = rs.getInt("reading_time");
                    final double longLeft = settingsRS.getFloat("longitudeLeft");
                    final double longRight = settingsRS.getFloat("longitudeRight");
                    final double latTop = settingsRS.getFloat("latitudeTop");
                    final double latBottom = settingsRS.getFloat("latitudeBottom");
                    // if this wasn't requested don't return it.
                    if (isInRectangle(settings.getLongitudeLeft(), settings.getLongitudeRight(), settings.getLatitudeTop(), settings.getLatitudeBottom(),
                            longLeft, latTop))
                    {
                        final int row_pos = settingsRS.getInt("row_position");
                        final int col_pos = settingsRS.getInt("column_position");
                        final double temp = settingsRS.getDouble("temperature");
                        final long simulation_date = settingsRS.getLong("simulation_date");
                        gs.addCell(row_pos, col_pos, settings.getGridSpacing(), temp, longLeft, latTop, longRight, latBottom, simulation_date, 0);
                    }
                }
                return gs;
            }
            else
            {
                System.out.println("No data found");
                return null;
            }
        }
        catch (final SQLException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            close(con);
        }
    }

    private boolean simulationExists(final SimulationSettings settings)
    {
        final String name = settings.getSimulationName();
        Connection con = null;
        try
        {
            final String sql = "SELECT TOP 1 name FROM simulations" + String.format(" WHERE name = '%s'", name);
            con = cp.getConnection();

            return createResultSet(con, sql).next();
        }
        catch (final SQLException ex)
        {
            ex.printStackTrace();
            return true;
        }
        finally
        {
            close(con);
        }
    }

    private void saveHeader(final SimulationSettings settings)
    {
        if (!simulationExists(settings))
        {
            Connection con = null;

            try
            {
                con = cp.getConnection();

                final String simName = settings.getSimulationName();
                final int gridSpacing = settings.getGridSpacing();
                final double orbitalEcc = settings.getPlanetsOrbitalEccentricity();
                final double axialTilt = settings.getPlanetsAxialTilt();
                final int simLength = settings.getSimulationLength();
                final int simTimeStep = settings.getSimulationTimeStepMinutes();
                final int dsPrecision = settings.getDatastoragePrecision();
                final int geoPrecision = settings.getGeographicPrecision();
                final int temporalPrecision = settings.getTemporalPrecision();

                final Statement st = cp.getConnection().createStatement();
                final String sql = String.format("INSERT INTO simulations (name, grid_spacing, simulation_time_step, simulation_length"
                        + ", axial_tilt, orbital_eccentricity, temperature_precision, geographic_precision, temporal_precision) "
                        + "VALUES ('%s', %d, %d, %d, %f, %f, %d, %d, %d)", simName, gridSpacing, simTimeStep, simLength, axialTilt, orbitalEcc, dsPrecision,
                        geoPrecision, temporalPrecision);
                st.execute(sql);
            }
            catch (final SQLException ex)
            {
                throw new RuntimeException(ex);
            }
            finally
            {
                close(con);
            }
        }
    }

    public void save(final SimulationSettings settings)
    {
        saveHeader(settings);

        Connection con = null;

        try
        {
            con = cp.getConnection();

            final Statement batch = con.createStatement();
            final LinkedList<LinkedList<GridCell>> grid = settings.getGrid();

            int numGridCell = 0;
            for (int row = 0; row < grid.size(); row++)
            {
                for (int cell = 0; cell < grid.get(0).size(); cell++)
                {
                    numGridCell++;
                    if (shouldPersist(numGridCell, settings))
                    {
                        final GridCell gridCell = grid.get(row).get(cell);
                        saveCell(batch, settings, gridCell);
                    }
                }
            }
            batch.executeBatch();
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            close(con);
        }
    }

    private boolean shouldPersist(final int numGridCell, final SimulationSettings settings)
    {
        return (numGridCell % settings.getGridCellSampleInterval()) == 0;
    }

    private void saveCell(final Statement batch, final SimulationSettings settings, final GridCell gridCell) throws SQLException
    {
        final StringBuilder sql = new StringBuilder("INSERT INTO simulation_grid_data ");
        sql.append("(simulation_name, row_position, column_position, temperature, simulation_date, insertion_ts, longitudeLeft, longitudeRight, latitudeTop, latitudeBottom) ");
        sql.append("VALUES (");
        sql.append("'" + settings.getSimulationName() + "'");
        sql.append(",");
        sql.append("" + gridCell.getRow());
        sql.append(",");
        sql.append("" + gridCell.getColumn());
        sql.append(",");
        sql.append(String.format("%." + settings.getDatastoragePrecision() + "f", gridCell.getTemp()));
        sql.append(",");
        sql.append("" + settings.getSimulationTimestamp().getTimeInMillis());
        sql.append(",");
        sql.append("" + System.nanoTime());
        sql.append(",");
        sql.append("" + gridCell.getLongitudeLeft());
        sql.append(",");
        sql.append("" + gridCell.getLongitudeRight());
        sql.append(",");
        sql.append("" + gridCell.getLatitudeTop());
        sql.append(",");
        sql.append("" + gridCell.getLatitudeBottom());
        sql.append(")");

        batch.addBatch(sql.toString());
    }

    /**
     * XXX - This returns the number of rows, which while not a great indicator
     * of actual database physical size, can be used to infer that metric. To
     * get actual database size varies from db to db and can be a VERY costly
     * query to perform.
     * 
     * @return
     */
    public long getDatabaseSize()
    {
        Connection con = null;

        try
        {
            con = cp.getConnection();

            final String sql = "SELECT count(*) AS rowCount FROM simulation_grid_data";
            final ResultSet result = createResultSet(con, sql);
            if (result.next())
            {
                return result.getLong("rowCount");
            }
        }
        catch (final SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            close(con);
        }
        return 0l;
    }

    private ResultSet createResultSet(final Connection con, final String sql) throws SQLException
    {
        return con.createStatement().executeQuery(sql);
    }

    private void close(final Connection con)
    {
        try
        {
            con.close();
        }
        catch (final Exception e)
        {
        }
    }

}

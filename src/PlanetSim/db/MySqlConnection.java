package PlanetSim.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.h2.jdbcx.JdbcConnectionPool;

import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;

/*
 CREATE DATABASE `heated_planet` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
 USE `heated_planet`;

 -- --------------------------------------------------------

 --
 -- Table structure for table `simulation_grid_data`
 --

 CREATE TABLE IF NOT EXISTS `simulation_grid_data` (
 `simulation_name` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
 `temperature` double NOT NULL,
 `reading_date` bigint(20) NOT NULL,
 `row_position` int(11) NOT NULL,
 `column_position` int(11) NOT NULL,
 `longitudeLeft` float NOT NULL,
 `longitudeRight` float NOT NULL,
 `latitudeTop` float NOT NULL,
 `latitudeBottom` float NOT NULL,
 PRIMARY KEY (`simulation_name`,`reading_date`,`row_position`,`column_position`)
 ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

 -- --------------------------------------------------------

 --
 -- Table structure for table `simulations`
 --

 CREATE TABLE `simulations` (
 `name` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
 `grid_spacing` int(11) NOT NULL,
 `simulation_time_step` int(11) NOT NULL,
 `simulation_length` int(11) NOT NULL,
 `axial_tilt` double NOT NULL,
 `orbital_eccentricity` double NOT NULL,
 `temperature_precision` int(11) NOT NULL,
 `geographic_precision` int(11) NOT NULL,
 `temporal_precision` int(11) NOT NULL,
 PRIMARY KEY (`name`)
 ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

 --
 -- Constraints for dumped tables
 --

 --
 -- Constraints for table `simulation_grid_data`
 --
 ALTER TABLE `simulation_grid_data`
 ADD CONSTRAINT `simulation_grid_data_ibfk_2` FOREIGN KEY (`simulation_name`) REFERENCES `simulations` (`name`) ON DELETE CASCADE ON UPDATE CASCADE;
 */
public class MySqlConnection
{
    private final String     url      = "jdbc:h2:file:~/heated_planet";
    private final String     user     = "sa";
    private final String     password = "";

    final JdbcConnectionPool cp;

    public MySqlConnection()
    {
        cp = JdbcConnectionPool.create(url, user, password);
        cp.setMaxConnections(25);
        createSchema();
    }

    private void createSchema()
    {
        try
        {
            cp.getConnection().createStatement().execute("RUNSCRIPT FROM 'classpath:heated_planet.sql'");
        }
        catch (final SQLException ex)
        {
            ex.printStackTrace();
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
        final ArrayList<SimulationSettings> result = new ArrayList<SimulationSettings>();
        String sql = "SELECT * FROM simulations WHERE 1=1 "; // 1=1 is stupid
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

        try
        {
            final ResultSet rs = createResultSet(sql);

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
    }

    public ArrayList<String> listSimulationNames()
    {
        final ArrayList<String> result = new ArrayList<String>();
        try
        {
            final ResultSet rs = createResultSet("SELECT name FROM simulations");
            while (rs.next())
            {
                result.add(rs.getString(0));
            }
            return result;
        }
        catch (final SQLException ex)
        {
            throw new RuntimeException(ex);
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
        try
        {
            final ResultSet simulationRS = createResultSet(String.format("SELECT * FROM simulations WHERE name = '%s'", settings.getSimulationName()));

            if (simulationRS.next())
            {
                final GridSettings gs = new GridSettings(settings);
                final ResultSet settingsRS = createResultSet(String.format("SELECT row_position, column_position, temperature, reading_date"
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
                        final long read_dt = settingsRS.getLong("reading_date");
                        gs.addCell(row_pos, col_pos, settings.getGridSpacing(), temp, longLeft, latTop, longRight, latBottom, read_dt, 0);
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

    }

    public void saveHeader(final String simName, final int gridSpacing, final double orbitalEcc, final double axialTilt, final int simLength,
            final int simTimeStep, final int dsPrecision, final int geoPrecision, final int temporalPrecision)
    {
        try
        {
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

    }

    public void save(final SimulationSettings settings)
    {
    }

    public void saveCell(final String simName, final int row, final int cell, final double d, final double e, final double f, final double g, final double h,
            final long date, final int dsPrecision)
    {
        try
        {
            final Statement st = cp.getConnection().createStatement();
            final String sql = "INSERT INTO simulation_grid_data (simulation_name, row_position, column_position, temperature, reading_date"
                    // how many decimals to store, kinda dorky but i can't
                    // figure
                    // how to round to x round places.
                    + ", longitudeLeft, longitudeRight, latitudeTop, latitudeBottom) " + " VALUES ('%s', %d, %d" + ", %." + dsPrecision + "f"
                    + ", %d, %f, %f, %f, %f)";
            st.execute(String.format(sql, simName, row, cell, d, date, f, h, e, g));
        }
        catch (final SQLException ex)
        {
            throw new RuntimeException(ex);
        }

    }

    public long getDatabaseSize()
    {
        try
        {
            final String sql = "SELECT sum( data_length + index_length ) FROM information_schema.TABLES  WHERE table_schema = 'heated_planet'";
            return createResultSet(sql).getLong(0);
        }
        catch (final SQLException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private ResultSet createResultSet(final String sql) throws SQLException
    {
        final Statement st = cp.getConnection().createStatement();
        return st.executeQuery(sql);
    }
}

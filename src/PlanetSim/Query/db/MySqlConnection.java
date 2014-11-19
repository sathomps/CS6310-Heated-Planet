package PlanetSim.Query.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
    String url      = "jdbc:mysql://localhost:3306/heated_planet";
    String user     = "root";
    String password = "root";

    // db heavy lifting
    Connection con = null; 
    public MySqlConnection()
    {

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
            final double orbitalEccentricity, final int tempPrecision, final int geoPrecision, final int temporalPrecision) throws SQLException
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
        //piazza 497
//        if (timeStep > 0)
//        {
//            sql += String.format(" AND simulation_time_step = %d", timeStep);
//        }
        if (axialTilt > 0)
        {
            sql += String.format(" AND axial_tilt = %f", axialTilt);
        }
        if (orbitalEccentricity > 0)
        {
            sql += String.format(" AND orbital_eccentricity = %f", orbitalEccentricity);
        }

        final Connection con = DriverManager.getConnection(url, user, password);
        final Statement st = con.createStatement();
        final ResultSet rs = st.executeQuery(sql);
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
        con.close();
        return result;
	}

    public ArrayList<String> listSimulationNames() throws SQLException
    {
        final ArrayList<String> result = new ArrayList<String>();
        final Connection con = DriverManager.getConnection(url, user, password);
        final Statement st = con.createStatement();
        final ResultSet rs = st.executeQuery("SELECT name FROM simulations");
        while (rs.next())
        {
            result.add(rs.getString(0));
        }
        return result;
    }

    private boolean isInRectangle(final double longitudeLeft, final double longitudeRight, final double latitudeTop, final double latitudeBottom,
            final float x, final float y)
    {
		//if zero was supplied for all four corners then the whole grid is what we want - return true
		if ((longitudeLeft != 0) && (latitudeBottom  != 0)&& (longitudeRight != 0) && (latitudeTop  != 0))
			return (longitudeLeft <= x) && (x <= longitudeRight) && (latitudeTop >= y) && (latitudeBottom <= y);
		else 
			return true;
	}

    public GridSettings query(final SimulationSettings settings) throws SQLException
    {
        final Connection con = DriverManager.getConnection(url, user, password);
        final Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(String.format("SELECT * FROM simulations WHERE name = '%s'", settings.getSimulationName()));

        if (rs.next())
        {
            final GridSettings gs = new GridSettings(settings);
            rs = st.executeQuery(String.format("SELECT row_position, column_position, temperature, reading_date"
                    + ", longitudeLeft, longitudeRight, latitudeTop, latitudeBottom " + 
            		" FROM simulation_grid_data WHERE simulation_name = '%s'", settings.getSimulationName()));
            while (rs.next())
            {
                // final int read_tm = rs.getInt("reading_time");
                final float longLeft = rs.getFloat("longitudeLeft");
                final float longRight = rs.getFloat("longitudeRight");
                final float latTop = rs.getFloat("latitudeTop");
                final float latBottom = rs.getFloat("latitudeBottom");
                // if this wasn't requested don't return it.
                if (isInRectangle(settings.getLongitudeLeft(), settings.getLongitudeRight(), settings.getLatitudeTop(), settings.getLatitudeBottom(), longLeft,
                        latTop))
                {
                    final int row_pos = rs.getInt("row_position");
                    final int col_pos = rs.getInt("column_position");
                    final double temp = rs.getDouble("temperature");
                    final long read_dt = rs.getLong("reading_date");
                    gs.addCell(row_pos, col_pos, settings.getGridSpacing(), temp, longLeft, latTop, longRight, latBottom, read_dt);
                }
            }
            con.close();
            return gs;
        }
        else
        {
            System.out.println("No data found");
            return null;
        }

    }

    public void saveHeader(final String simName, final int gridSpacing, final double orbitalEcc, final double axialTilt, final int simLength,
            final int simTimeStep, final int dsPrecision, final int geoPrecision, final int temporalPrecision) throws SQLException
    {
        final Connection con = DriverManager.getConnection(url, user, password);
        final Statement st = con.createStatement();
        final String sql = String.format("INSERT INTO simulations (name, grid_spacing, simulation_time_step, simulation_length"
                + ", axial_tilt, orbital_eccentricity, temperature_precision, geographic_precision, temporal_precision) "
                + "VALUES ('%s', %d, %d, %d, %f, %f, %d, %d, %d)"
                , simName, gridSpacing, simTimeStep, simLength, axialTilt, orbitalEcc, dsPrecision, geoPrecision, temporalPrecision);
        st.execute(sql);
    }

    public void save(final SimulationSettings settings) throws SQLException
    {
    }

    public void saveCell(final String simName, final int row, final int cell, final double d, final double e, final double f, final double g, final double h,
            final long date, final int dsPrecision) throws SQLException
    {
        //final Connection con = DriverManager.getConnection(url, user, password);
    	boolean localCon = false;
    	if (con == null)
    	{
    		con = DriverManager.getConnection(url, user, password);
    		localCon = true;
    	}
        final Statement st = con.createStatement();
        final String sql = "INSERT INTO simulation_grid_data (simulation_name, row_position, column_position, temperature, reading_date"
        		// how many decimals to store, kinda dorky but i can't figure how to round to x round places.
                + ", longitudeLeft, longitudeRight, latitudeTop, latitudeBottom) " +
                " VALUES ('%s', %d, %d" + ", %." + dsPrecision + "f" + 
                ", %d, %f, %f, %f, %f)";
        st.execute(String.format(sql, simName, row, cell, d, date, f, h, e, g));
        if (localCon)
        {
        	con.close();
        	con = null;
        }
    }

    public long getDatabaseSize() throws SQLException
    {
        final String sql = "SELECT sum( data_length + index_length ) FROM information_schema.TABLES  WHERE table_schema = 'heated_planet'";
        Connection con = null;
        try
        {
            con = DriverManager.getConnection(url, user, password);
            final Statement st = con.createStatement();
            final ResultSet rs = st.executeQuery(sql);
            return rs.getLong(0);
        }
        catch (final SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
        	if (con!=null) con.close();
        }
        return -1;
    }

	public void startTransaction() throws SQLException {
		if (con != null)
			con.setAutoCommit(false);
		
	}

	public void commitTransaction() throws SQLException {
		if (con != null)
		{
			con.commit();
			con.close();
		}
	}

	public void rollbackTransaction() throws SQLException {
		if (con != null)
		{
			con.rollback();
			con.close();
		}		
	}
}

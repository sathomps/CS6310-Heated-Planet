package PlanetSim.Query.db;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
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
  `reading_date` int(11) NOT NULL,
  `reading_time` int(11) NOT NULL,
  `row_position` int(11) NOT NULL,
  `column_position` int(11) NOT NULL,
  `longitudeLeft` float NOT NULL,
  `longitudeRight` float NOT NULL,
  `latitudeTop` float NOT NULL,
  `latitudeBottom` float NOT NULL,
  PRIMARY KEY (`simulation_name`,`reading_date`,`reading_time`,`row_position`,`column_position`)
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
public class MySqlConnection {
    String url = "jdbc:mysql://localhost:3306/heated_planet";
    String user = "root";
    String password = "root";

	//db heavy lifting
	public MySqlConnection ()
	{
		
	}
	/**
	 * @return - ArrayList<SimulationSettings>.  Will never be null. A length of zero means nothing was found.  More than one 
	 * indicates a 'best choice' has to be made.  One means that will be displayed or interpolated.
	 * @param - all parameters that are to be ignored should be zero or zero length
	 * 
	 */
	public ArrayList<SimulationSettings> queryHeader(String name, int gridSpacing, int timeStep, int simLength
			, double axialTilt, double orbitalEccentricity, int tempPrecision, int geoPrecision, int temporalPrecision) throws SQLException
	{
		ArrayList<SimulationSettings> result = new ArrayList<SimulationSettings>();
		String sql = "SELECT * FROM simulations WHERE 1=1 "; //1=1 is stupid trick to not have to figure out when an AND should be used.
		if (name != null && name.length() > 0)
			sql += String.format(" AND name = '%s'", name);
		if (timeStep > 0)
			sql += String.format(" AND simulation_time_spacing = %d", timeStep);
		if (axialTilt > 0)
			sql += String.format(" AND axial_tilt = %d", axialTilt);
		if (orbitalEccentricity > 0)
			sql += String.format(" AND orbital_eccentricity = %d", orbitalEccentricity);

		Connection con = DriverManager.getConnection(url, user, password);
        Statement st = con.createStatement();        
        ResultSet rs = st.executeQuery(sql);        
    	while (rs.next())
    	{
        	SimulationSettings ss = new SimulationSettings();
        	ss.setAxialTilt(rs.getDouble("axial_tilt"));
        	ss.setGridSpacing(rs.getInt("grid_spacing"));
        	ss.setName(rs.getString("name"));
        	ss.setOrbitalEccentricity(rs.getDouble("orbital_eccentricity"));
        	ss.setGeographicPrecision(rs.getInt("geographic_precision"));
        	ss.setSimulationLength(rs.getInt("simulation_length"));
        	ss.setSimulationTimeStepMinutes(rs.getInt("simulation_time_step"));
        	ss.setTemporalPrecision(rs.getInt("temporal_precision"));
        	result.add(ss);
        }
		return result;
	}
	public ArrayList<String> listSimulationNames() throws SQLException
	{
		ArrayList<String> result = new ArrayList<String>();
		Connection con = DriverManager.getConnection(url, user, password);
        Statement st = con.createStatement();        
        ResultSet rs = st.executeQuery("SELECT name FROM simulations" );
		while (rs.next())
		{
			result.add(rs.getString(0));
		}
		return result;
	}
	public GridSettings query(SimulationSettings settings) throws SQLException 
	{
		Connection con = DriverManager.getConnection(url, user, password);
        Statement st = con.createStatement();        
        ResultSet rs = st.executeQuery(String.format("SELECT * FROM simulations WHERE name = '%s'", settings.getName()));
        
        if (rs.next()) 
        {
        	GridSettings gs = new GridSettings(settings);
        	rs = st.executeQuery(String.format(
        			"SELECT row_position, column_position, temperature, reading_date, reading_time" +
        			", longitudeLeft, longitudeRight, latitudeTop, latitudeBottom " +
        			" FROM simulation_grid WHERE name = '%s'"
        			, settings.getName()));
        	while (rs.next())
        	{
        		int row_pos = rs.getInt("row_position");
        		int col_pos = rs.getInt("column_position");
        		double temp = rs.getDouble("temperature");
        		int read_dt = rs.getInt("reading_date");
        		int read_tm = rs.getInt("reading_time");
        		float longLeft = rs.getFloat("longitudeLeft");
        		float longRight = rs.getFloat("longitudeRight");
        		float latTop = rs.getFloat("latitudeTop");
        		float latBottom = rs.getFloat("latitudeBottom");
        		gs.addCell(row_pos, col_pos, settings.getGridSpacing(), temp
        	    		, longLeft, latTop, longRight, latBottom
        	    		, read_dt, read_tm);
        	}
        			
        	return gs;
        }
        else
        {
        	System.out.println("No data found");
        	return null;
        }
		
	}
	public void save(SimulationSettings settings) throws SQLException
	{
		Connection con = DriverManager.getConnection(url, user, password);
        Statement st = con.createStatement();        
		
	}
}

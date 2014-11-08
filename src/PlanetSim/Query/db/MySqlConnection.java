package PlanetSim.Query.db;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import EarthSim.common.SimulationSettings;
import EarthSim.common.GridSettings;

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

CREATE TABLE IF NOT EXISTS `simulations` (
  `name` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `grid_spacing` int(11) NOT NULL,
  `simulation_time_step` int(11) NOT NULL,
  `simulation_length` int(11) NOT NULL,
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

CREATE TABLE IF NOT EXISTS simulations (
  name varchar(50) NOT NULL,
  grid_spacing INT NOT NULL,
  simulation_time_step INT NOT NULL,
  simulation_length INT NOT NULL,
  axial_tilt FLOAT NOT NULL,
  orbital_eccentricity FLOAT NOT NULL,
  temperature_precision INT NOT NULL,
  geographic_precision INT NOT NULL,
  temporal_precision INT NOT NULL,
  PRIMARY KEY (name)
);
        
CREATE TABLE IF NOT EXISTS simulation_grid_data (
  simulation_name varchar(50)  NOT NULL,
  temperature DOUBLE NOT NULL,
  simulation_date BIGINT NOT NULL,
  insertion_ts BIGINT NOT NULL,
  row_position INT NOT NULL,
  column_position INT NOT NULL,
  longitudeLeft FLOAT NOT NULL,
  longitudeRight FLOAT NOT NULL,
  latitudeTop FLOAT NOT NULL,
  latitudeBottom FLOAT NOT NULL,
  PRIMARY KEY (simulation_name,simulation_date,insertion_ts,row_position,column_position)
);

ALTER TABLE simulation_grid_data
  ADD CONSTRAINT IF NOT EXISTS simulation_grid_data_ibfk_2 FOREIGN KEY (simulation_name) REFERENCES simulations (name) ON DELETE CASCADE ON UPDATE CASCADE;


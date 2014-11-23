CREATE TABLE IF NOT EXISTS simulations (
  name varchar(50) NOT NULL,
  grid_spacing int(11) NOT NULL,
  simulation_time_step int(11) NOT NULL,
  simulation_length int(11) NOT NULL,
  axial_tilt double NOT NULL,
  orbital_eccentricity double NOT NULL,
  temperature_precision int(11) NOT NULL,
  geographic_precision int(11) NOT NULL,
  temporal_precision int(11) NOT NULL,
  PRIMARY KEY (name)
)
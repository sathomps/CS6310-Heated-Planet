
CREATE TABLE IF NOT EXISTS simulation_grid_data (
  simulation_name varchar(50)  NOT NULL,
  temperature DOUBLE NOT NULL,
  reading_date BIGINT NOT NULL,
  row_position INT NOT NULL,
  column_position INT NOT NULL,
  longitudeLeft DOUBLE NOT NULL,
  longitudeRight DOUBLE NOT NULL,
  latitudeTop DOUBLE NOT NULL,
  latitudeBottom DOUBLE NOT NULL,
  PRIMARY KEY (simulation_name,reading_date,row_position,column_position)
);

CREATE TABLE IF NOT EXISTS simulation_grid_data (
  simulation_name varchar(50)  NOT NULL,
  temperature DOUBLE NOT NULL,
  reading_date BIGINT NOT NULL,
  row_position INT NOT NULL,
  column_position INT NOT NULL,
  longitudeLeft DOUBLE NOT NULL,
  longitudeRight DOUBLE NOT NULL,
  latitudeTop DOUBLE NOT NULL,
  latitudeBottom DOUBLE NOT NULL,
  PRIMARY KEY (simulation_name,reading_date,row_position,column_position)
);

ALTER TABLE simulation_grid_data
  ADD CONSTRAINT IF NOT EXISTS simulation_grid_data_ibfk_2 FOREIGN KEY (simulation_name) REFERENCES simulations (name) ON DELETE CASCADE ON UPDATE CASCADE;


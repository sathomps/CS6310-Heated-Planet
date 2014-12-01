mkdir bin
javac $(find . -name '*.java') -classpath '.:./lib/*:./bin/*' -d './bin' 
cp src/heated_planet.sql bin/heated_planet.sql
mkdir bin/PlanetSim/display/images
cp src/PlanetSim/display/images/*.png bin/PlanetSim/display/images

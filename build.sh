mkdir bin
cp src/heated_planet.sql bin/heated_planet.sql

javac $(find . -name '*.java') -classpath '.:./lib/*:./bin/*' -d './bin' 

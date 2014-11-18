CS6310-Heated-Planet
===================
#this config set of statements presumes you are using the
#VM supplied with the class and that it is configured with
#the default mysql root/root uid/password as the one i had
#if those are different then change them on the mysql cmd
#From the cmd line in /CS6310-Heated-Planet/src/PlanetSim
#create the db schema
mysql --user=root --password=root < heated_planet.sql 
#the following will compile the code
javac *.java
javac common/*.java
javac display/*.java
javac metrics/*.java
javac model/*.java
javac Query/*.java
javac simulation/*.java



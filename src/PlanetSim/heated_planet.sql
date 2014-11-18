-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 17, 2014 at 08:04 PM
-- Server version: 5.5.35
-- PHP Version: 5.3.10-1ubuntu3.10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE DATABASE `heated_planet` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `heated_planet`;


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `heated_planet`
--

-- --------------------------------------------------------

--
-- Table structure for table `simulations`
--

CREATE TABLE IF NOT EXISTS `simulations` (
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

--
-- Constraints for dumped tables
--

--
-- Constraints for table `simulation_grid_data`
--
ALTER TABLE `simulation_grid_data`
  ADD CONSTRAINT `simulation_grid_data_ibfk_2` FOREIGN KEY (`simulation_name`) REFERENCES `simulations` (`name`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

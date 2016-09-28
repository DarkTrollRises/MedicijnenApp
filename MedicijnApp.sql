-- phpMyAdmin SQL Dump
-- version 4.4.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Gegenereerd op: 03 nov 2015 om 08:52
-- Serverversie: 5.6.26
-- PHP-versie: 5.6.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `medicationapp`
--

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `homecare`
--

CREATE TABLE IF NOT EXISTS `homecare` (
  `employeeID` int(11) NOT NULL,
  `employeeNumber` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

--
-- Gegevens worden geëxporteerd voor tabel `homecare`
--

INSERT INTO `homecare` (`employeeID`, `employeeNumber`, `username`, `password`) VALUES
(1, 215946, 'LSJansen', 'Beuningen14'),
(4, 369418, 'DThijssen', 'Arnehem23'),
(17, 946173, 'MACvanHeel', 'Arnhem02');

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `medication`
--

CREATE TABLE IF NOT EXISTS `medication` (
  `medicationID` int(11) NOT NULL,
  `medicationName` varchar(30) NOT NULL,
  `type` varchar(10) NOT NULL,
  `unit` varchar(10) NOT NULL,
  `photo` varchar(100) NOT NULL,
  `leaflet` varchar(500) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

--
-- Gegevens worden geëxporteerd voor tabel `medication`
--

INSERT INTO `medication` (`medicationID`, `medicationName`, `type`, `unit`, `photo`, `leaflet`) VALUES
(1, 'Paracetamol 500mg', 'Tablet', 'Stuk(s)', '', ''),
(4, 'Ibuprofen 400mg', 'Tablet', 'Stuk(s)', '', ''),
(5, 'Claritine', 'Siroop', 'ml', '', ''),
(6, 'Aciclovir 50mg', 'Créme', 'mg', '', '');

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `medicationuse`
--

CREATE TABLE IF NOT EXISTS `medicationuse` (
  `UseID` int(11) NOT NULL,
  `patientID` int(11) NOT NULL,
  `medicationID` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `days` varchar(15) NOT NULL,
  `time` time NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `patient`
--

CREATE TABLE IF NOT EXISTS `patient` (
  `patientID` int(11) NOT NULL,
  `firstName` varchar(20) NOT NULL,
  `lastName` varchar(30) NOT NULL,
  `birthDate` date NOT NULL,
  `birthPlace` varchar(30) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(64) NOT NULL,
  `registrationID` varchar(255) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Gegevens worden geëxporteerd voor tabel `patient`
--

INSERT INTO `patient` (`patientID`, `firstName`, `lastName`, `birthDate`, `birthPlace`, `username`, `password`, `registrationID`) VALUES
(1, 'Herman', 'Oerlemans', '1948-12-20', 'Diepenheim', 'HOerlemans', 'Diepenheim20', ''),
(2, 'Hendrika', 'van Laar', '1932-10-09', 'Venlo', 'HvanLaar', 'Venlo09', ''),
(3, 'Theo', 'Micheels', '2015-02-24', 'Zaltbommel', 'TMicheels', 'Zaltbommel24', '');

--
-- Indexen voor geëxporteerde tabellen
--

--
-- Indexen voor tabel `homecare`
--
ALTER TABLE `homecare`
  ADD PRIMARY KEY (`employeeID`),
  ADD UNIQUE KEY `usernamePassword` (`password`,`username`) USING BTREE,
  ADD UNIQUE KEY `employeeNumber` (`employeeNumber`) USING BTREE;

--
-- Indexen voor tabel `medication`
--
ALTER TABLE `medication`
  ADD PRIMARY KEY (`medicationID`),
  ADD UNIQUE KEY `mednameType` (`medicationName`,`type`) USING BTREE;

--
-- Indexen voor tabel `medicationuse`
--
ALTER TABLE `medicationuse`
  ADD PRIMARY KEY (`UseID`),
  ADD UNIQUE KEY `patientID` (`patientID`,`medicationID`,`startDate`,`endDate`);

--
-- Indexen voor tabel `patient`
--
ALTER TABLE `patient`
  ADD PRIMARY KEY (`patientID`),
  ADD UNIQUE KEY `firstLastNameBirthPlaceDate` (`firstName`,`lastName`,`birthDate`,`birthPlace`) USING BTREE;

--
-- AUTO_INCREMENT voor geëxporteerde tabellen
--

--
-- AUTO_INCREMENT voor een tabel `homecare`
--
ALTER TABLE `homecare`
  MODIFY `employeeID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=18;
--
-- AUTO_INCREMENT voor een tabel `medication`
--
ALTER TABLE `medication`
  MODIFY `medicationID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT voor een tabel `medicationuse`
--
ALTER TABLE `medicationuse`
  MODIFY `UseID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT voor een tabel `patient`
--
ALTER TABLE `patient`
  MODIFY `patientID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

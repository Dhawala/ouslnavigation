-- phpMyAdmin SQL Dump
-- version 4.5.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: May 22, 2019 at 03:23 AM
-- Server version: 5.7.11
-- PHP Version: 5.6.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ousl_navigation`
--

-- --------------------------------------------------------

--
-- Table structure for table `activity`
--

CREATE TABLE `activity` (
  `ac_code` varchar(10) NOT NULL,
  `ac_name` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `activity`
--

INSERT INTO `activity` (`ac_code`, `ac_name`) VALUES
('CAT1', 'CA-Test 1'),
('CAT2', 'CA-Test2'),
('DS1', 'Day School 1'),
('DS2', 'Day School 2'),
('DS3', 'Day School 3'),
('DS4', 'Day School 4'),
('FE1', 'Final Examination 1'),
('MP1', 'Mini Project 1'),
('OQ1', 'Online Quiz 1'),
('TMA1', 'Tutor Marked Assignment Due 1'),
('TMA2', 'Tutor Marked Assignment Due 2'),
('TMA3', 'Tutor Marked Assignment Due 3');

-- --------------------------------------------------------

--
-- Table structure for table `building`
--

CREATE TABLE `building` (
  `bul_id` int(11) NOT NULL,
  `coordinates` varchar(500) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `building`
--

INSERT INTO `building` (`bul_id`, `coordinates`) VALUES
(1, '6.882936,79.886606:6.882962,79.886399:6.883308,79.886431:6.883284,79.886636:6.882936,79.886606'),
(2, '6.883334,79.886458:6.883311,79.886657:6.883567,79.886668:6.883600,79.886517:6.883412,79.886502:6.883413,79.886462:6.883334,79.886458'),
(3, '6.882930,79.886337:6.883603,79.886404:6.883635,79.886162:6.882948,79.886113:6.882930,79.886337');

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `course_code` varchar(10) NOT NULL,
  `course_name` varchar(50) NOT NULL,
  `department` varchar(50) NOT NULL,
  `credits` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`course_code`, `course_name`, `department`, `credits`) VALUES
('EEI6461', 'Electronic Commerce', 'Electrical and Computer Engineering', 4),
('EEI6560', 'Software Project Management', 'Electrical and Computer Engineering', 5),
('EEI6565', 'Artificial Intelligence Techniques', 'Electrical and Computer Engineering', 5),
('EEI6567', 'Software Architecture and Design', 'Electrical and Computer Engineering', 5),
('EEX6563', 'Software Construction', 'Electrical and Computer Engineering', 5);

-- --------------------------------------------------------

--
-- Table structure for table `enrollment`
--

CREATE TABLE `enrollment` (
  `sno` varchar(10) NOT NULL,
  `course_code` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `enrollment`
--

INSERT INTO `enrollment` (`sno`, `course_code`) VALUES
('S13012418', 'EEI6461'),
('S13012418', 'EEI6560'),
('S13012418', 'EEI6565'),
('S13012418', 'EEI6567'),
('S13012418', 'EEX6563');

-- --------------------------------------------------------

--
-- Table structure for table `lecturer`
--

CREATE TABLE `lecturer` (
  `emp_no` int(11) NOT NULL,
  `nic` varchar(15) NOT NULL,
  `name` varchar(400) NOT NULL,
  `email` varchar(45) NOT NULL,
  `contact` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE `location` (
  `loc_id` varchar(5) NOT NULL,
  `loc_name` varchar(45) NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `location`
--

INSERT INTO `location` (`loc_id`, `loc_name`, `longitude`, `latitude`) VALUES
('b01', 'CRC Class Rooms/Dispatch/Registration Block', 6.883131, 79.886514);

-- --------------------------------------------------------

--
-- Table structure for table `room`
--

CREATE TABLE `room` (
  `room_id` varchar(5) NOT NULL,
  `room_name` varchar(45) NOT NULL,
  `description` varchar(400) NOT NULL,
  `loc_id` varchar(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `route`
--

CREATE TABLE `route` (
  `loc_no_from` int(11) NOT NULL,
  `loc_name_from` varchar(45) NOT NULL,
  `loc_no_to` int(11) NOT NULL,
  `loc_name_to` varchar(45) NOT NULL,
  `width` int(11) DEFAULT NULL,
  `weight` int(11) DEFAULT NULL,
  `coordinates` varchar(400) DEFAULT NULL,
  `main_route` enum('Yes','No') DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `route`
--

INSERT INTO `route` (`loc_no_from`, `loc_name_from`, `loc_no_to`, `loc_name_to`, `width`, `weight`, `coordinates`, `main_route`) VALUES
(0, 'Main Gate - Nawala', 1, 'CRC Office', 15, 3, '6.882991,79.886674:6.883321,79.886696', 'Yes'),
(2, 'CRC Office', 3, 'Junction 1', 15, 6, '6.883321,79.886696:6.883552,79.886723:6.883618,79.886696:6.883641,79.886444', 'Yes');

-- --------------------------------------------------------

--
-- Table structure for table `schedule`
--

CREATE TABLE `schedule` (
  `ac_code` varchar(10) NOT NULL,
  `course_code` varchar(10) NOT NULL,
  `group` varchar(10) NOT NULL,
  `medium` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `centre` varchar(10) DEFAULT NULL,
  `loc_id` varchar(5) DEFAULT NULL,
  `room_id` varchar(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `schedule`
--

INSERT INTO `schedule` (`ac_code`, `course_code`, `group`, `medium`, `date`, `start_time`, `end_time`, `centre`, `loc_id`, `room_id`) VALUES
('CAT1', 'EEI6461', '-', 'E', '2019-10-07', '10:30:00', '11:45:00', 'RC', NULL, NULL),
('DS1', 'EEI6461', '-', 'E', '2019-02-25', '10:30:00', '12:30:00', 'CL', NULL, NULL),
('DS2', 'EEI6461', '-', 'E', '2019-05-13', '08:00:00', '10:00:00', 'CL', NULL, NULL),
('DS3', 'EEI6461', '-', 'E', '2019-09-15', '15:30:00', '17:30:00', 'CL', NULL, NULL),
('DS4', 'EEI6461', '-', 'E', '2019-11-04', '13:00:00', '15:00:00', 'CL', NULL, NULL),
('FE1', 'EEI6461', '-', 'E', '2020-02-09', '09:30:00', '12:30:00', 'RC', NULL, NULL),
('MP1', 'EEI6461', '-', 'E', '2019-11-24', '09:00:00', '16:00:00', 'CL', NULL, NULL),
('OQ1', 'EEI6461', '-', 'E', '2019-06-16', '09:00:00', '10:00:00', 'CL', NULL, NULL),
('TMA1', 'EEI6461', '-', 'E', '2019-06-09', '08:00:00', '16:00:00', 'CL', NULL, NULL),
('TMA2', 'EEI6461', '-', 'E', '2019-09-29', '08:00:00', '16:00:00', 'CL', NULL, NULL),
('TMA3', 'EEI6461', '-', 'E', '2019-11-10', '08:00:00', '16:00:00', 'CL', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `sno` varchar(10) NOT NULL,
  `regno` int(10) NOT NULL,
  `nic` varchar(15) NOT NULL,
  `name` varchar(400) NOT NULL,
  `email` varchar(45) NOT NULL,
  `contact` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`sno`, `regno`, `nic`, `name`, `email`, `contact`) VALUES
('S13012418', 415432055, '950243694v', 'Yazeed Hannan', 'yazeed.hannan@gmail.com', 715782085);

-- --------------------------------------------------------

--
-- Table structure for table `student_login`
--

CREATE TABLE `student_login` (
  `sno` varchar(10) NOT NULL,
  `password` varchar(45) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `student_login`
--

INSERT INTO `student_login` (`sno`, `password`) VALUES
('S13012418', 'Yazeed123');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `activity`
--
ALTER TABLE `activity`
  ADD PRIMARY KEY (`ac_code`);

--
-- Indexes for table `building`
--
ALTER TABLE `building`
  ADD PRIMARY KEY (`bul_id`);

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`course_code`);

--
-- Indexes for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD PRIMARY KEY (`sno`,`course_code`),
  ADD KEY `course_StudentCourse_idx` (`course_code`);

--
-- Indexes for table `lecturer`
--
ALTER TABLE `lecturer`
  ADD PRIMARY KEY (`emp_no`),
  ADD UNIQUE KEY `nic_UNIQUE` (`nic`);

--
-- Indexes for table `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`loc_id`);

--
-- Indexes for table `room`
--
ALTER TABLE `room`
  ADD PRIMARY KEY (`room_id`),
  ADD UNIQUE KEY `roomcol_UNIQUE` (`room_name`),
  ADD UNIQUE KEY `room_id_UNIQUE` (`room_id`),
  ADD KEY `locid_LocationRoom_idx` (`loc_id`);

--
-- Indexes for table `route`
--
ALTER TABLE `route`
  ADD PRIMARY KEY (`loc_no_from`,`loc_name_from`,`loc_no_to`,`loc_name_to`);

--
-- Indexes for table `schedule`
--
ALTER TABLE `schedule`
  ADD PRIMARY KEY (`ac_code`,`course_code`,`group`),
  ADD KEY `coursecode_CourseSchedule_idx` (`course_code`),
  ADD KEY `loc_LocationSchedule_idx` (`loc_id`),
  ADD KEY `room_RoomSchedule_idx` (`room_id`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`sno`),
  ADD UNIQUE KEY `regno_UNIQUE` (`regno`),
  ADD UNIQUE KEY `nic_UNIQUE` (`nic`);

--
-- Indexes for table `student_login`
--
ALTER TABLE `student_login`
  ADD PRIMARY KEY (`sno`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `building`
--
ALTER TABLE `building`
  MODIFY `bul_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD CONSTRAINT `course_StudentCourse` FOREIGN KEY (`course_code`) REFERENCES `course` (`course_code`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `sno_StudentCourse` FOREIGN KEY (`sno`) REFERENCES `student` (`sno`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `room`
--
ALTER TABLE `room`
  ADD CONSTRAINT `locid_LocationRoom` FOREIGN KEY (`loc_id`) REFERENCES `location` (`loc_id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

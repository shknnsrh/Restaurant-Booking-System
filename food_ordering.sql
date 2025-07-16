-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 15, 2025 at 09:33 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `food_ordering`
--

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `id` int(11) NOT NULL,
  `item_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`id`, `item_id`, `quantity`) VALUES
(1, 1, NULL),
(2, 2, NULL),
(3, 1, 2),
(4, 2, 1),
(5, 4, 2),
(6, 17, 2),
(7, 16, 1),
(8, 17, 1),
(9, 6, 3),
(10, 20, 1),
(11, 23, 2),
(12, 10, 1),
(13, 11, 1),
(14, 12, 1);

-- --------------------------------------------------------

--
-- Table structure for table `menu_items`
--

CREATE TABLE `menu_items` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menu_items`
--

INSERT INTO `menu_items` (`id`, `name`, `price`, `category`) VALUES
(1, 'Mee Goreng Mamak', 5.50, 'Food'),
(2, 'Nasi Lemak Biasa', 3.00, 'Food'),
(3, 'Mee Rebus', 4.00, 'Food'),
(4, 'Nasi Lemak Ayam', 6.00, 'Food'),
(5, 'Mee Bandung', 6.00, 'Food'),
(6, 'Nasi Lemak Telur', 4.50, 'Food'),
(7, 'Soto', 4.00, 'Food'),
(8, 'Nasi Goreng Kampung', 6.00, 'Food'),
(9, 'Roti Canai', 1.50, 'Food'),
(10, 'Laksa', 5.50, 'Food'),
(11, 'Lontong', 4.00, 'Food'),
(12, 'Ayam Goreng', 3.00, 'Food'),
(13, 'Nasi Ayam', 6.50, 'Food'),
(14, 'Char Kuey Teow', 6.00, 'Food'),
(15, 'Teh Tarik', 2.00, 'Beverage'),
(16, 'Kopi O', 1.80, 'Beverage'),
(17, 'Milo Ais', 2.50, 'Beverage'),
(18, 'Sirap Bandung', 2.20, 'Beverage'),
(19, 'Air Limau Ais', 2.00, 'Beverage'),
(20, 'Teh O Ais', 1.80, 'Beverage'),
(21, 'Nescafe Panas', 2.30, 'Beverage'),
(22, 'Jus Oren', 3.00, 'Beverage'),
(23, 'Air Kosong', 0.50, 'Beverage'),
(24, 'Air Barli', 2.20, 'Beverage'),
(25, 'Ice latte', 10.00, 'Beverage');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `status` varchar(50) DEFAULT 'Pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `status`, `created_at`) VALUES
(1, 'Completed', '2025-06-12 15:15:56'),
(2, 'Completed', '2025-06-12 15:59:40'),
(3, 'Completed', '2025-06-12 16:00:21'),
(4, 'Completed', '2025-06-12 16:26:03'),
(5, 'Completed', '2025-06-12 16:35:56'),
(6, 'Completed', '2025-06-13 05:27:24'),
(7, 'Completed', '2025-06-13 07:26:32'),
(8, 'Completed', '2025-06-13 07:36:03'),
(9, 'Preparing', '2025-06-13 07:36:38'),
(10, 'Completed', '2025-06-13 07:53:22'),
(11, 'Completed', '2025-06-13 07:58:49'),
(12, 'Completed', '2025-06-13 08:01:37'),
(13, 'Completed', '2025-06-13 08:51:00'),
(14, 'Completed', '2025-06-13 08:52:50'),
(15, 'Completed', '2025-06-13 09:29:31'),
(16, 'Completed', '2025-06-13 15:06:14'),
(17, 'Completed', '2025-06-14 08:59:03'),
(18, 'Completed', '2025-06-14 09:24:38'),
(19, 'Completed', '2025-06-14 09:42:00'),
(20, 'Completed', '2025-06-14 09:52:51'),
(21, 'Completed', '2025-06-14 10:42:58'),
(22, 'Completed', '2025-07-15 16:35:40'),
(23, 'Preparing', '2025-07-15 17:22:35'),
(24, 'Pending', '2025-07-15 17:23:12'),
(25, 'Pending', '2025-07-15 17:23:16'),
(26, 'Pending', '2025-07-15 17:25:10'),
(27, 'Pending', '2025-07-15 17:25:45'),
(28, 'Pending', '2025-07-15 17:38:07'),
(29, 'Pending', '2025-07-15 17:38:51'),
(30, 'Pending', '2025-07-15 17:49:00'),
(31, 'Pending', '2025-07-15 18:40:03'),
(32, 'Pending', '2025-07-15 19:05:36'),
(33, 'Pending', '2025-07-15 19:06:28'),
(34, 'Pending', '2025-07-15 19:23:00'),
(35, 'Pending', '2025-07-15 19:30:53'),
(36, 'Pending', '2025-07-15 19:30:57');

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `id` int(11) NOT NULL,
  `order_id` int(11) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`id`, `order_id`, `item_id`, `quantity`) VALUES
(1, 1, 6, 1),
(2, 2, 1, 1),
(3, 2, 2, 1),
(4, 2, 4, 1),
(5, 3, 1, 3),
(6, 4, 10, 3),
(7, 4, 11, 3),
(8, 4, 12, 4),
(9, 5, 2, 3),
(10, 5, 4, 4),
(11, 6, 1, 1),
(12, 6, 2, 1),
(13, 7, 1, 2),
(14, 8, 1, 2),
(15, 9, 5, 5),
(16, 10, 7, 1),
(17, 11, 1, 1),
(18, 11, 15, 1),
(19, 12, 11, 1),
(20, 12, 12, 1),
(21, 13, 1, 1),
(22, 13, 2, 1),
(23, 13, 13, 1),
(24, 14, 2, 1),
(25, 14, 5, 1),
(26, 15, 1, 1),
(27, 15, 2, 1),
(28, 15, 4, 1),
(29, 15, 5, 1),
(30, 16, 1, 1),
(31, 16, 4, 1),
(32, 16, 9, 1),
(33, 17, 1, 1),
(34, 17, 2, 1),
(35, 18, 1, 1),
(36, 18, 2, 1),
(37, 18, 5, 1),
(38, 19, 1, 1),
(39, 20, 4, 1),
(40, 20, 5, 1),
(41, 21, 2, 1),
(42, 21, 16, 1),
(43, 21, 17, 1),
(44, 22, 1, 1),
(45, 22, 2, 1),
(46, 22, 3, 1),
(47, 23, 10, 1),
(48, 24, 10, 1),
(49, 25, 10, 1),
(50, 26, 3, 1),
(51, 27, 1, 1),
(52, 28, 6, 1),
(53, 28, 7, 1),
(54, 28, 25, 1),
(55, 29, 5, 1),
(56, 30, 2, 1),
(57, 31, 1, 1),
(58, 31, 6, 1),
(59, 31, 18, 1),
(60, 31, 20, 1),
(61, 32, 11, 1),
(62, 32, 12, 1),
(63, 33, 3, 1),
(64, 33, 5, 1),
(65, 34, 2, 1),
(66, 34, 5, 1),
(67, 34, 7, 1),
(68, 35, 2, 1),
(69, 35, 3, 1),
(70, 36, 2, 1),
(71, 36, 3, 1);

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `paid_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`id`, `order_id`, `payment_method`, `amount`, `paid_at`) VALUES
(1, 36, 'Cash', 7.00, '2025-07-16 03:30:57');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `menu_items`
--
ALTER TABLE `menu_items`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cart`
--
ALTER TABLE `cart`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `menu_items`
--
ALTER TABLE `menu_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=72;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `menu_items` (`id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `menu_items` (`id`);

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

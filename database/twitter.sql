-- -------------------------------------------------------------
--
-- Eugene Zimin
--
-- Database: twitter
-- Generation Time: 2020-11-12 13:33:56.8490
-- -------------------------------------------------------------


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id` binary(16) NOT NULL,
  `producer_id` binary(16) NOT NULL,
  `content` varchar(140) DEFAULT NULL,
  `created` int DEFAULT NULL,
  PRIMARY KEY (`id`,`producer_id`),
  KEY `fk_messages_producers_idx` (`producer_id`),
  CONSTRAINT `fk_messages_producers1` FOREIGN KEY (`producer_id`) REFERENCES `producers` (`producer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `producers`;
CREATE TABLE `producers` (
  `producer_id` binary(16) NOT NULL,
  PRIMARY KEY (`producer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `subscribers`;
CREATE TABLE `subscribers` (
  `subscriber_id` binary(16) NOT NULL,
  PRIMARY KEY (`subscriber_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `subscriptions`;
CREATE TABLE `subscriptions` (
  `subscriber_id` binary(16) NOT NULL,
  `producer_id` binary(16) NOT NULL,
  PRIMARY KEY (`subscriber_id`,`producer_id`),
  KEY `fk_subscriptions_producers_idx` (`producer_id`),
  KEY `fk_subscriptions_subscribers_idx` (`subscriber_id`),
  CONSTRAINT `fk_subscriptions_producers` FOREIGN KEY (`producer_id`) REFERENCES `producers` (`producer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_subscriptions_subscribers` FOREIGN KEY (`subscriber_id`) REFERENCES `subscribers` (`subscriber_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `messages` (`id`, `producer_id`, `content`, `created`) VALUES
(X'462894B2B5A14088B7AF701C71D6D304', X'6E27EA06A7164C89AF88813749A8BD48', 'Who is subscribed to Mr. Trump?', '1605194769'),
(X'B7A1C8D5C04E4FE38F986392BB751AD9', X'6E27EA06A7164C89AF88813749A8BD48', 'And here Mr. Trump come again', '1605194747'),
(X'DF7E3D8FE19A4458BAD64BE7585200F5', X'1CD89E11602A4186AFBFE0149B59EB08', 'Mr. Macron would like to say hello!', '1605197637'),
(X'E5C6F13A76D648B681F020B74FA9F04C', X'6E27EA06A7164C89AF88813749A8BD48', 'Donald Trump posted his first message', '1605194709'),
(X'FCCC455BFF284DFD8153F07C0F869118', X'1CD89E11602A4186AFBFE0149B59EB08', 'Now France President is here as well', '1605195323');

INSERT INTO `producers` (`producer_id`) VALUES
(X'1CD89E11602A4186AFBFE0149B59EB08'),
(X'6E27EA06A7164C89AF88813749A8BD48');

INSERT INTO `subscribers` (`subscriber_id`) VALUES
(X'70A64B5443C34C18BBEC64590FF7E0CC');

INSERT INTO `subscriptions` (`subscriber_id`, `producer_id`) VALUES
(X'70A64B5443C34C18BBEC64590FF7E0CC', X'1CD89E11602A4186AFBFE0149B59EB08');



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
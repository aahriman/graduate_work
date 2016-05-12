use transcripter;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `nick_name` varchar(32) NOT NULL UNIQUE,
  `email` text NOT NULL,
  `score` int(11) NOT NULL DEFAULT '0',
  `google_id` varchar(255) NOT NULL UNIQUE,
  `selected_language` varchar(3) DEFAULT 'cs'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

CREATE TABLE IF NOT EXISTS `records` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `path_audio` text NOT NULL,
  `path_fst` text NOT NULL,
  `sending_data` text,
  `language` varchar(3),
  `transcript` text
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

CREATE TABLE IF NOT EXISTS `responses` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` int(11) NOT NULL ,
  `record_id` int(11) NOT NULL ,
  `user_response` text NOT NULL,
  `answer` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `correct` int(1) NOT NULl DEFAULT '0',
  CONSTRAINT fk_user_id FOREIGN KEY (`user_id`) REFERENCES users(`id`),
  CONSTRAINT fk_record_id FOREIGN KEY (`record_id`) REFERENCES records(`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

-- `2fa_microservice` database definition
CREATE DATABASE IF NOT EXISTS `2fa_microservice`;

-- `2fa_microservice`.`user` table definition
CREATE TABLE IF NOT EXISTS `user` (
  `mfa_enabled` bit(1) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `secret` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB /*AUTO_INCREMENT=11*/ DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- `2fa_microservice`.`user` data definition   -- admin password: admin123    -- TestUserMFA password: testuser123
INSERT INTO `2fa_microservice`.`user` (mfa_enabled,email,password,`role`,secret,username) VALUES
	 (0,'admin@company.org','$2a$10$cKDF/ZMl.dUO7Kk9ns8qD.AgW5YKL9wxEbAS7wqBUbHyT0kfb/hiW','ADMIN',NULL,'admin'),
	 (1,'testUserMFA@company.org','$2a$10$llHWb8laNeGf3jo1MKDf1eaHJlSX3QuLnrkGCaAb1tdN2AoFG2K.m','USER',
	 'FHPGGFN7BMWCIMVZ25H4YIBZAXJ5N3JMAJOLSWR2KMAUSPGWAYDEOEQRJX42KK6U','TestUserMFA');

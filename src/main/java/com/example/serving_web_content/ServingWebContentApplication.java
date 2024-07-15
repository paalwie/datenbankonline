package com.example.serving_web_content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
@ComponentScan("com.example.serving_web_content")
public class ServingWebContentApplication {

	//Version 1.02

	public static void main(String[] args) {
		SpringApplication.run(ServingWebContentApplication.class, args);

		// Testen der Datenbankverbindung
		testDatabaseConnection();
	}

	private static void testDatabaseConnection() {
		try {
			// Verbindung zur Datenbank herstellen
			DriverManager.getConnection(
					"jdbc:postgresql://ep-gentle-dew-a2m722pi.eu-central-1.aws.neon.tech/neondb",
					"neondb_owner",
					"6QyuJaIPBs7f"
			);

			System.out.println("Verbindung zur Neon-Datenbank erfolgreich hergestellt!");
		} catch (SQLException e) {
			System.err.println("Fehler beim Herstellen der Verbindung zur Neon-Datenbank: " + e.getMessage());
		}
	}
}


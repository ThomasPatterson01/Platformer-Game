package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	
	private static String dbPath = "jdbc:sqlite:./db/database.db";
	
	//execute and return the result of an sql statement
	public static ResultSet executeSQL(String sql) {
		try {
			Connection conn = DriverManager.getConnection(dbPath);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
}

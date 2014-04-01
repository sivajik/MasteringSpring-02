package com.mastspring.lesson07;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClassicBoringJDBC {
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // BP
			Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test"); //BP
			String sql = "select petname, petowner, species from tbl_ourpets";
			Statement stmt = conn.createStatement(); //BP
			ResultSet rs = stmt.executeQuery(sql); //BP
			
			while (rs.next()) {
				String name = rs.getString(1);
				String owner = rs.getString(2);
				String species = rs.getString(3);
				System.out.println(name + " , " + owner + " , " + species);
			}
			rs.close(); //BP
			stmt.close(); //BP
			conn.close(); //BP
		} catch (ClassNotFoundException e) { //BP
			e.printStackTrace(); //BP
		} catch (SQLException e) { //BP
			e.printStackTrace(); //BP
		}
	}
}
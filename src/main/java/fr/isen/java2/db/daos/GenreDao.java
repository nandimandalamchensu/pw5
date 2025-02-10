package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

import javax.sql.DataSource;

public class GenreDao {
	private final DataSource dataSource;

	public GenreDao(){
		this.dataSource = DataSourceFactory.getDataSource();
	}

	public List<Genre> listGenres() {
		List<Genre> allgenre = new ArrayList<>();
		String sql = "SELECT * FROM genre";
		try(Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql)){
			while(rs.next()){
				allgenre.add(new Genre(rs.getInt("idgenre"),rs.getString("name")));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return allgenre;
	}

	public Genre getGenre(String name) {
	    String sql = "SELECT * FROM genre WHERE name = ?";
	    try (Connection conn = dataSource.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setString(1, name);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return new Genre(rs.getInt("idgenre"), rs.getString("name"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // Return null if no genre is found
	}


	public void addGenre(String name) {
	    String sql = "INSERT INTO genre(name) VALUES(?)";
	    try (Connection conn = dataSource.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        
	        pstmt.setString(1, name);
	        pstmt.executeUpdate(); 

	        // Retrieve the generated ID (optional)
	        try (ResultSet rs = pstmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                int generatedId = rs.getInt(1);
	                System.out.println("Inserted genre with ID: " + generatedId);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

}

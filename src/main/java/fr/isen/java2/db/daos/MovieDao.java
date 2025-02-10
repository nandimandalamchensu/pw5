package fr.isen.java2.db.daos;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

import javax.sql.DataSource;

public class MovieDao {
	private final DataSource dataSource;

	public MovieDao() {
		this.dataSource = DataSourceFactory.getDataSource();
	}

	public List<Movie> listMovies() {
		List<Movie> allMovies = new ArrayList<>();
		String sql = "SELECT movie.idmovie, movie.title, movie.release_date, movie.duration, " +
				"movie.director, movie.summary, genre.idgenre, genre.name AS genre_name " +
				"FROM movie " +
				"JOIN genre ON movie.genre_id = genre.idgenre";

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				Genre genre = new Genre(rs.getInt("idgenre"), rs.getString("genre_name"));

				// Corrected: Properly retrieving release_date
				LocalDate releaseDate = rs.getDate("release_date") != null ?
						rs.getDate("release_date").toLocalDate() : null;

				Movie movie = new Movie(
						rs.getInt("idmovie"),
						rs.getString("title"),
						releaseDate,
						genre,
						rs.getInt("duration"),
						rs.getString("director"),
						rs.getString("summary")
				);

				allMovies.add(movie);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return allMovies;
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> moviesByGenre = new ArrayList<>();
		String sql = "SELECT movie.idmovie, movie.title, movie.release_date, movie.duration, " +
				"movie.director, movie.summary, genre.idgenre, genre.name AS genre_name " +
				"FROM movie " +
				"JOIN genre ON movie.genre_id = genre.idgenre " +
				"WHERE genre.name = ?";

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, genreName);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Genre genre = new Genre(rs.getInt("idgenre"), rs.getString("genre_name"));

					// Corrected: Properly retrieving release_date
					LocalDate releaseDate = rs.getDate("release_date") != null ?
							rs.getDate("release_date").toLocalDate() : null;

					Movie movie = new Movie(
							rs.getInt("idmovie"),
							rs.getString("title"),
							releaseDate,
							genre,
							rs.getInt("duration"),
							rs.getString("director"),
							rs.getString("summary")
					);

					moviesByGenre.add(movie);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return moviesByGenre;
	}

	public Movie addMovie(Movie movie) {
		String sql = "INSERT INTO movie(title, release_date, genre_id, duration, director, summary) " +
				"VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, movie.getTitle());

			// Properly handling NULL values
			if (movie.getReleaseDate() != null) {
				pstmt.setDate(2, Date.valueOf(movie.getReleaseDate()));
			} else {
				pstmt.setNull(2, Types.DATE);
			}

			pstmt.setInt(3, movie.getGenre().getId());
			pstmt.setInt(4, movie.getDuration());
			pstmt.setString(5, movie.getDirector());
			pstmt.setString(6, movie.getSummary());

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						int generatedId = rs.getInt(1);
						movie.setId(generatedId);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movie;
	}
}
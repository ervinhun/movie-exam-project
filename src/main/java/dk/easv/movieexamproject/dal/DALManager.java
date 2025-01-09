package dk.easv.movieexamproject.dal;

import dk.easv.movieexamproject.be.Category;
import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.bll.BLLManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DALManager
{
    private ConnectionManager connectionManager;
    private BLLManager bllManager;

    public DALManager(BLLManager bllManager)
    {
        this.connectionManager = new ConnectionManager();
        this.bllManager = bllManager;
    }

    public void deleteMovie(Movie movieToDelete) {
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandInsert = "DELETE FROM CatMovie WHERE MovieId = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, movieToDelete.getId());
            pstmtSelect.execute();
            sqlcommandInsert = "DELETE FROM Movie WHERE id = ?";
            pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, movieToDelete.getId());
            pstmtSelect.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void retrieveMovie()
    {
        String query = "SELECT * FROM Movie";

        try
        {
            Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("name");
                float imdbRating = resultSet.getFloat("rating");
                float userRating = resultSet.getFloat("own_rating");
                String fileLink = resultSet.getString("filelink");
                Date lastView = resultSet.getDate("lastview");
                Boolean favorite = resultSet.getBoolean("favorite");


                List<String> categories = new ArrayList<>();
                String categoryQuery =  "SELECT c.name FROM Category c " +
                                        "JOIN CatMovie cm ON cm.CategoryId = c.id " +
                                        "WHERE cm.MovieId = ?";

                try
                {
                    PreparedStatement categoryStatement = connection.prepareStatement(categoryQuery);
                    categoryStatement.setInt(1, id);
                    ResultSet categoryResultSet = categoryStatement.executeQuery();

                    while (categoryResultSet.next())
                    {
                        categories.add(categoryResultSet.getString("name"));
                    }
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }

                Movie movie = new Movie(id, title, imdbRating, userRating, categories.toArray(new String[0]), lastView, fileLink, favorite);
                bllManager.showMovie(movie);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandSelect = "SELECT * FROM Category";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name"))
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categories;
    }

    public void addCategory(String name) {
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandInsert = "INSERT INTO Category (name) VALUES (?)";
            PreparedStatement pstmtInsert = con.prepareStatement(sqlcommandInsert);
            pstmtInsert.setString(1, name);
            pstmtInsert.execute();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addMovie(String name, float IMDB, float userRating, int[] categories, String fileLink, boolean favorite) {
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandInsert = "INSERT INTO Movie (name, rating, filelink, own_rating, favorite) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmtInsert = con.prepareStatement(sqlcommandInsert, Statement.RETURN_GENERATED_KEYS);
            pstmtInsert.setString(1, name);
            pstmtInsert.setFloat(2, IMDB);
            pstmtInsert.setString(3, fileLink);
            pstmtInsert.setFloat(4, userRating);
            pstmtInsert.setBoolean(5, favorite);

            pstmtInsert.executeUpdate();

            ResultSet generatedKeys = pstmtInsert.getGeneratedKeys();
            if (generatedKeys.next()) {
                int movieId = generatedKeys.getInt(1);


                String sqlInsertCatMovie = "INSERT INTO CatMovie (MovieId, CategoryId) VALUES (?, ?)";
                PreparedStatement pstmtInsertCatMovie = con.prepareStatement(sqlInsertCatMovie);


                for (int categoryId : categories) {
                    pstmtInsertCatMovie.setInt(1, movieId);
                    pstmtInsertCatMovie.setInt(2, categoryId);
                    pstmtInsertCatMovie.executeUpdate();
                }
            } else {
                throw new SQLException("Failed to retrieve the generated movie ID.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public List<Movie> getMoviesToNotify() {
        List<Movie> moviesToNotify = new ArrayList<>();
        String sql = """
        SELECT * 
        FROM Movie
        WHERE own_rating < 6 
           OR lastview IS NULL 
           OR DATEDIFF(YEAR, lastview, GETDATE()) >= 2
    """;

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("name");
                float imdbRating = rs.getFloat("rating");
                float userRating = rs.getFloat("own_rating");
                String fileLink = rs.getString("filelink");
                Date lastView = rs.getDate("lastview");
                boolean favorite = rs.getBoolean("favorite");

                // Retrieve categories for the current movie:
                List<String> categories = new ArrayList<>();
                String catQuery = """
                SELECT c.name 
                FROM Category c 
                JOIN CatMovie cm ON cm.CategoryId = c.id 
                WHERE cm.MovieId = ?
            """;
                try (PreparedStatement catStmt = conn.prepareStatement(catQuery)) {
                    catStmt.setInt(1, id);
                    try (ResultSet catRs = catStmt.executeQuery()) {
                        while (catRs.next()) {
                            categories.add(catRs.getString("name"));
                        }
                    }
                }

                Movie movie = new Movie(
                        id, title, imdbRating, userRating,
                        categories.toArray(new String[0]),
                        lastView, fileLink, favorite
                );
                moviesToNotify.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moviesToNotify;
    }

    public void updateLastView(Movie movie) {
        try (Connection con = connectionManager.getConnection()) {
            String SQLSelect = "UPDATE Movie SET lastview = CONVERT (date, GETDATE()) WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(SQLSelect);
            pstmt.setInt(1, movie.getId());
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkIfFavorite(Movie movie) {
        try (Connection con = connectionManager.getConnection()) {
            String SQLSelect = "SELECT favorite FROM Movie WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(SQLSelect);
            pstmt.setInt(1, movie.getId());

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("favorite");
            } else {
                throw new SQLException("Movie with ID " + movie.getId() + " not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error checking if movie is favorite", ex);
        }
    }

    public void toggleFavorite(Movie movie) {
        try (Connection con = connectionManager.getConnection()) {
            String SQLSelect;

            if (!checkIfFavorite(movie)) {
                SQLSelect = "UPDATE Movie SET favorite = 1 WHERE id = ?";
            } else {
                SQLSelect = "UPDATE Movie SET favorite = 0 WHERE id = ?";
            }

            PreparedStatement pstmt = con.prepareStatement(SQLSelect);
            pstmt.setInt(1, movie.getId());
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteCategory(Category category) {
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandInsert = "DELETE FROM CatMovie WHERE CategoryId = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, category.getId());
            pstmtSelect.execute();
            sqlcommandInsert = "DELETE FROM Category WHERE id = ?";
            pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, category.getId());
            pstmtSelect.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    }
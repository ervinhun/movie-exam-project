package dk.easv.movieexamproject.dal;

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
                float userRating = resultSet.getFloat("userRating");
                String fileLink = resultSet.getString("fileLink");
                Date lastView = resultSet.getDate("lastView");

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

                Movie movie = new Movie(id, title, imdbRating, userRating, categories.toArray(new String[0]), lastView, fileLink);
                bllManager.showMovie(movie);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}

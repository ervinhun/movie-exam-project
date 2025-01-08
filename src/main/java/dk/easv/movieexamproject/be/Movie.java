package dk.easv.movieexamproject.be;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Date;

public class Movie
{
    private int id;
    private String title;
    private float IMDB;
    private float userRating;
    private String[] categories;
    private Date lastView;
    private String fileLink;


    public Movie(int id, String title, float IMDB, float userRating, String[] categories, Date lastView, String fileLink)
    {
        this.id = id;
        this.title = title;
        this.IMDB = IMDB;
        this.userRating = userRating;
        this.categories = categories;
        this.lastView = lastView;
        this.fileLink = fileLink;
    }

    public String getTitle()
    {
        return title;
    }

    public float getIMDB()
    {
        return IMDB;
    }

    public float getUserRating()
    {
        return userRating;
    }

    public String[] getCategories()
    {
        return categories;
    }

    public Date getLastView()
    {
        return lastView;
    }
}

package dk.easv.movieexamproject.be;

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
}

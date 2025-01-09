package dk.easv.movieexamproject.bll;

import dk.easv.movieexamproject.MovieController;
import dk.easv.movieexamproject.be.Category;
import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.dal.ChooseFile;
import dk.easv.movieexamproject.dal.DALManager;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

public class BLLManager {

    private ChooseFile fileBrowser;
    private DALManager dalManager;
    private MovieController movieController;

    public BLLManager(MovieController movieController)
    {
        dalManager = new DALManager(this);
        this.movieController = movieController;
        dalManager.retrieveMovie();
    }

    public String openFile(Window window) {
        fileBrowser = new ChooseFile(window);

        if (fileBrowser.getSelectedFilePath() != null) {
            return fileBrowser.getSelectedFilePath();
        }
        return null;
    }

    public void showMovie(Movie movie)
    {
        movieController.setMovie(movie);
    }

    public List<Category> getAllCategories() {
        return dalManager.getAllCategories();
    }

    public void addCategory (String name) {
        dalManager.addCategory(name);
    }

    public void refreshMovieList()
    {
        dalManager.retrieveMovie();
    }

    public void addMovie(String name, float IMDB, float userRating, int[] categories, String fileLink, boolean favorite) {
        dalManager.addMovie(name, IMDB, userRating, categories, fileLink, favorite);
    }
    public ArrayList<Movie> getWarning() {
        if (dalManager.getOldMovies() != null && dalManager.getOldMovies().size() > 0) {
            ArrayList<Movie> warning = new ArrayList<>(dalManager.getOldMovies());
            return warning;
        }
        else return null;
    }

}

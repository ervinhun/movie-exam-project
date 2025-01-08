package dk.easv.movieexamproject.bll;

import dk.easv.movieexamproject.MovieController;
import dk.easv.movieexamproject.be.Category;
import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.dal.ChooseFile;
import dk.easv.movieexamproject.dal.DALManager;
import javafx.stage.Window;

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
}

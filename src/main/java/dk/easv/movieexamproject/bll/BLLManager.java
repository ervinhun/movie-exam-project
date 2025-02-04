package dk.easv.movieexamproject.bll;

import dk.easv.movieexamproject.pl.MovieController;
import dk.easv.movieexamproject.be.Category;
import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.dal.ChooseFile;
import dk.easv.movieexamproject.dal.DALManager;
import dk.easv.movieexamproject.dal.DeleteFile;
import javafx.stage.Window;

import java.util.List;

public class BLLManager {

    private ChooseFile fileBrowser;
    private DALManager dalManager;
    private MovieController movieController;

    public BLLManager(MovieController movieController)
    {
        dalManager = new DALManager();
        this.movieController = movieController;
    }

    public String openFile(Window window) {
        fileBrowser = new ChooseFile(window);

        if (fileBrowser.getSelectedFilePath() != null) {
            return fileBrowser.getSelectedFilePath();
        }
        return null;
    }

    public List<Movie> refreshMovieList(int id, boolean isRefreshingAll)
    {
        return dalManager.retrieveMovies(id, isRefreshingAll);
    }

    public List<Category> getAllCategories() {
        return dalManager.getAllCategories();
    }

    public void addCategory (String name) {
        dalManager.addCategory(name);
    }

    public void addMovie(String name, float IMDB, float userRating, int[] categories, String fileLink, boolean favorite) {
        dalManager.addMovie(name, IMDB, userRating, categories, fileLink, favorite);
    }
    public List<Movie> getMoviesToNotify() {return dalManager.getMoviesToNotify();}


    public void deleteMovie(Movie movieToDelete, Boolean fileDelete) {
        if (fileDelete) {
            DeleteFile delete = new DeleteFile();
            try {
                delete.deleteFile(movieToDelete.getFileLink());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        dalManager.deleteMovie(movieToDelete);
    }

    public void updateLastView(Movie movie) {
        dalManager.updateLastView(movie);
    }

    public void toggleFavorite(Movie movie) {
        dalManager.toggleFavorite(movie);
    }

    public void deleteCategory(Category category) {
        dalManager.deleteCategory(category);
    }

    public void updateMovie(Movie movie, String newTitle, float newImdbRating,
                            float newUserRating, int[] newCategories,
                            String newFileLink, boolean newFavorite)
    {
        dalManager.updateMovieInDB(movie.getId(),
                newTitle,
                newImdbRating,
                newUserRating,
                newCategories,
                newFileLink,
                newFavorite);
    }
}

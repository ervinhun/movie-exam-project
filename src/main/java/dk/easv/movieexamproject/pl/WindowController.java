package dk.easv.movieexamproject.pl;

import dk.easv.movieexamproject.be.Category;
import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.bll.BLLManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.util.List;

public class WindowController extends TableViewController
{

    @FXML
    protected void openHideRating() {
        vBoxRating.setVisible(!vBoxRating.isVisible());
        vBoxRating.setManaged(vBoxRating.isVisible());
        if (vBoxRating.isVisible()) {
            lblFilterRating.setText(RATING_LABEL + UP_ARROW);
        } else {
            lblFilterRating.setText(RATING_LABEL + DOWN_ARROW);
        }
    }

    @FXML
    protected void openHideCategory() {
        lstCategory.setVisible(!lstCategory.isVisible());
        lstCategory.setManaged(lstCategory.isVisible());
        if (lstCategory.isVisible()) {
            lblFilterCat.setText(CATEGORY_LABEL + UP_ARROW);
        } else {
            lblFilterCat.setText(CATEGORY_LABEL + DOWN_ARROW);
        }
    }

    protected void showAddMovie()
    {
        populateCategories();
        popUpBg.setVisible(true);
        popupNewMovie.setVisible(true);
    }

    @FXML protected void hideAddMovie() {
        txtFilePath.setText("");
        txtMovieTitle.setText("");
        txtImdb.setText("");
        txtUserScore.setText("");
        categoriesListView.getItems().clear();
        popUpBg.setVisible(false);
        popupNewMovie.setVisible(false);
    }

    @FXML
    protected void delMovieConfirm() {
        Boolean fileDelete = cbDeleteFile.isSelected();
        if (movieToDelete != null) {
            manager.deleteMovie(movieToDelete, fileDelete);
            items.remove(movieToDelete);
            hideDelMoviePopUp();
        }
    }

    @FXML
    protected void choosePath() {
        try
        {
            String filepath = manager.openFile(btnChoose.getScene().getWindow());
            txtFilePath.setText(filepath);
        }
        catch(Exception _)
        {}
    }

    @FXML
    protected void showAddCategory()
    {
        popUpBg.setVisible(true);
        popupVBoxCat.setVisible(true);
    }

    @FXML protected void hideAddCategory()
    {
        txtNewCategory.setText("");
        popUpBg.setVisible(false);
        popupVBoxCat.setVisible(false);
    }

    @FXML
    protected void showDelCategory() {
        popUpBg.setVisible(true);
        vbDeleteCategory.setVisible(true);
        lstCategoryDelete.getItems().clear();
        lstCategoryDelete.getItems().addAll(categories);
    }

    @FXML
    protected void hideDelCategory()
    {
        lstCategoryDelete.getItems().clear();
        vbDeleteCategory.setVisible(false);
        popUpBg.setVisible(false);
    }

    @FXML
    protected void showDelCategoryConfirm()
    {
        popupDeleteCategoryConfirmation.setVisible(true);
        String categoryName = lstCategoryDelete.getSelectionModel().getSelectedItem().toString();
        lblDeletingCategory.setText("Are you sure you want to delete " + categoryName + "?");
    }

    @FXML
    protected void hideDelCategoryConfirm() {
        popupDeleteCategoryConfirmation.setVisible(false);
    }


    protected void checkObsoleteMovies(BLLManager manager)
    {
        List<Movie> obsoleteMovies = manager.getMoviesToNotify();

        if (!obsoleteMovies.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Obsolete Movies");
            alert.setHeaderText("Some movies may need to be deleted!");

            StringBuilder sb = new StringBuilder("These movies have rating < 6 or haven't been viewed in 2+ years:\n\n");
            for (Movie m : obsoleteMovies) {
                sb.append("- ").append(m.getTitle()).append("\n");
            }
            alert.setContentText(sb.toString());
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/dk/easv/movieexamproject/css/red.css").toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add("dialog-pane");
            alert.showAndWait();
        }
    }

    @FXML
    protected void delCategoryConfirm() {
        Category category = lstCategoryDelete.getSelectionModel().getSelectedItem();
        manager.deleteCategory(category);
        categories.remove(category);
        clearFilters();
        lstCategory.getItems().removeIf(checkBox -> checkBox.getText().equals(category.getName()));
        hideDelCategoryConfirm();
        hideDelCategory();
        refreshMovies(-1, -1, true);
    }

    @FXML
    protected void delMoviePopUp(Movie movie) {
        popUpBg.setVisible(true);
        popupDeleteMovie.setVisible(true);
        lblDeletingMovie.setText(DELETING_MOVIE_LABEL + movie.getTitle());
        movieToDelete = movie;
    }

    protected void hideDelMoviePopUp() {
        lblDeletingMovie.setText(DELETING_MOVIE_LABEL);
        popupDeleteMovie.setVisible(false);
        popUpBg.setVisible(false);
    }
}

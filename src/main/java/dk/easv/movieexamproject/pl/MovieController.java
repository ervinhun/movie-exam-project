package dk.easv.movieexamproject.pl;

import dk.easv.movieexamproject.be.Category;
import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.bll.BLLManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MovieController implements Initializable
{
    @FXML
    protected CheckBox cbFavourite;
    @FXML
    protected VBox vBoxRating;
    @FXML
    protected ListView<CheckBox> lstCategory;
    @FXML
    private RadioButton radio2;
    @FXML
    private RadioButton radio3;
    @FXML
    private RadioButton radio4;
    @FXML
    private RadioButton radio5;
    @FXML
    private RadioButton radio6;
    @FXML
    private RadioButton radio7;
    @FXML
    private RadioButton radio8;
    @FXML
    private RadioButton radio9;
    @FXML
    protected VBox popUpBg;
    @FXML
    protected VBox popupVBoxCat;
    @FXML
    protected TextField txtNewCategory;
    @FXML
    protected VBox popupNewMovie;
    @FXML
    protected VBox vbDeleteCategory;
    @FXML
    protected ListView<Category> lstCategoryDelete;
    @FXML
    protected ListView<Category> categoriesListView;
    @FXML
    protected TextField txtFilePath;
    @FXML
    protected TextField txtMovieTitle;
    @FXML
    protected TextField txtImdb;
    @FXML
    protected TextField txtUserScore;
    @FXML
    protected Label lblFilterCat;
    @FXML
    protected Label lblFilterRating;
    @FXML
    protected TextField searchField;
    @FXML
    private Button btnClose;
    @FXML
    protected VBox popupDeleteCategoryConfirmation;
    @FXML
    protected Label lblDeletingCategory;
    @FXML
    protected Button btnChoose;
    @FXML
    protected TableView<Movie> moviesTable;
    @FXML
    protected TableColumn<Movie, Void> clmPlay;
    @FXML
    protected TableColumn<Movie, String> clmTitle;
    @FXML
    protected TableColumn<Movie, String> clmImdb;
    @FXML
    protected TableColumn<Movie, String> clmUserRating;
    @FXML
    protected TableColumn<Movie, String> clmCategories;
    @FXML
    protected TableColumn<Movie, String> clmLastView;
    @FXML
    protected TableColumn<Movie, Void> clmControl;
    @FXML
    private Button btnSaveMovie;
    @FXML
    protected VBox popupDeleteMovie;
    @FXML
    protected Label lblDeletingMovie;
    @FXML
    protected CheckBox cbDeleteFile;
    protected ObservableList<Movie> items = FXCollections.observableArrayList();
    protected FilteredList<Movie> filteredItems = new FilteredList<>(items);
    protected ObservableList<Category> categories = FXCollections.observableArrayList();

    protected final ToggleGroup ratingGroup = new ToggleGroup();
    protected Movie movieToDelete;

    protected static final String CATEGORY_LABEL = "Category ";
    protected static final String RATING_LABEL = "IMDB minimum rating ";
    protected static final String UP_ARROW = "▲";
    protected static final String DOWN_ARROW = "▼";
    protected static final String DELETING_MOVIE_LABEL = "Are you sure you want to delete ";

    protected BLLManager manager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        movieToDelete = null;
        manager = new BLLManager(this);
        refreshMovies(-1, -1, true);
        groupIMDBScore();
        setCategories();
        setFavorite();
        setUpMoviesTable();
        WindowController windowController = new WindowController();
        windowController.checkObsoleteMovies(manager);
    }

    protected void updateFavoriteIcon(Movie movie, Button favoriteButton) {
        if (movie.isFavorite()) {
            favoriteButton.setStyle("-fx-background-image: url('/dk/easv/movieexamproject/img/fav1.png')");
        } else {
            favoriteButton.setStyle("-fx-background-image: url('/dk/easv/movieexamproject/img/fav0.png')");
        }
    }


    protected void editMovie(Movie movie, int itemID) {
        showAddMovie();
        populateCategories();

        txtFilePath.setText(movie.getFileLink());
        txtMovieTitle.setText(movie.getTitle());
        txtImdb.setText(String.valueOf(movie.getIMDB()));
        txtUserScore.setText(String.valueOf(movie.getUserRating()));

        for (Category category : categoriesListView.getItems()) {
            for (String catName : movie.getCategories()) {
                if (category.getName().equals(catName)) {
                    categoriesListView.getSelectionModel().select(category);
                }
            }
        }

        btnSaveMovie.setOnAction(_ -> editMovieSave(movie, itemID));
    }

    private void editMovieSave(Movie movie, int itemID) {
        String newTitle = txtMovieTitle.getText();
        String newFilePath = txtFilePath.getText();
        float newImdb;
        float newUserScore;

        try {
            newImdb = Float.parseFloat(txtImdb.getText());
            newUserScore = Float.parseFloat(txtUserScore.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in IMDb or user score fields.");
            return;
        }

        ObservableList<Category> selectedCategories = categoriesListView.getSelectionModel().getSelectedItems();
        int[] categoryIds = selectedCategories.stream()
                .mapToInt(Category::getId)
                .toArray();

        manager.updateMovie(movie, newTitle, newImdb, newUserScore, categoryIds, newFilePath, movie.isFavorite());

        hideAddMovie();

        refreshMovies(movie.getId(), itemID, false);
    }

    private void setCategories() {
        categories.addAll(manager.getAllCategories());
        for (Category category : categories) {
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(false);
            checkBox.setText(category.getName());
            checkBox.setOnAction(_ -> addFilters());
            lstCategory.getItems().add(checkBox);

        }
    }

    private void setFavorite() {
        cbFavourite.setSelected(false);
        cbFavourite.setOnAction(_ -> addFilters());
    }

    private void groupIMDBScore() {
        RadioButton[] radioButtons = {radio2, radio3, radio4, radio5, radio6, radio7, radio8, radio9};
        for (RadioButton radioButton : radioButtons) {
            radioButton.setToggleGroup(ratingGroup);
        }
    }

    protected void populateCategories() {
        categoriesListView.getItems().clear();
        for (Category category : manager.getAllCategories()) {
            categoriesListView.getItems().add(category);
        }
        categoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void saveMovie() {
        String title = txtMovieTitle.getText();
        String filePath = txtFilePath.getText();

        float imdbRating;
        float userScore;

        try {
            imdbRating = Float.parseFloat(txtImdb.getText());
            userScore = Float.parseFloat(txtUserScore.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in IMDb or user score fields.");
            return;
        }

        ObservableList<Category> selectedCategories = categoriesListView.getSelectionModel().getSelectedItems();

        if (selectedCategories.isEmpty()) {
            System.out.println("No categories selected.");
            return;
        }

        // Transform selected categories to an array of IDs
        int[] categoryIds = selectedCategories.stream()
                .mapToInt(Category::getId)
                .toArray();

        manager.addMovie(title, imdbRating, userScore, categoryIds, filePath, false);
        hideAddMovie();
        refreshMovies(-1, -1, true);
    }

    @FXML
    private void saveCategory() {
        manager.addCategory(txtNewCategory.getText());
        CheckBox checkBox = new CheckBox();
        categories.add(new Category(categories.getLast().getId()+1, txtNewCategory.getText()));
        checkBox.setText(txtNewCategory.getText());
        checkBox.setOnAction(_ -> addFilters());
        lstCategory.getItems().add(checkBox);
        hideAddCategory();
    }

    protected void refreshMovies(int movieID, int itemsListID, boolean isRefreshingAll) {
        if (isRefreshingAll) {
            items.clear();
            items.addAll(manager.refreshMovieList(-1, true));
        } else {
            items.set(itemsListID, manager.refreshMovieList(movieID, false).getFirst());
        }
    }

    @FXML
    private void exitApp() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    //Managed by TableViewController
    protected void setUpMoviesTable() {}

    protected void openImdbPage(Movie movie) throws Exception {}

    //Managed by FilterController

    @FXML
    protected void addFilters() {}

    @FXML
    protected void clearFilters() {}

    //Managed by WindowController

    @FXML
    protected void openHideRating() {}

    @FXML
    protected void openHideCategory() {}

    @FXML
    protected void showAddMovie() {}

    @FXML
    protected void hideAddMovie() {}

    @FXML
    protected void showAddCategory() {}

    @FXML
    protected void hideAddCategory() {}

    @FXML
    protected void delMovieConfirm() {}

    @FXML
    protected void showDelCategory() {}

    @FXML
    protected void hideDelCategoryConfirm() {}

    @FXML
    protected void showDelCategoryConfirm() {}

    @FXML
    protected void delCategoryConfirm() {}

    @FXML
    protected void hideDelCategory() {}

    protected void delMoviePopUp(Movie movie) {}

    @FXML
    protected void hideDelMoviePopUp() {}

    @FXML
    protected void choosePath() {}
}
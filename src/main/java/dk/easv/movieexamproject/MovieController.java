package dk.easv.movieexamproject;

import dk.easv.movieexamproject.be.Category;
import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.bll.BLLManager;
import dk.easv.movieexamproject.dal.PlayingExt;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MovieController implements Initializable {
    @FXML private CheckBox cbFavourite;
    @FXML private VBox vBoxRating;
    @FXML private ListView<CheckBox> lstCategory;
    @FXML private RadioButton radio2;
    @FXML private RadioButton radio3;
    @FXML private RadioButton radio4;
    @FXML private RadioButton radio5;
    @FXML private RadioButton radio6;
    @FXML private RadioButton radio7;
    @FXML private RadioButton radio8;
    @FXML private RadioButton radio9;
    @FXML private VBox popUpBg;
    @FXML private VBox popupVBoxCat;
    @FXML private TextField txtNewCategory;
    @FXML private VBox popupNewMovie;
    @FXML private VBox vbDeleteCategory;
    @FXML private ListView<Category> lstCategoryDelete;
    @FXML private ListView<Category> categoriesListView;
    @FXML private TextField txtFilePath;
    @FXML private TextField txtMovieTitle;
    @FXML private TextField txtImdb;
    @FXML private TextField txtUserScore;
    @FXML private Label lblFilterCat;
    @FXML private Label lblFilterRating;
    @FXML private TextField searchField;
    @FXML private Button btnClose;
    @FXML private VBox popupDeleteCategoryConfirmation;
    @FXML private Label lblDeletingCategory;
    @FXML private Button btnChoose;
    @FXML private TableView<Movie> moviesTable;
    @FXML private TableColumn<Movie, Void> clmPlay;
    @FXML private TableColumn<Movie, String> clmTitle;
    @FXML private TableColumn<Movie, String> clmImdb;
    @FXML private TableColumn<Movie, String> clmUserRating;
    @FXML private TableColumn<Movie, String> clmCategories;
    @FXML private TableColumn<Movie, String> clmLastView;
    @FXML private TableColumn<Movie, Void> clmControl;
    @FXML private Button btnSaveMovie;
    @FXML private VBox popupDeleteMovie;
    @FXML private Label lblDeletingMovie;
    @FXML private CheckBox cbDeleteFile;
    private ObservableList<Movie> items = FXCollections.observableArrayList();
    private FilteredList<Movie> filteredItems = new FilteredList<>(items);
    private ObservableList<Category> categories = FXCollections.observableArrayList();

    private final ToggleGroup ratingGroup = new ToggleGroup();
    private Movie movieToDelete;

    private static final String CATEGORY_LABEL = "Category ";
    private static final String RATING_LABEL = "IMDB minimum rating ";
    private static final String UP_ARROW = "▲";
    private static final String DOWN_ARROW = "▼";
    private static final String DELETING_MOVIE_LABEL = "Are you sure you want to delete ";

    private BLLManager manager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //CreateImdbScore score = new CreateImdbScore(vBoxRating);
        movieToDelete = null;
        manager = new BLLManager(this);
        groupIMDBScore();
        setCategories();
        setFavorite();
        setUpMoviesTable();
        checkObsoleteMovies();
    }

    private void setUpMoviesTable()
    {
        PlayingExt playMovie = new PlayingExt();
        moviesTable.setItems(filteredItems);
        //Setting the Play button for each row
        clmPlay.setCellFactory(column -> new TableCell<Movie, Void>() {
            private final Button playButton = new Button();

            {
                playButton.setId("playButton");
                playButton.setOnAction(event -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        playMovie.PlayingExt(movie);
                        manager.updateLastView(movie);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Remove the button if the row is empty
                } else {
                    setGraphic(playButton); // Add the button to the cell
                }
            }
        });
        clmTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        clmImdb.setCellValueFactory(new PropertyValueFactory<>("IMDB"));
        clmUserRating.setCellValueFactory(new PropertyValueFactory<>("userRating"));
        clmCategories.setCellValueFactory(cellData -> {
            Movie movie = cellData.getValue();
            return new SimpleStringProperty(String.join(", ", movie.getCategories()));
        });
        clmLastView.setCellValueFactory(new PropertyValueFactory<>("lastView"));
        //Setting the control buttons after each row
        clmControl.setCellFactory(column -> new TableCell<Movie, Void>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final Button userRatingButton = new Button();
            private final Button favoriteButton = new Button();
            private final Button imdbButton = new Button("IMDB");
            private final HBox buttonContainer = new HBox(5); // Container for buttons with spacing

            {
                // Add buttons to the container
                buttonContainer.getChildren().addAll(editButton, deleteButton, userRatingButton, favoriteButton, imdbButton);

                imdbButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black;");

                editButton.setId("editButton");
                deleteButton.setId("deleteButton");
                userRatingButton.setId("userRatingButton");
                favoriteButton.setId("favoriteButton");

                // Attach actions to buttons
                editButton.setOnAction(event -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        editMovie(movie);
                    }
                });
                deleteButton.setOnAction(event -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        deleteMoviePopUp(movie);
                    }
                });

                userRatingButton.setOnAction(event -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        //setUserRating(movie);
                    }
                });

                favoriteButton.setOnAction(event -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        toggleFavorite(movie);
                    }
                });

                imdbButton.setOnAction(event -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        openImdbPage(movie);
                    }
                });
            }

            public void toggleFavorite(Movie movie) {
                manager.toggleFavorite(movie);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Remove buttons for empty rows
                } else {
                    setGraphic(buttonContainer); // Add the button container to the cell
                }
            }
        });
    }


    private void editMovie(Movie movie) {
        showAddMovieWindow();
        populateCategories();
        btnSaveMovie.setOnAction(e -> editMovieSave());
        txtFilePath.setText(movie.getFileLink());
        txtMovieTitle.setText(movie.getTitle());
        txtImdb.setText(String.valueOf(movie.getIMDB()));
        txtUserScore.setText(String.valueOf(movie.getUserRating()));
        for (Category category : categoriesListView.getItems()) {
            for (String s : movie.getCategories()) {
                if (category.getName().equals(s)) {
                    categoriesListView.getSelectionModel().select(category);
                }
            }
        }
    }

    private void editMovieSave() {
        //To save to DB
    }

    private void openImdbPage(Movie movie) {
        if (movie != null) {
            try {
                //Opening the IMDB page
                String url = "https://www.imdb.com/find/?q=" +
                        movie.getTitle().replace(" ", "+");
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    // Fallback for unsupported systems
                    System.out.println("Cannot open browser. Unsupported platform.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setMovie(Movie movie)
    {
        items.add(movie);
    }

    private void setCategories() {
        categories.addAll(manager.getAllCategories());
        for (Category category : categories) {
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(false);
            checkBox.setText(category.getName());
            checkBox.setOnAction(_ -> addFilters());
            //scrlCategory.getChildren().add(checkBox);
            lstCategory.getItems().add(checkBox);

        }
    }

    private void setFavorite() {
        cbFavourite.setSelected(false);
        cbFavourite.setOnAction(_ -> addFavoriteFilter());
    }

    private void groupIMDBScore()
    {
        RadioButton[] radioButtons = {radio2, radio3, radio4, radio5, radio6, radio7, radio8, radio9};
        for (RadioButton radioButton : radioButtons)
        {
            radioButton.setToggleGroup(ratingGroup);
        }
    }

    private void populateCategories() {
        categoriesListView.getItems().clear();
        for (Category category : categories) {
            categoriesListView.getItems().add(category);
        }
        categoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML private void addFilters()
    {
        String searchText;
        int IMDBRating;
        List<String> selectedCategories = new ArrayList<>();
        boolean favoriteFilter = cbFavourite.isSelected();

        if(!searchField.getText().isEmpty())
        {
            searchText = searchField.getText().toLowerCase();
        }
        else searchText = "";

        if(ratingGroup.getSelectedToggle() != null)
        {
            ToggleButton button = (ToggleButton) ratingGroup.getSelectedToggle();
            IMDBRating = Integer.parseInt(button.getText());
        }
        else IMDBRating = -1;

        ObservableList<CheckBox> catItems = lstCategory.getItems();
        for(CheckBox checkBox : catItems)
        {
            if(checkBox.isSelected())
            {
                selectedCategories.add(checkBox.getText());
            }
        }

        filteredItems.setPredicate(movie -> {
            boolean matchesTitle = searchText.isEmpty() || movie.getTitle().toLowerCase().startsWith(searchText);
            boolean matchesRating = IMDBRating == -1 || movie.getIMDB() >= IMDBRating;
            List<String> movieCategories = Arrays.asList(movie.getCategories());
            boolean matchesCategories = selectedCategories.isEmpty() || selectedCategories.stream().allMatch(movieCategories::contains);
            boolean matchesFavorite = !favoriteFilter || movie.isFavorite();
            return matchesTitle && matchesRating && matchesCategories && matchesFavorite;
        });
    }

    private void addFavoriteFilter() {
        addFilters();
    }

    //Button clicks
    @FXML private void btnAddMovieClicked() {
        showAddMovieWindow();
        populateCategories();
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
        hideAddMovieWindow();
        refreshMovies();
    }

    @FXML private void saveNewCategory() {
        manager.addCategory(txtNewCategory.getText());
        CheckBox checkBox = new CheckBox();
        lstCategory.getItems().add(checkBox);
        hideAddPlaylistWindow();
    }

    private void refreshMovies()
    {
        items.clear();
        manager.refreshMovieList();
    }


    @FXML private void openHideCategory() {
        lstCategory.setVisible(!lstCategory.isVisible());
        lstCategory.setManaged(lstCategory.isVisible());
        if (lstCategory.isVisible()) {
            lblFilterCat.setText(CATEGORY_LABEL + UP_ARROW);
        } else {
            lblFilterCat.setText(CATEGORY_LABEL + DOWN_ARROW);
        }
    }
    @FXML private void openHideRating() {
        vBoxRating.setVisible(!vBoxRating.isVisible());
        vBoxRating.setManaged(vBoxRating.isVisible());
        if (vBoxRating.isVisible()) {
            lblFilterRating.setText(RATING_LABEL + UP_ARROW);
        } else {
            lblFilterRating.setText(RATING_LABEL + DOWN_ARROW);
        }
    }
@FXML private void clearFilters() {
        searchField.clear();
        filteredItems.setPredicate(null);
        if (ratingGroup.getSelectedToggle() != null) {
            ratingGroup.getSelectedToggle().setSelected(false);
        }
        for (CheckBox checkBox : lstCategory.getItems()) {
            checkBox.setSelected(false);
        }
}

    @FXML
    private void btnCloseClicked(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML private void btnCancelDeleteMovieClicked(ActionEvent event) {
        hideDeleteMoviePopUp();
    }

    @FXML private void btnYesDeleteMovieClicked(ActionEvent event) {
        Boolean fileDelete = cbDeleteFile.isSelected();
        if (movieToDelete != null) {
            manager.deleteMovie(movieToDelete, fileDelete);
            items.remove(movieToDelete);
            hideDeleteMoviePopUp();
        }
    }


    @FXML private void btnCancelDeleteCategoryClicked(ActionEvent event) {
        hideDeleteCategoryConfirmationWindow();
    }

    @FXML private void btnYesDeleteCategoryClicked() {
        Category category = lstCategoryDelete.getSelectionModel().getSelectedItem();
        System.out.println("Will delete " + category.getName());
        categories.remove(category);
        lstCategory.getItems().removeIf(checkBox -> checkBox.getText().equals(category.getName()));
        hideDeleteCategoryConfirmationWindow();
        hideDeleteCategoryWindow();
    }
    @FXML private void btnCancelDeleteMovieClicked() {
        hideDeleteMoviePopUp();
    }




    //Show and hide pop-up windows
    @FXML private void showAddMovieWindow() {
        popUpBg.setVisible(true);
        popupNewMovie.setVisible(true);
    }
    @FXML private void hideAddMovieWindow() {
        txtFilePath.setText("");
        txtMovieTitle.setText("");
        txtImdb.setText("");
        txtUserScore.setText("");
        categoriesListView.getItems().clear();
        popUpBg.setVisible(false);
        popupNewMovie.setVisible(false);
    }
    @FXML private void showAddPlaylistWindow() {
        popUpBg.setVisible(true);
        popupVBoxCat.setVisible(true);
    }
    @FXML private void hideAddPlaylistWindow() {
        txtNewCategory.setText("");
        popUpBg.setVisible(false);
        popupVBoxCat.setVisible(false);
    }

    public void showDeleteCategoryWindow(ActionEvent event) {
        popUpBg.setVisible(true);
        vbDeleteCategory.setVisible(true);
        lstCategoryDelete.getItems().clear();
        lstCategoryDelete.getItems().addAll(categories);
    }

    public void hideDeleteCategoryWindow() {
        lstCategoryDelete.getItems().clear();
        vbDeleteCategory.setVisible(false);
        popUpBg.setVisible(false);
    }

    public void deleteCategoryConfirmation(ActionEvent event) {
        showDeleteCategoryConfirmationWindow();
        String categoryName = lstCategoryDelete.getSelectionModel().getSelectedItem().toString();
        lblDeletingCategory.setText("Are you sure you want to delete " + categoryName + "?");
        //hideDeleteCategoryWindow();
    }

    private void showDeleteCategoryConfirmationWindow() {
            popupDeleteCategoryConfirmation.setVisible(true);
    }

    private void hideDeleteCategoryConfirmationWindow() {
        popupDeleteCategoryConfirmation.setVisible(false);
    }


    @FXML private void btnChooseClicked(ActionEvent event) {
            String filepath =  manager.openFile(btnChoose.getScene().getWindow());
            if (filepath != null) {
                txtFilePath.setText(filepath);
            }
    }

    private void checkObsoleteMovies() {
        // 1. Retrieve the list of problematic movies through BLL
        List<Movie> obsoleteMovies = manager.getMoviesToNotify();

        // 2. If the list is not empty – show an Alert (or another dialog)
        if (!obsoleteMovies.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Obsolete Movies");
            alert.setHeaderText("Some movies may need to be deleted!");

            StringBuilder sb = new StringBuilder("These movies have rating < 6 or haven't been viewed in 2+ years:\n\n");
            for (Movie m : obsoleteMovies) {
                sb.append("- ").append(m.getTitle()).append("\n");
            }
            alert.setContentText(sb.toString());
            //  connect the .css to change the color
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/dk/easv/movieexamproject/css/red.css").toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add("dialog-pane");
            alert.showAndWait();
        }
    }
    private void deleteMoviePopUp(Movie movie) {
        popUpBg.setVisible(true);
        popupDeleteMovie.setVisible(true);
        lblDeletingMovie.setText(DELETING_MOVIE_LABEL + movie.getTitle());
        movieToDelete = movie;
    }
    private void hideDeleteMoviePopUp() {
        lblDeletingMovie.setText(DELETING_MOVIE_LABEL);
        popupDeleteMovie.setVisible(false);
        popUpBg.setVisible(false);
    }
}
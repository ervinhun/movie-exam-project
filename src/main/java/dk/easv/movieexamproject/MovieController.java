package dk.easv.movieexamproject;

import dk.easv.movieexamproject.be.Category;
import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.bll.BLLManager;
import dk.easv.movieexamproject.dal.PlayingExt;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class MovieController implements Initializable {
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
    private ObservableList<Movie> items = FXCollections.observableArrayList();

    private ToggleGroup ratingGroup;

    private ArrayList<Category> categoryTestArrayList;
    private static final String CATEGORY_LABEL = "Category ";
    private static final String RATING_LABEL = "IMDB minimum rating ";
    private static final String UP_ARROW = "▲";
    private static final String DOWN_ARROW = "▼";
    private ImageView playImage;

    private BLLManager manager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //CreateImdbScore score = new CreateImdbScore(vBoxRating);
        groupIMDBScore();
        setTestCategories();
        setCategories();
        setUpMoviesTable();
        items.add(new Movie(1, "TEST", 3.0f, 4.0f, new String[] {"asd", "cat", "action"}, null, "asd"));
        manager = new BLLManager(this);
    }

    private void setUpMoviesTable()
    {
        PlayingExt playMovie = new PlayingExt();
        moviesTable.setItems(items);
        //Setting the Play button for each row
        clmPlay.setCellFactory(column -> new TableCell<Movie, Void>() {
        private final Button playButton = new Button();

        {
            playButton.setId("playButton");
            playButton.setOnAction(event -> {
                Movie movie = getTableView().getItems().get(getIndex());
                if (movie != null) {
                    playMovie.PlayingExt(movie);
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

                userRatingButton.setOnAction(event -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        //setUserRating(movie);
                    }
                });

                favoriteButton.setOnAction(event -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        //toggleFavorite(movie);
                    }
                });

                imdbButton.setOnAction(event -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        openImdbPage(movie);
                    }
                });
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
        for (Category category : categoryTestArrayList) {
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(false);
            checkBox.setText(category.getName());
            //scrlCategory.getChildren().add(checkBox);
            lstCategory.getItems().add(checkBox);

        }
    }

    private void setTestCategories() {
        categoryTestArrayList = new ArrayList<>();
        Category cat = new Category(1, "Action");
        categoryTestArrayList.add(cat);
        cat = new Category(2, "Comedy");
        categoryTestArrayList.add(cat);
        cat = new Category(3, "Fantasy");
        categoryTestArrayList.add(cat);
        cat = new Category(4, "Horror");
        categoryTestArrayList.add(cat);
        cat = new Category(5, "Science Fiction");
        categoryTestArrayList.add(cat);
        cat = new Category(6, "Thriller");
        categoryTestArrayList.add(cat);
        cat = new Category(7, "Documentary");
        categoryTestArrayList.add(cat);
        cat = new Category(8, "Animation");
        categoryTestArrayList.add(cat);
    }

    private void groupIMDBScore() {
        ratingGroup = new ToggleGroup();
        radio2.setToggleGroup(ratingGroup);
        radio3.setToggleGroup(ratingGroup);
        radio4.setToggleGroup(ratingGroup);
        radio5.setToggleGroup(ratingGroup);
        radio6.setToggleGroup(ratingGroup);
        radio7.setToggleGroup(ratingGroup);
        radio8.setToggleGroup(ratingGroup);
        radio9.setToggleGroup(ratingGroup);
    }

    private void populateCategories() {
        categoriesListView.getItems().clear();
        for (Category category : categoryTestArrayList) {
            categoriesListView.getItems().add(category);
        }
        categoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    //Button clicks
    @FXML private void btnAddMovieClicked(ActionEvent event) {
        btnSaveMovie.setOnAction(e -> saveMovie());
        showAddMovieWindow();
        populateCategories();
    }
    @FXML private void saveMovie() {
        System.out.println("Save Movie");
        System.out.println(txtFilePath.getText());
        System.out.println(txtMovieTitle.getText());
        System.out.println(txtImdb.getText());
        System.out.println(txtUserScore.getText());
        ObservableList<Category> selectedCategories = categoriesListView.getSelectionModel().getSelectedItems();
        System.out.println("Selected Categories: " + selectedCategories);
        hideAddMovieWindow();
    }



    @FXML private void saveNewCategory() {
        System.out.println("In the future saving: " + txtNewCategory.getText());
        int lastID = categoryTestArrayList.getLast().getId();
        Category newCat = new Category(lastID+1, txtNewCategory.getText());
        categoryTestArrayList.add(newCat);
        CheckBox checkBox = new CheckBox();
        checkBox.setText(newCat.getName());
        lstCategory.getItems().add(checkBox);
        hideAddPlaylistWindow();
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

    public void btnCancelDeleteMovieClicked(ActionEvent event) {
    }

    public void btnYesDeleteMovieClicked(ActionEvent event) {
    }


    public void btnCancelDeleteCategoryClicked(ActionEvent event) {
        hideDeleteCategoryConfirmationWindow();
    }



    public void btnYesDeleteCategoryClicked() {
        Category category = lstCategoryDelete.getSelectionModel().getSelectedItem();
        System.out.println("Will delete " + category.getName());
        categoryTestArrayList.remove(category);
        lstCategory.getItems().removeIf(checkBox -> checkBox.getText().equals(category.getName()));
        hideDeleteCategoryConfirmationWindow();
        hideDeleteCategoryWindow();
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
        lstCategoryDelete.getItems().addAll(categoryTestArrayList);
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
}
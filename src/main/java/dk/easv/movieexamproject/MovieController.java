package dk.easv.movieexamproject;

import dk.easv.movieexamproject.be.Category;
import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.bll.BLLManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
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
    @FXML private TableColumn<Movie, String> clmTitle;
    @FXML private TableColumn<Movie, String> clmImdb;
    @FXML private TableColumn<Movie, String> clmUserRating;
    @FXML private TableColumn<Movie, String> clmCategories;
    @FXML private TableColumn<Movie, String> clmLastView;
    private ObservableList<Movie> items = FXCollections.observableArrayList();
    private FilteredList<Movie> filteredItems = new FilteredList<>(items);
    private ObservableList<Category> categories = FXCollections.observableArrayList();

    private final ToggleGroup ratingGroup = new ToggleGroup();

    private static final String CATEGORY_LABEL = "Category ";
    private static final String RATING_LABEL = "IMDB minimum rating ";
    private static final String UP_ARROW = "▲";
    private static final String DOWN_ARROW = "▼";

    private BLLManager manager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //CreateImdbScore score = new CreateImdbScore(vBoxRating);
        manager = new BLLManager(this);
        groupIMDBScore();
        setCategories();
        setUpMoviesTable();
        items.add(new Movie(1, "TEST", 2.0f, 3.0f, new String[] {"asdd", "categ"}, null, "link", true));
    }

    private void setUpMoviesTable()
    {
        moviesTable.setItems(filteredItems);
        clmTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        clmImdb.setCellValueFactory(new PropertyValueFactory<>("IMDB"));
        clmUserRating.setCellValueFactory(new PropertyValueFactory<>("userRating"));
        clmCategories.setCellValueFactory(cellData -> {
            Movie movie = cellData.getValue();
            return new SimpleStringProperty(String.join(", ", movie.getCategories()));
        });
        clmLastView.setCellValueFactory(new PropertyValueFactory<>("lastView"));
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
            //scrlCategory.getChildren().add(checkBox);
            lstCategory.getItems().add(checkBox);

        }
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

        filteredItems.setPredicate(movie -> {
            boolean matchesTitle = searchText.isEmpty() || movie.getTitle().toLowerCase().startsWith(searchText);
            boolean matchesRating = IMDBRating == -1 || movie.getIMDB() == IMDBRating;
            return matchesTitle && matchesRating;
        });
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
        categories.remove(category);
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
}
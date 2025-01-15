package dk.easv.movieexamproject.pl;

import dk.easv.movieexamproject.be.Movie;
import dk.easv.movieexamproject.dal.PlayingExt;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.io.File;
import java.net.URI;

public class TableViewController extends FilterController
{
    protected void setUpMoviesTable() {
        PlayingExt playMovie = new PlayingExt();
        SortedList<Movie> sortedData = new SortedList<>(filteredItems);
        sortedData.comparatorProperty().bind(moviesTable.comparatorProperty());
        moviesTable.setItems(sortedData);

      // moviesTable.setItems(filteredItems);
        //Setting the Play button for each row
        clmPlay.setCellFactory(_ -> new TableCell<>() {
            private final Button playButton = new Button();

            {
                playButton.setId("playButton");
                playButton.setOnAction(_ -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        if (movie.getFileLink() != null && new File(movie.getFileLink()).exists()) {
                            playMovie.PlayingExt(movie);
                        }
                        manager.updateLastView(movie);
                        refreshMovies(movie.getId(), items.indexOf(movie), false);
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

        clmTitle.setSortable(true);
        clmImdb.setSortable(true);
        clmUserRating.setSortable(true);
        clmCategories.setSortable(true);
        clmLastView.setSortable(true);

        clmControl.setCellFactory(_ -> new TableCell<Movie, Void>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final Button favoriteButton = new Button();
            private final Button imdbButton = new Button("IMDB");
            private final HBox buttonContainer = new HBox(5); // Container for buttons with spacing

            {
                buttonContainer.getChildren().addAll(editButton, deleteButton, favoriteButton, imdbButton);

                imdbButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black;");

                editButton.setId("editButton");
                deleteButton.setId("deleteButton");
                favoriteButton.setId("favoriteButton");

                // Attach actions to buttons
                editButton.setOnAction(_ -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        editMovie(movie, items.indexOf(movie));
                    }
                });
                deleteButton.setOnAction(_ -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        delMoviePopUp(movie);
                    }
                });

                favoriteButton.setOnAction(_ -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        toggleFavorite(movie, items.indexOf(movie));
                        updateFavoriteIcon(movie, favoriteButton);
                    }
                });

                imdbButton.setOnAction(_ -> {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        openImdbPage(movie);
                    }
                });
            }

            private void toggleFavorite(Movie movie, int itemID) {
                manager.toggleFavorite(movie);
                movie.setFavorite(!movie.isFavorite());
                refreshMovies(movie.getId(), itemID, false);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Remove buttons for empty rows
                } else {
                    Movie movie = getTableView().getItems().get(getIndex());
                    if (movie != null) {
                        updateFavoriteIcon(movie, favoriteButton);
                    }
                    setGraphic(buttonContainer);
                }
            }
        });
    }

    protected void openImdbPage(Movie movie) {
        if (movie != null) {
            try {
                String url = "https://www.imdb.com/find/?q=" +
                        movie.getTitle().replace(" ", "+");
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    System.out.println("Cannot open browser. Unsupported platform.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

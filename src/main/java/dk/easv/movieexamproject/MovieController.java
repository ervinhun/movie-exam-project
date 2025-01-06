package dk.easv.movieexamproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MovieController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
package dk.easv.movieexamproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MovieController implements Initializable {
    @FXML
    private VBox vBoxRating;
    @FXML private RadioButton radio2;
    @FXML private RadioButton radio3;
    @FXML private RadioButton radio4;
    @FXML private RadioButton radio5;
    @FXML private RadioButton radio6;
    @FXML private RadioButton radio7;
    @FXML private RadioButton radio8;
    @FXML private RadioButton radio9;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //CreateImdbScore score = new CreateImdbScore(vBoxRating);
        createIMDBScore();
    }

    private void createIMDBScore() {
        ToggleGroup group = new ToggleGroup();
        radio2.setToggleGroup(group);
        radio3.setToggleGroup(group);
        radio4.setToggleGroup(group);
        radio5.setToggleGroup(group);
        radio6.setToggleGroup(group);
        radio7.setToggleGroup(group);
        radio8.setToggleGroup(group);
        radio9.setToggleGroup(group);
    }
}
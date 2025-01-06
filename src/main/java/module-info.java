module dk.easv.movieexamproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens dk.easv.movieexamproject to javafx.fxml;
    exports dk.easv.movieexamproject;
}
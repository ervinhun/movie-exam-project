package dk.easv.movieexamproject.dal;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class ChooseFile {
    File chosenFile;

    public ChooseFile(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Movie File");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Movie mp4", "*.mp4");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("Movie mpeg4", "*.mpeg4");

        fileChooser.getExtensionFilters().addAll(extFilter, extFilter2);
        chosenFile = fileChooser.showOpenDialog(window);

    }

    public String getSelectedFilePath() {
        if (chosenFile != null) {
            return chosenFile.getAbsolutePath();
        } else
            throw new RuntimeException("File is null, can not return filePath");
    }
}


package dk.easv.movieexamproject.bll;

import dk.easv.movieexamproject.dal.ChooseFile;
import javafx.stage.Window;

public class BLLManager {

    private ChooseFile fileBrowser;

    public String openFile(Window window) {
        fileBrowser = new ChooseFile(window);

        if (fileBrowser.getSelectedFilePath() != null) {
            return fileBrowser.getSelectedFilePath();
        }
        return null;
    }
}

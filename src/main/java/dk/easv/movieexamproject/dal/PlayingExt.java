package dk.easv.movieexamproject.dal;

import dk.easv.movieexamproject.be.Movie;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayingExt {
    public void PlayingExt () {
        String[] categories = new String[2];
        categories[0] = "Action";
        categories[1] = "Docu";
        Movie testPlay = new Movie(1, "Test", (float)8.9, (float)9.2,
                categories, null, "c:\\1\\20231213_141219.mp4" );

        openDesktopPlayer(testPlay);
    }

    public void PlayingExt (Movie movie) {
        openDesktopPlayer(movie);
    }


    private void openDesktopPlayer(Movie movie) {
        try {
            File mediaFile = new File(movie.getFileLink());
            // Open the media file using the default system application
            Desktop.getDesktop().open(mediaFile);
        } catch (IOException e) {
            System.err.println("Error opening media file: " + e.getMessage());
        }
    }
}

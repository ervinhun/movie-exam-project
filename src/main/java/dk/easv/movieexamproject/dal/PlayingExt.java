package dk.easv.movieexamproject.dal;

import dk.easv.movieexamproject.be.Movie;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PlayingExt {

    public void PlayingExt (Movie movie) {
        //TODO: Update DB lastView
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

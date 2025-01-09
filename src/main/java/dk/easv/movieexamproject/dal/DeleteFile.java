package dk.easv.movieexamproject.dal;

import java.io.File;

public class DeleteFile {
    public boolean deleteFile(String filePath) {
        File myFile = new File(filePath);
        return myFile.delete();
    }
}

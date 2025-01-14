package dk.easv.movieexamproject.pl;

import dk.easv.movieexamproject.be.Movie;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterController extends MovieController
{
    @Override
    @FXML
    protected void addFilters()
    {
        String searchText;
        int IMDBRating;
        List<String> selectedCategories = new ArrayList<>();
        boolean favFilter = cbFavourite.isSelected();

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

        List<CheckBox> catItems = lstCategory.getItems();
        for(CheckBox checkBox : catItems)
        {
            if(checkBox.isSelected())
            {
                selectedCategories.add(checkBox.getText());
            }
        }

        filteredItems.setPredicate(movie -> {
            boolean matchesTitle = searchText.isEmpty() || movie.getTitle().toLowerCase().startsWith(searchText);
            boolean matchesRating = IMDBRating == -1 || movie.getIMDB() >= IMDBRating;
            List<String> movieCategories = Arrays.asList(movie.getCategories());
            boolean matchesCategories = selectedCategories.isEmpty() || selectedCategories.stream().allMatch(movieCategories::contains);
            boolean matchesFavorite = !favFilter || movie.isFavorite();
            return matchesTitle && matchesRating && matchesCategories && matchesFavorite;
        });
    }

    @Override
    @FXML
    protected void clearFilters()
    {
        searchField.clear();
        filteredItems.setPredicate(null);
        cbFavourite.setSelected(false);
        if (ratingGroup.getSelectedToggle() != null) {
            ratingGroup.getSelectedToggle().setSelected(false);
        }
        for (CheckBox checkBox : lstCategory.getItems()) {
            checkBox.setSelected(false);
        }
    }
}

package pl.put.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.put.model.Keyword;

/**
 * Author: Krystian Świdurski
 */
public class KeywordDialog extends Stage {
    VBox mainPane = new VBox(20);

    public KeywordDialog(Keyword keyword) {
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        setTitle(keyword.getValue());
        setWidth(300);
        setHeight(150);
        setScene(new Scene(mainPane));

        mainPane.getChildren().add(new Label(String.format("Original: %s", keyword.getValue())));
        mainPane.getChildren().add(new Label(String.format("Stemmed: %s", keyword.getStemmedValue())));
        mainPane.setPadding(new Insets(10));
    }

    public static void show(Keyword keyword) {
        KeywordDialog dialog = new KeywordDialog(keyword);
        dialog.showAndWait();
    }
}

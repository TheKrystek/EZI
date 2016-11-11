package pl.put.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.put.model.Document;

/**
 * Author: Krystian Świdurski
 */
public class DocumentDialog extends Stage {
    private TabPane tabPane = new TabPane();
    private Tab originalTab = new Tab("Original");
    private Tab stemmedTab = new Tab("Stemmed");

    public DocumentDialog(Document document) {
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        setTitle(document.getTitle());
        setWidth(500);
        setHeight(500);
        setScene(new Scene(tabPane));

        tabPane.getTabs().add(originalTab);
        tabPane.getTabs().add(stemmedTab);

        addToTab(originalTab, document.getTitle(), document.getText());
        addToTab(stemmedTab, document.getStemmedTitle(), document.getStemmedText());
    }

    private void addToTab(Tab tab, String title, String text) {
        tab.setClosable(false);
        tab.setContent(getPaneWithTitle(title, text));
    }


    private Node getPaneWithTitle(String title, String text) {
        Label titleLabel = getLabel(title, true);
        Label textLabel = getLabel(text, false);
        AnchorPane pane = new AnchorPane();
        pane.getChildren().setAll(titleLabel, textLabel);
        pane.setPadding(new Insets(10));
        anchorTitle(titleLabel);
        anchorContent(textLabel);
        return pane;
    }


    private Label getLabel(String text, boolean bold) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setAlignment(Pos.TOP_LEFT);
        if (bold) {
            label.setStyle("-fx-font-weight: bold;");
        }
        return label;
    }

    private void anchorContent(Node node) {
        AnchorPane.setTopAnchor(node, 30.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }

    private void anchorTitle(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }


    public static void show(Document document) {
        DocumentDialog dialog = new DocumentDialog(document);
        dialog.showAndWait();
    }
}

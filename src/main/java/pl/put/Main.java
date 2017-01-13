package pl.put;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public final static String DEFAULT_DOCUMENTS_PATH = "documents.txt";
    public final static String DEFAULT_KEYWORDS_PATH = "keywords.txt";

    public final static String DEFAULT_K_MEANS_DOCUMENTS_PATH = "documents-2.txt";
    public final static String DEFAULT_K_MEANS_KEYWORDS_PATH = "keywords-2.txt";

    public final static String DEFAULT_STOPWORDS_PATH = "stopwords.txt";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gui/main.fxml"));
        primaryStage.setTitle("Simple Search Engine - Sobkowiak, Swidurski");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

package pl.put.gui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.put.Main;
import pl.put.model.*;
import pl.put.services.*;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

public class MainController {

    private File documentFile = new File(Main.DEFAULT_DOCUMENTS_PATH);
    private File keywordFile = new File(Main.DEFAULT_K_MEANS_KEYWORDS_PATH);
    private File classifiedDocumentFile = new File(Main.DEFAULT_K_MEANS_DOCUMENTS_PATH);

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Tab loadDataTab;
    @FXML
    private ListView<Document> documentsListView;
    @FXML
    private ListView<Keyword> keywordsListView;
    @FXML
    private TextField documentsTextField;
    @FXML
    private TextField classifiedDocumentsTextField;
    @FXML
    private TextField kTextField;
    @FXML
    private TextField numberOfIterationsTextField;
    @FXML
    private TextField keywordsTextField;
    @FXML
    private Button TFIDFButton;
    @FXML
    private Button KMeansButton;
    @FXML
    private Tab searchTab;
    @FXML
    private Tab groupingTab;
    @FXML
    private TextField queryTextField;
    @FXML
    private RadioButton cosinusRadioButton;
    @FXML
    private ToggleGroup similarityToggleGroup;
    @FXML
    private RadioButton jaccardRadioButton;
    @FXML
    private CheckBox allResultsCheckBox;
    @FXML
    private TableView<SearchResult> resultsTableView;
    @FXML
    private ListView<Document> groupedDocumentsListView;
    @FXML
    private TableColumn<SearchResult, String> titleColumn;
    @FXML
    private TableColumn<SearchResult, String> similarityColumn;
    @FXML
    private Label numberOfResults;
    @FXML
    private HBox queryExpansionBox;

    private Documents documents;
    private HashSet<String> classNames;
    private Keywords keywords;
    private Stemmer stemmer = new Stemmer();
    private TFIDF tfidf;
    private SearchEngine searchEngine;
    private SearchResults searchResults;
    private QueryExpansion queryExpansion = new QueryExpansion();

    @FXML
    void loadDocuments() {
        try {
            DocumentReader reader = new FileDocumentReader(documentFile);
            documents = reader.read();
            stemmer.run(documents);
            documentsListView.getItems().setAll(documents);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't read file: " + documentFile);
        }
    }

    @FXML
    void loadClassifiedDocuments() {
        try {
            DocumentReader reader = new FileClassifiedDocumentReader(classifiedDocumentFile);
            documents = reader.read();
            stemmer.run(documents);
            documentsListView.getItems().setAll(documents);
            classNames = ((FileClassifiedDocumentReader)reader).getClassNames();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't read file: " + classifiedDocumentFile);
        }
    }

    @FXML
    void loadKeywords() {
        try {
            KeywordsReader reader = new FileKeywordsReader(keywordFile);
            keywords = reader.read();
            stemmer.run(keywords);
            keywordsListView.getItems().setAll(keywords);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't read file: " + keywordFile);
        }
    }


    @FXML
    void runTFIDF() {
        tfidf = new TFIDF(documents, keywords);
        searchTab.setDisable(false);
        groupingTab.setDisable(false);
        searchTab.getTabPane().getSelectionModel().select(searchTab);
        createSearchEngine();
    }

    @FXML
    void runKMeans() {
        Integer k;
        Integer iterations;
        try {
            try {
                k = Integer.parseInt(kTextField.getText());
                iterations = Integer.parseInt(numberOfIterationsTextField.getText());
            } catch (NumberFormatException e) {
                throw new Exception(e.getMessage());
            }
            if(iterations == null){
                throw new Exception("Invalid number of iterations.");
            }
            if(k == null){
                throw new Exception("Invalid value of k.");
            }
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }

        KMeans kMeans = new KMeans(documents, k, tfidf, iterations);
        groupedDocumentsListView.getItems().setAll(kMeans.getDocuments());
    }

    @FXML
    void search() {
        searchResults = searchEngine.search(new Query(queryTextField.getText()));
        showResults(searchResults);
        showQueryExpansion(queryExpansion.expandQuery(new Query(queryTextField.getText())));
    }

    private void showResults(SearchResults result) {
        resultsTableView.getItems().setAll(result.getResults(allResultsCheckBox.isSelected()));
        numberOfResults.setText(String.format("Number of results: %s", resultsTableView.getItems().size()));
    }

    private void showQueryExpansion(List<Query> queryExpansion) {
        queryExpansionBox.getChildren().clear();
        queryExpansionBox.getChildren().add(new Label("Query expansion: "));
        for(Query query : queryExpansion){
            Hyperlink link = new Hyperlink(query.getText());
            link.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    queryTextField.setText(link.getText());
                    search();
                }
            });
            queryExpansionBox.getChildren().add(link);
        }
    }





    private void createSearchEngine() {
        searchEngine = new SearchEngine(stemmer, tfidf);
        searchEngine.setSimilarity(new CosineSimilarity());
    }

    @FXML
    void initialize() {
        setupTabs();
        setupTextFields();
        setupListViews();
        setupToggleGroup();
        setupTableView();
        setupShowAllCheckBox();
    }

    private void setupShowAllCheckBox() {
        allResultsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (searchResults == null) {
                return;
            }
            showResults(searchResults);
        });
    }

    private void setupTableView() {
        titleColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDocument().getTitle()));
        similarityColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(String.format("%.4f", param.getValue().getSimilarity().doubleValue())));
        resultsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                SearchResult result = resultsTableView.getSelectionModel().getSelectedItem();
                if (result!=null) {
                    DocumentDialog.show(result.getDocument());
                }
            }
        });
    }

    private void setupToggleGroup() {
        similarityToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (searchEngine == null) {
                return;
            }

            if (newValue == cosinusRadioButton) {
                searchEngine.setSimilarity(new CosineSimilarity());
            }
            if (newValue == jaccardRadioButton) {
                searchEngine.setSimilarity(new JaccardSimilarity());
            }
            search();
        });
    }

    private void setupTabs() {
        searchTab.setDisable(true);
        groupingTab.setDisable(true);
    }

    private void setupTextFields() {
        documentsTextField.setText(documentFile.getAbsolutePath());
        classifiedDocumentsTextField.setText(classifiedDocumentFile.getAbsolutePath());
        keywordsTextField.setText(keywordFile.getAbsolutePath());

        documentsTextField.setOnMouseClicked(getMouseEventHandler(documentsTextField, documentFile));
        classifiedDocumentsTextField.setOnMouseClicked(getMouseEventHandler(classifiedDocumentsTextField, classifiedDocumentFile));
        keywordsTextField.setOnMouseClicked(getMouseEventHandler(keywordsTextField, keywordFile));

        documentsTextField.textProperty().addListener((observable, oldValue, newValue) -> documentFile = assignIfProperFile(newValue, documentFile));
        classifiedDocumentsTextField.textProperty().addListener((observable, oldValue, newValue) -> classifiedDocumentFile = assignIfProperFile(newValue, classifiedDocumentFile));
        keywordsTextField.textProperty().addListener((observable, oldValue, newValue) -> keywordFile = assignIfProperFile(newValue, keywordFile));

        queryTextField.setOnAction(event -> search());
    }

    private void setupListViews() {
        TFIDFButton.setDisable(true);
        KMeansButton.setDisable(true);
        documentsListView.getItems().addListener(getListener());
        documentsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Document document = documentsListView.getSelectionModel().getSelectedItem();
                if (document != null) {
                    DocumentDialog.show(document);
                }
            }
        });
        keywordsListView.getItems().addListener(getListener());
        keywordsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Keyword keyword = keywordsListView.getSelectionModel().getSelectedItem();
                if (keyword != null) {
                    KeywordDialog.show(keyword);
                }
            }
        });

        groupedDocumentsListView.getItems().addListener(getListener());
        groupedDocumentsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Document document = groupedDocumentsListView.getSelectionModel().getSelectedItem();
                if (document != null) {
                    DocumentDialog.show(document);
                }
            }
        });

    }

    private ListChangeListener<Object> getListener() {
        return c -> {
            TFIDFButton.setDisable(documentsListView.getItems().isEmpty() || keywordsListView.getItems().isEmpty());
            KMeansButton.setDisable(documentsListView.getItems().isEmpty() || keywordsListView.getItems().isEmpty());
        };
    }

    private File assignIfProperFile(String path, File destination) {
        File f = new File(path);
        if (f != null && f.exists() && !f.isDirectory()) {
            return f;
        }
        return destination;
    }

    private EventHandler<MouseEvent> getMouseEventHandler(TextField textField, File file) {
        return event -> {
            if (event.getClickCount() == 2) {
                Stage stage = (Stage) textField.getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a file");
                fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
                if (file.exists()) {
                    fileChooser.setInitialDirectory(file.getParentFile());
                }
                File selectedFile = fileChooser.showOpenDialog(stage);
                if (selectedFile != null && selectedFile.exists()) {
                    textField.setText(selectedFile.getAbsolutePath());
                }
            }
        };
    }
}

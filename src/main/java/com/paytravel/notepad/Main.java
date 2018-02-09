package com.paytravel.notepad;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.paytravel.notepad.Note.DATETIME_PATTERN;
import static com.paytravel.notepad.Note.getLocalDateTimeString;
import static javafx.collections.FXCollections.observableArrayList;

public class Main extends Application {
    public final static int TEXT_AREA_LIMIT = 100;

    private ObservableList<Note> notes = observableArrayList();
    private TableView<Note> table = new TableView<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private NoteDao dao = new NoteDao();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(event -> {
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        try (Connection connection = ConnectionManager.getConnection()) {
            RunScript.runScript(connection, "TEST_DELETE_TABLE"); //Runscript uses only for test tasks
            RunScript.runScript(connection, "TEST_CREATE_TABLE");
        }

        Scene scene = new Scene(new Group());
        primaryStage.setWidth(500);
        primaryStage.setHeight(500);

        TableColumn<Note, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(DATETIME_PATTERN.length() * 8);
        dateCol.setMinWidth(100);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateString"));
        TableColumn<Note, String> noteCol = new TableColumn<>("Note");
        noteCol.setPrefWidth(300);
        noteCol.setMinWidth(200);

        noteCol.setCellValueFactory(new PropertyValueFactory<>("note")); //not succeed with CellDataFeatures to wrap the text

        table.getColumns().addAll(dateCol, noteCol);
        table.setItems(notes);
        table.setEditable(false);

        Button addButton = new Button("Add new");

        addButton.setOnAction(addNoteEvent -> {
            GridPane secondaryLayout = new GridPane();
            Scene secondScene = new Scene(secondaryLayout, 400, 200);

            TextArea textArea = new TextArea();
            textArea.setWrapText(true);
            textArea.setTextFormatter(new TextFormatter<>(
                    change -> change.getControlNewText().length() <= TEXT_AREA_LIMIT ? change : null));

            Label dateLabel = new Label();
            final LocalDateTime[] now = new LocalDateTime[1];
            final Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.millis(1000),
                            event -> {
                                now[0] = LocalDateTime.now();
                                dateLabel.setText(getLocalDateTimeString(now[0]));
                            }
                    )
            );
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            Button saveButton = new Button("Save");
            saveButton.setOnAction(event -> executorService.execute(() -> {
                dao.insertNote(Timestamp.valueOf(now[0]), textArea.getText());
                notes.clear();
                notes.addAll(dao.getNotes());
            }));
            GridPane.setHalignment(dateLabel, HPos.LEFT);
            secondaryLayout.add(dateLabel, 0, 0, 2, 1);
            secondaryLayout.add(textArea, 0, 1);
            secondaryLayout.add(saveButton, 0, 2);

            Stage newWindow = new Stage();
            newWindow.setTitle("Second Stage");
            newWindow.setScene(secondScene);
            newWindow.show();
        });

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(table, addButton);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        primaryStage.setTitle("Notepad");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

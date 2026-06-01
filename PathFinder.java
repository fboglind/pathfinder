// PROG2 VT2023, Inlämmningsuppgift, del 2
// Grupp 028
// Gabriel Bendezu gabe3137
// Fredrik Boglind frbo5627
// Maria Fernanda Esquivel Hidalgo maes3583

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PathFinder extends Application {

    private ImageView imageView;
    private BorderPane root;
    private Pane outputArea;
    private Graph<Place> places = new ListGraph<>();
    private ArrayDeque<Place> selectedPlaces = new ArrayDeque<>();
    private boolean changesMade = false;
    private String imageFileName;

    @Override
    public void start(Stage primaryStage) throws IOException {

        root = new BorderPane();
        GridPane grid = new GridPane();
        primaryStage.setTitle("PathFinder");
        primaryStage.setOnCloseRequest(new ExitHandler());

        // MENY
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        menuBar.getMenus().add(menuFile);
        MenuItem menuNewMap = new MenuItem("New Map");
        menuNewMap.setOnAction(new NewMapHandler(primaryStage));
        MenuItem menuOpenFile = new MenuItem("Open");
        menuOpenFile.setOnAction(new OpenHandler(primaryStage));
        MenuItem menuSaveFile = new MenuItem("Save");
        menuSaveFile.setOnAction(new SaveHandler());
        MenuItem menuSaveImage = new MenuItem("Save Image");
        menuSaveImage.setOnAction(new SaveImageHandler());
        MenuItem menuExit = new MenuItem("Exit");
        menuExit.setOnAction(event -> {
            WindowEvent closeEvent = new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST);
            primaryStage.fireEvent(closeEvent);
            event.consume();
        });

        menuFile.getItems().addAll(menuNewMap, menuOpenFile, menuSaveFile, menuSaveImage, menuExit);

        // KNAPPAR
        Button btnFindPath = new Button("Find Path");
        btnFindPath.setOnAction(new FindPathHandler());
        Button btnShowConnection = new Button("Show Connection");
        btnShowConnection.setOnAction(new ShowConnectionHandler());
        Button btnNewPlace = new Button("New Place");
        btnNewPlace.setOnAction(new NewPlaceHandler());
        Button btnNewConnection = new Button("New Connection");
        btnNewConnection.setOnAction(new NewConnectionHandler());
        Button btnChangeConnection = new Button("Change Connection");
        btnChangeConnection.setOnAction(new ChangeConnectionHandler());

        grid.setAlignment(Pos.TOP_CENTER);
        grid.setVgap(5);
        grid.setHgap(5);
        grid.add(btnFindPath, 0, 2);
        grid.add(btnShowConnection, 1, 2);
        grid.add(btnNewPlace, 2, 2);
        grid.add(btnNewConnection, 3, 2);
        grid.add(btnChangeConnection, 4, 2);

        // grid och image kommer ligga i outputArea //
        outputArea = new Pane();
        root.setTop(menuBar);
        root.setCenter(grid);
        root.setBottom(outputArea);

        // Set scene
        Scene scene = new Scene(root, 500, 75);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);

        // ID
        // Samtliga JavaFX-komponenter skall ha ett ID satt med metoden
        menuBar.setId("menu");
        menuFile.setId("menuFile");
        menuNewMap.setId("menuNewMap");
        menuOpenFile.setId("menuOpenFile");
        menuSaveFile.setId("menuSaveFile");
        menuSaveImage.setId("menuSaveImage");
        menuExit.setId("menuExit");
        btnFindPath.setId("btnFindPath");
        btnNewPlace.setId("btnNewPlace");
        btnNewConnection.setId("btnNewConnection");
        btnChangeConnection.setId("btnChangeConnection");
        btnShowConnection.setId("btnShowConnection");
        outputArea.setId("outputArea");
        // place.setId //finns i addCircles-metoden
    }
    // HANDLERS FÖR MENYER

    class NewMapHandler implements EventHandler<ActionEvent> {
        private Stage primaryStage;

        NewMapHandler(Stage primaryStage) {
            this.primaryStage = primaryStage;
        }

        @Override
        public void handle(ActionEvent event) {
            imageFileName = "file:europa.gif";
            if (!changesMade || confirmUnsavedChanges()) {
                showNewMap(primaryStage);
            }
        }
    }

    class OpenHandler implements EventHandler<ActionEvent> {
        private ArrayList<Place> cities = new ArrayList<Place>();
        private Stage primaryStage;

        OpenHandler(Stage primaryStage) {
            this.primaryStage = primaryStage;
        }

        @Override
        public void handle(ActionEvent event) {
            if (!changesMade || confirmUnsavedChanges()) {
                openFile();
            }
        }

        public void openFile() {
            try {
                cities.clear();
                FileReader reader = new FileReader("europa.graph");
                BufferedReader in = new BufferedReader(reader);
                imageFileName = in.readLine();
                showNewMap(primaryStage);
                String[] placeTokens = in.readLine().split(";");
                parsePlaces(placeTokens);
                String line;
                while ((line = in.readLine()) != null) {
                    String[] connectionTokens = line.split(";");
                    if (connectionTokens.length == 4) { // Bör ändras
                        parseConnections(connectionTokens);
                    }
                }
                in.close();
                reader.close();
                changesMade = false;

            } catch (IOException e) {
                displayErrorMessage("IO Error");
            }

        }

        public void parsePlaces(String[] tokens) {
            Place place;
            for (int i = 0; i < tokens.length; i += 3) {
                String placeName = tokens[i];
                double x = Double.parseDouble(tokens[i + 1]);
                double y = Double.parseDouble(tokens[i + 2]);
                place = new Place(placeName, x, y);
                cities.add(place);
                places.add(place);
                addCirclesToPane(place);
            }
        }

        public void parseConnections(String[] connections) {
            Place fromCity = findCity(connections[0]);
            Place toCity = findCity(connections[1]);

            if (fromCity != null && toCity != null && places.getEdgeBetween(fromCity, toCity) == null) {
                places.connect(fromCity, toCity, connections[2], Integer.parseInt(connections[3]));
                addLinesToPane(fromCity, toCity);
            }
        }

        private Place findCity(String name) {
            for (Place city : cities) {
                if (city.getName().equalsIgnoreCase(name)) {
                    return city;
                }
            }
            return null;
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            try {
                FileWriter outFile = new FileWriter("europa.graph");
                PrintWriter out = new PrintWriter(outFile);
                out.println(imageFileName);

                for (Place place : places.getNodes()) {
                    out.print(place.getName() + ";" + place.getCenterX() + ";" + place.getCenterY() + ";");
                }
                out.println();

                for (Place place : places.getNodes()) {
                    for (Edge<Place> edge : places.getEdgesFrom(place)) {
                        out.println(place.getName() + ";" + edge.getDestination().getName() + ";" + edge.getName() + ";"
                                + edge.getWeight());
                    }
                }
                out.close();
                outFile.close();
                changesMade = false;

            } catch (IOException e) {
                displayErrorMessage("IO Error");
            }
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                WritableImage image = outputArea.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", new File("capture.png"));
            } catch (IOException e) {
                displayErrorMessage("IO Error");
            }
        }
    }

    class ExitHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent event) {
            if (changesMade && !confirmUnsavedChanges()) {
                event.consume();
            }
        }
    }

    // HANDLERS FÖR KNAPPAR

    class NewPlaceHandler implements EventHandler<ActionEvent> {

        private Button btnNewPlace;

        @Override
        public void handle(ActionEvent event) {
            // Muspekaren ändras till ett + i väntan på att användaren klickar på den
            // positionen på kartan där hen vill ha sin plats
            // Samtidigt skall även knappen göras inaktiv(disabled
            root.setCursor(Cursor.CROSSHAIR);
            btnNewPlace = (Button) event.getSource();
            btnNewPlace.setDisable(true);
            // När användaren klickar på kartan för att placera en ny plats visas
            // dialogrutan i figur 8
            imageView.setOnMouseClicked(new MapClickHandler(btnNewPlace));
        }
    }

    class MapClickHandler implements EventHandler<MouseEvent> {

        private String name;
        private Place newPlace;
        private Button button;

        MapClickHandler(Button button) {
            this.button = button;
        }

        @Override
        public void handle(MouseEvent event) {
            TextInputDialog dialogue = new TextInputDialog();
            dialogue.setTitle("New Place");
            dialogue.setContentText("Name of place:");
            dialogue.showAndWait();

            name = dialogue.getResult();

            // När användaren matar in platsens namn i textfältet och klickar "OK" i
            // dialogrutan:
            if (name != null && !name.isEmpty()) {
                newPlace = new Place(name, event.getX(), event.getY());
                places.add(newPlace); // lägger till platsen i grafen
                addCirclesToPane(newPlace);
            }

            // Muspekaren återgår till att vara en vanlig pil
            root.setCursor(Cursor.DEFAULT);
            // Knappen New Place skall aktiveras igen(enabled);
            button.setDisable(false);
            // turn off listening to mouseclicks on the map
            imageView.setOnMouseClicked(null);
        }

    }

    class NewConnectionHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {

            if (!isMaxSelections()) {
                displayErrorMessage("Two places must be selected");
                return;
            }

            if (connectionExists()) {
                displayErrorMessage("Connection already exists");
                return;
            }

            Place place1 = selectedPlaces.peekFirst();
            Place place2 = selectedPlaces.peekLast();

            Dialog<String> dialogue = new Dialog<>();
            dialogue.setTitle("Connection");
            dialogue.setHeaderText("Connection from " + place1.getName() + " to " + place2.getName());

            Label nameLabel = new Label("Name: ");
            Label timeLabel = new Label("Time: ");
            TextField nameField = new TextField();
            TextField timeField = new TextField();

            GridPane grid = new GridPane();
            grid.add(nameLabel, 1, 1);
            grid.add(timeLabel, 1, 2);
            grid.add(nameField, 2, 1);
            grid.add(timeField, 2, 2);
            dialogue.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            dialogue.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialogue.setResultConverter(dialogButton -> {
                if (dialogButton != buttonTypeOk) {
                    return null;
                }

                String name = nameField.getText();
                if (name.isEmpty()) {
                    displayErrorMessage("Name can't be empty");
                    return null;
                }

                int time = parseTime(timeField.getText());
                if (time == -1)
                    return null;

                places.connect(place1, place2, name, time);
                addLinesToPane(place2, place1);
                changesMade = true;
                return "";
            });

            dialogue.showAndWait();
        }
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            if (!isMaxSelections()) {
                displayErrorMessage("Two places must be selected");
                return;
            }

            if (!connectionExists()) {
                displayErrorMessage("No connection");
                return;
            }

            Place place1 = selectedPlaces.peekFirst();
            Place place2 = selectedPlaces.peekLast();

            Dialog<String> dialogue = new Dialog<>();

            dialogue.setTitle("Connection");
            dialogue.setHeaderText("Connection from " + place1.getName() + " to " + place2.getName());

            Label nameLabel = new Label("Name: ");
            Label timeLabel = new Label("Time: ");
            TextField nameField = new TextField();
            TextField timeField = new TextField();

            Edge<Place> connection = places.getEdgeBetween(place1, place2);

            nameField.setText(connection.getName());
            timeField.setText(Integer.toString(connection.getWeight()));

            nameField.setEditable(false);
            timeField.setEditable(false);

            GridPane grid = new GridPane();
            grid.add(nameLabel, 1, 1);
            grid.add(timeLabel, 1, 2);
            grid.add(nameField, 2, 1);
            grid.add(timeField, 2, 2);
            dialogue.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.CANCEL_CLOSE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            dialogue.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
            dialogue.showAndWait();
        }
    }

    class ChangeConnectionHandler implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {

            if (!isMaxSelections()) {
                displayErrorMessage("Two places must be selected");
                return;
            }

            if (!connectionExists()) {
                displayErrorMessage("No connection");
                return;
            }

            Place place1 = selectedPlaces.peekFirst();
            Place place2 = selectedPlaces.peekLast();

            Dialog<String> dialogue = new Dialog<>();
            dialogue.setTitle("Connection");
            dialogue.setHeaderText("Connection from " + place1.getName() + " to " + place2.getName());

            Label nameLabel = new Label("Name: ");
            Label timeLabel = new Label("Time: ");
            TextField nameField = new TextField();
            TextField timeField = new TextField();
            nameField.setEditable(false);
            timeField.setEditable(true);

            Edge<Place> connection = places.getEdgeBetween(place1, place2);

            nameField.setText(connection.getName());
            timeField.setText(Integer.toString(connection.getWeight()));

            GridPane grid = new GridPane();
            grid.add(nameLabel, 1, 1);
            grid.add(timeLabel, 1, 2);
            grid.add(nameField, 2, 1);
            grid.add(timeField, 2, 2);
            dialogue.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            dialogue.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialogue.setResultConverter(dialogButton -> {
                if (dialogButton != buttonTypeOk) {
                    return null;
                }

                int time = parseTime(timeField.getText());
                if (time == -1)
                    return null;

                places.setConnectionWeight(place1, place2, time);
                changesMade = true;

                return "";
            });

            dialogue.showAndWait();
        }

    }

    class PlaceClickHandler implements EventHandler<MouseEvent> {

        private final Place selectedPlace;

        PlaceClickHandler(Place place) {
            selectedPlace = place;
        }

        @Override
        public void handle(MouseEvent event) {
            if (selectedPlaces.contains(selectedPlace)) {
                selectedPlace.deselect();
                selectedPlaces.remove(selectedPlace);
                return;
            }
            // If two places are already selected and an unselected place is clicked, the
            // first selected place gets removed and the
            // unselected place gets added
            if (isMaxSelections()) {
                Place firstPlace = selectedPlaces.removeFirst();
                firstPlace.deselect();
            }
            selectedPlaces.addLast(selectedPlace);
            selectedPlace.select();

        }

    }

    class FindPathHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (!isMaxSelections()) {
                displayErrorMessage("Two places must be selected");
                return;
            }

            Place from = selectedPlaces.peekFirst();
            Place to = selectedPlaces.peekLast();

            List<Edge<Place>> edges = places.getPath(from, to);

            if (edges == null) {
                displayErrorMessage("No path");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("message");
            alert.setHeaderText("The Path from " + from.getName() + " to " + to.getName() + ":");

            StringBuilder sb = new StringBuilder();

            int total = 0;
            for (Edge<Place> e : edges) {
                sb.append(e.toString()).append("\n");
                total += e.getWeight();
            }

            sb.append("Total " + total);

            TextArea textArea = new TextArea(sb.toString());
            textArea.setEditable(false);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(textArea);
            scrollPane.setFitToHeight(true);
            alert.getDialogPane().setContent(scrollPane);
            alert.showAndWait();
        }

    }

    // METODER
    private int parseTime(String timeString) {
        try {
            int time = Integer.parseInt(timeString);
            if (time < 0) {
                displayErrorMessage("Time can't be negative");
                return -1;
            }
            return time;

        } catch (NumberFormatException e) {
            displayErrorMessage("Please enter a time value");
            return -1;
        }
    }

    private boolean isMaxSelections() {
        return selectedPlaces.size() == 2;
    }

    private boolean connectionExists() {
        return places.getEdgeBetween(selectedPlaces.peekFirst(), selectedPlaces.peekLast()) != null;
    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirmUnsavedChanges() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText("Unsaved changes, continue anyway?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void clearGraph() {
        places = new ListGraph<>();
        selectedPlaces.clear(); // Grafik och data rensas i samma metod
        outputArea.getChildren().clear();
    }

    private void addLinesToPane(Place from, Place to) {
        Line line = new Line();
        line.setStartX(from.getCenterX());
        line.setStartY(from.getCenterY());
        line.setEndX(to.getCenterX());
        line.setEndY(to.getCenterY());
        outputArea.getChildren().add(line);
        line.toBack(); // Flyttar lines och imageview till bakgrunden så
        imageView.toBack(); // att circles hamnar i förgrunden
        line.setDisable(true);

    }

    private void addCirclesToPane(Place place) {
        outputArea.getChildren().add(place);
        place.setId(place.getName());
        StackPane.setAlignment(place, Pos.TOP_CENTER);
        Insets margins = new Insets(place.getCenterY(), 0, 0, place.getCenterX());
        StackPane.setMargin(place, margins);
        place.setOnMouseClicked(new PlaceClickHandler(place));
        changesMade = true;
    }

    private void showNewMap(Stage primaryStage) {
        clearGraph();
        Image mapImage = new Image(imageFileName);
        imageView = new ImageView(mapImage);
        outputArea.getChildren().add(imageView);
        primaryStage.setWidth(mapImage.getWidth() + 10);
        primaryStage.setHeight(mapImage.getHeight() + 105);
        changesMade = true;
    }

    public static void main(String[] args) {
        launch();
    }

}

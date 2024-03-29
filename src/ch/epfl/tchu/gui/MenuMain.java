package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

/**
 * MenuMain : this class will be the menu of the game
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class MenuMain extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MenuMain.fxml")));
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setScene(new Scene(root));
        stage.show();
    }
}


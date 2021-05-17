package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.List;

/**
 * ClientMain : This class represents the client who will play the game.
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class ClientMainBonus extends Application {
    /**
     * the main method of the programme
     *
     * @param args (String[]) :
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method will create and launch the graphical Player which represents our client.
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
        List<String> parameters = getParameters().getRaw();
        int i = 0;
        RemotePlayerClient client = new RemotePlayerClient(new GraphicalPlayerAdapter(),
                (parameters.isEmpty() || parameters.size() == 1)?"localhost": parameters.get(i++) ,
                parameters.isEmpty()?5108:Integer.parseInt(parameters.get(i)));
        new Thread(client::run).start();
    }
}

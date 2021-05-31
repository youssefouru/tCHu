package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.AdvancedPlayer;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.GameTest2;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ClientMain : This class represents the client who will play the game.
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Client2 extends Application {
    private final static String DEFAULT_HOST_NAME = "localhost";
    private final static int DEFAULT_PORT = 5108;
    /**
     * the main method of the programme
     *
     * @param args (String[]) : the arguments of the programme
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method will create and launch the graphical Player which represents our client.
     *
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     But in this class it has no utility
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> parameters = getParameters().getRaw();
        int i = 0;
        String instructionSocketName = (parameters.isEmpty() || parameters.size() == 1) ?DEFAULT_HOST_NAME: parameters.get(i++);
        int instructionSocketPort = parameters.isEmpty() ? DEFAULT_PORT : Integer.parseInt(parameters.get(i));
        Socket messageSocket = new Socket(instructionSocketName,instructionSocketPort);
        AdvancedPlayer graphicalPlayer =new GraphicalPlayerAdapter(messageSocket);
        RemotePlayerClient client = new RemotePlayerClient(graphicalPlayer, instructionSocketName, instructionSocketPort,messageSocket);
        new Thread(client::run).start();
        new Thread(client::manageMessages).start();
    }
}

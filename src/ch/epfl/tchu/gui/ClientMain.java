package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.AdvancedPlayer;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClientMain : This class represents the client who will play the game.
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class ClientMain  {
    private final static String DEFAULT_HOST_NAME = "localhost";
    private final static int DEFAULT_PORT = 5108;
    /**
     * the main method of the programme
     *
     * @param args (String[]) : the arguments of the programme
     */
    public static void main(String[] args) throws IOException {

        List<String> parameters = Arrays.stream(args).collect(Collectors.toList());
        int i = 0;
        String instructionSocketName = (parameters.isEmpty() ) ?DEFAULT_HOST_NAME: parameters.get(i++);
        int instructionSocketPort = parameters.isEmpty() || parameters.size() == 1? DEFAULT_PORT : Integer.parseInt(parameters.get(i));
        Socket messageSocket = new Socket(instructionSocketName,instructionSocketPort);
        AdvancedPlayer graphicalPlayer = new GraphicalPlayerAdapter(messageSocket);
        RemotePlayerClient client = new RemotePlayerClient(graphicalPlayer, instructionSocketName, instructionSocketPort,messageSocket);
        new Thread(client::run).start();
        new Thread(client::manageMessages).start();
    }


}

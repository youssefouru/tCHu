package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.AdvancedPlayer;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public final class MenuController {
    private final static String DEFAULT_HOST_NAME = "localhost";
    private final static int DEFAULT_PORT = 5108;
    private final static int portNumber = 5108;

    private static BooleanProperty hide = new SimpleBooleanProperty();
    private boolean isServer = false;
    private boolean launched = false;

    public static ReadOnlyBooleanProperty hide(){
        return hide;
    }
    @FXML
    private TextField nameField;

    @FXML
    private TextField portField;

    @FXML
    private TextField portNumberClient;

    @FXML
    private TextField hostNameField;

    @FXML
    private ListView<Text> texts;

    @FXML
    private Button startServerButton;

    @FXML
    private Button launchNgrokButton;

    @FXML
    private Button clientButton;

    /**
     * This method will start the Server
     *
     * @throws UncheckedIOException if something goes wrong
     */
    @FXML
    public void startServer() {
        ObservableList<Text> playersConnected = FXCollections.observableArrayList();
        texts.setItems(playersConnected);
        new Thread(() -> {
            try {
                ServerMain.main(nameField.getText().isEmpty() ? new String[]{} : nameField.getText().trim().split(Pattern.quote(" "), -1),
                                portField.getText().isEmpty() ? portNumber : Integer.parseInt(portField.getText().trim()),
                                playersConnected);
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        }).start();
        nodeDisabler(startServerButton,portField,nameField);
        isServer = true;
        launched = true;
    }

    private void nodeDisabler(Node... nodes){
        for (Node node : nodes) {
            node.disableProperty().set(true);
        }
    }


    /**
     * This method launch the client
     */
    @FXML
    public void connectClient() {
        ClientMain clientMain = new ClientMain();
        String[] args;
        if (hostNameField.getText().isEmpty() && !portNumberClient.getText().isEmpty()) {
            args = new String[]{portNumberClient.getText()};
        } else if (portNumberClient.getText().isEmpty()) {
            args = new String[]{};
        } else {
            args = new String[]{hostNameField.getText(), portNumberClient.getText()};
        }
        new Thread(()-> {
            try {
                ClientMain.main(args);
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        }
        ).start();
        if(!isServer) {
            hide.set(true);
        }
        if(!isServer){
            nodeDisabler(launchNgrokButton);
        }
        nodeDisabler(startServerButton,clientButton,nameField,hostNameField,portField,portNumberClient);
    }


    /**
     * This method will launch ngrok on the terminal
     * Warning : this method work only on mac and the command "chmod +x <path of launcher.sh>" has to be used first
     *
     * @throws IOException if something goes wrong
     */
    @FXML
    public void LaunchNgrok() throws IOException {

        ProcessBuilder builder = new ProcessBuilder("/bin/zsh", "src/launcher.sh", portField.getText().isEmpty() ? String.valueOf(portNumber) : portField.getText());
        nodeDisabler(launchNgrokButton);
        builder.start();
        if(!launched)
            startServer();
    }

}




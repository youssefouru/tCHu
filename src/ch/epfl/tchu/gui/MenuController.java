package ch.epfl.tchu.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.regex.Pattern;


public final class MenuController {

    private final static int portNumber = 5108;

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

    /**
     * This method will start the Server
     *
     * @throws IOException if something goes wrong
     */
    @FXML
    public void startServer() {
        ObservableList<Text> playersConnected = FXCollections.observableArrayList();
        texts.setItems(playersConnected);
        new Thread(() -> {
            try {
                ServerMain.main(nameField.getText().isEmpty() ? new String[]{} : nameField.getText().split(Pattern.quote(" "), -1), portField.getText().isEmpty() ? portNumber : Integer.parseInt(portField.getText()), playersConnected);
            } catch (IOException ignored) {

            }
        }).start();
        startServerButton.disableProperty().set(true);

    }


    /**
     * This method launch the client
     *
     * @throws IOException : if something goes wrong
     */
    @FXML
    public void connectClient() throws IOException {
        String[] args;
        if (hostNameField.getText().isEmpty() && !portNumberClient.getText().isEmpty()) {
            args = new String[]{portNumberClient.getText()};
        } else if (portNumberClient.getText().isEmpty()) {
            args = new String[]{};
        } else {
            args = new String[]{hostNameField.getText(), portNumberClient.getText()};
        }
        new Thread(() -> ClientMain.main(args)).start();
    }


    @FXML
    public void LaunchNgrok() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("/bin/zsh", "src/launcher.sh", portField.getText().isEmpty() ? String.valueOf(portField) : portField.getText());
        launchNgrokButton.disableProperty().set(true);
        builder.start();
        startServer();
    }


}




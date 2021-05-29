package ch.epfl.tchu.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.regex.Pattern;


public class MenuController {

    private final static int portNumber = 5108;

    @FXML
    private TextField nameField;

    @FXML
    private TextField portField;

    @FXML
    private TextField portNumberClient;

    @FXML
    private TextField hostNameField;

    /**
     * This method will start the Server
     *
     * @throws IOException if something goes wrong
     */
    @FXML
    public void startServer() {
       new Thread(()-> {
           try {
               ServerMain.main(nameField.getText().isEmpty() ? new String[]{} : nameField.getText().split(Pattern.quote(" "), -1), portField.getText().isEmpty() ? portNumber : Integer.parseInt(portField.getText()));
           } catch (IOException ioException) {

           }
       }).start();

    }


    /**
     * This method launch the client
     *
     * @throws IOException : if something goes wrong
     */
    @FXML
    public void connectClient() throws IOException {
        String[] args = new String[]{hostNameField.getText(), portNumberClient.getText()};
        ClientMain.main(args);
    }


    @FXML
    public void LaunchNgrok() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("src/launcher.sh", "./ngrok tcp 5108 --region eu");
        builder.start();
        startServer();
    }


}




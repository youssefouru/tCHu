package ch.epfl.tchu.game;

import java.util.List;

public interface AdvancedPlayer extends Player{
    /**
     * This method is used to send a message to the client bound to the player.
     *
     * @param serializedMessage (String) : the serialized message sent from the manager that we want to send tot the client
     */
    void sendToClient(String serializedMessage);

    /**
     * This method is used to verify if a message has been written in the socket of the client and write it in the socket of the manager
     */
    void sendToManager();

    /**
     * This method method is used by the client to send a message to the proxy that can be transmitted to the manager after
     * @param message (String) : the message we want to send to the proxy
     */
    void sendToProxy(String message);

    /**
     * This method is used to receive a message from a the socket of messages.
     */
    void receiveMessage(String message);


}

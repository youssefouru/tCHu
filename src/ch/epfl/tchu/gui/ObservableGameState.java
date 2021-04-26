package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ObservableGameState {
    private PlayerState playerState;
    private PublicGameState gameState;
    private final PlayerId playerId;
    private final IntegerProperty ticketPercentage =new SimpleIntegerProperty();
    private final IntegerProperty cardPercentage = new SimpleIntegerProperty();
    private final List<ObjectProperty<Card>> faceUpCards =createFaceUpCardProperty();
    private final ObjectProperty<Map<Route,PlayerId>> routes = createRoutes();
    private final IntegerProperty ticketCount = new SimpleIntegerProperty();
    private final IntegerProperty cardCount = new SimpleIntegerProperty();
    private final IntegerProperty carCount = new SimpleIntegerProperty();



    /**
     * Constructor of ObservableGameState
     *
     * @param playerId (PlayerId) : the id of the
     */
    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
        gameState = null;
        playerState = null;

    }

    /**
     * this method refresh the states of the game
     *
     * @param publicGameState (PublicGameState)  : the new publicGameState of the this observer
     * @param playerState (PlayerState) : the new playerState of the observer
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState){
        gameState = publicGameState;
        this.playerState = playerState;
        ticketPercentage.set((gameState.ticketsCount()/ChMap.tickets().size())*100);
        cardPercentage.set((gameState.cardState().deckSize()/Constants.ALL_CARDS.size())*100);
        for(int slot : Constants.FACE_UP_CARD_SLOTS){
            faceUpCards.get(slot).set(gameState.cardState().faceUpCard(slot));
        }
        updateRoutes();
        ticketCount.set(playerState.ticketCount());
        cardCount.set(playerState.cardCount());
        carCount.set(playerState.carCount());

    }

    /**
     *this method returns the readOnly part of the cardPercentage
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute cardPercentage
     */
    public ReadOnlyIntegerProperty cardPercentage(){
        return cardPercentage;
    }

    /**
     *this method returns the readOnly part of the ticketPercentage
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute ticketPercentage
     */
    public ReadOnlyIntegerProperty ticketPercentage(){
        return ticketPercentage;
    }

    /**
     * this method returns a mapProperty that associate each route to the id of the it's owner
     *
     * @return (ObjectProperty< Map< Route,PlayerId > >)
     */
    public ObjectProperty<Map<Route,PlayerId>> routes(){
        return routes;
    }

    /**
     * this method  return the readOnly objectProperty of the faceUpCard in the index slot
     *
     * @param slot (int) : the index of the faceUpCard that we want the objectProperty
     * @return (ReadOnlyObjectProperty< Card >) : the readOnlyObjectProperty of the card of index slot
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot){
        return faceUpCards.get(slot);
    }

    /**
     * this method returns the readOnly part of the the ticketCount of the playerState with the identification id
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute ticketCount
     */
    public ReadOnlyIntegerProperty ticketCount(){
        return ticketCount;
    }

    /**
     * this method returns the readOnly part of the the cardCount of the playerState with the identification id
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute cardCount
     */
    public ReadOnlyIntegerProperty cardCount(){
        return cardCount;
    }

    private List<ObjectProperty<Card>> createFaceUpCardProperty(){
        List<ObjectProperty<Card>> cards = new ArrayList<>();
        for (int i = 0; i <Constants.FACE_UP_CARDS_COUNT ; i++) {
            cards.add(null);
        }
        return cards;
    }

    private ObjectProperty<Map<Route,PlayerId>> createRoutes(){
        Map<Route,PlayerId>  map = new HashMap<>();
        for(Route route : ChMap.routes()){
           map.put(route,null);
        }
        ObjectProperty<Map<Route,PlayerId>> mapObjectProperty = new SimpleObjectProperty<>();
        mapObjectProperty.set(map);
        return mapObjectProperty ;
    }

    private void updateRoutes(){
        Map<Route,PlayerId> map = new HashMap<>();
        for(Route route : ChMap.routes()){
            PlayerId routeOwnerId = null;
            for(PlayerId playerId : PlayerId.ALL) {
                if (gameState.playerState(playerId).routes().contains(route)) {
                    routeOwnerId = playerId;
                    break;
                }
            }
            map.put(route,playerId);
         }
        routes.set(map);
    }


}

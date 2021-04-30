package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

public final class ObservableGameState {
    private final PlayerId playerId;
    private final IntegerProperty ticketPercentage = new SimpleIntegerProperty();
    private final IntegerProperty cardPercentage = new SimpleIntegerProperty();
    private final List<ObjectProperty<Card>> faceUpCards = createFaceUpCardProperty();
    private final Map<Route, ObjectProperty<PlayerId>> routeMap = new HashMap<>();
    private final IntegerProperty ticketCount = new SimpleIntegerProperty();
    private final IntegerProperty cardCount = new SimpleIntegerProperty();
    private final IntegerProperty carCount = new SimpleIntegerProperty();
    private final IntegerProperty claimPoints = new SimpleIntegerProperty();
    private final ObservableList<Ticket> playerTickets = observableArrayList();
    private final List<IntegerProperty> cardsTypeNumber = createsNumberOfCard();
    private final Map<Route, BooleanProperty> routeClaimable = new HashMap<>();
    private PlayerState playerState;
    private PublicGameState gameState;


    /**
     * Constructor of ObservableGameState
     *
     * @param playerId (PlayerId) : the id of the player to this class
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
     * @param playerState     (PlayerState) : the new playerState of the observer
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        gameState = publicGameState;
        this.playerState = playerState;
        ticketPercentage.set((gameState.ticketsCount() / ChMap.tickets().size()) * 100);
        cardPercentage.set((gameState.cardState().deckSize() / Constants.ALL_CARDS.size()) * 100);
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            faceUpCards.get(slot).set(gameState.cardState().faceUpCard(slot));
        }
        updateRoutes();
        ticketCount.set(playerState.ticketCount());
        cardCount.set(playerState.cardCount());
        carCount.set(playerState.carCount());
        claimPoints.set(playerState.claimPoints());
        playerTickets.setAll(playerState.tickets().toList());
        for (int i = 0; i < Card.COUNT; i++) {
            cardsTypeNumber.get(i).set(playerState
                    .cards()
                    .countOf(Card.ALL.get(i)));
        }


        updateClaimableRoute();


    }

    /**
     * this method returns the readOnly part of the cardPercentage
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute cardPercentage
     */
    public ReadOnlyIntegerProperty cardPercentage() {
        return cardPercentage;
    }

    /**
     * this method returns the readOnly part of the ticketPercentage
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute ticketPercentage
     */
    public ReadOnlyIntegerProperty ticketPercentage() {
        return ticketPercentage;
    }

    /**
     * this method returns the owner of the route in parameter
     *
     * @param route (Route) : the route we want to know the owner
     * @return (ObjectProperty < Map < Route, PlayerId > >) :  the readOnlyProperty of the owner of the route in parameter
     */
    public ReadOnlyObjectProperty<PlayerId> routeOwner(Route route) {
        return routeMap.get(route);
    }

    /**
     * this method  return the readOnly objectProperty of the faceUpCard in the index slot
     *
     * @param slot (int) : the index of the faceUpCard that we want the objectProperty
     * @return (ReadOnlyObjectProperty < Card >) : the readOnlyObjectProperty of the card of index slot
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot <= Constants.FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * this method returns the readOnly part of the the ticketCount of the playerState
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute ticketCount
     */
    public ReadOnlyIntegerProperty ticketCount() {
        return ticketCount;
    }

    /**
     * this method returns the readOnly part of the the cardCount of the playerState
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute cardCount
     */
    public ReadOnlyIntegerProperty cardCount() {
        return cardCount;
    }

    /**
     * this method returns the readOnly part of the the claimPoints of the playerState
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute claimPoints
     */
    public ReadOnlyIntegerProperty claimPoints() {
        return claimPoints;
    }

    /**
     * this method returns the readOnly part of the the tickets of the playerState
     *
     * @return (ObservableList < Ticket >) : the readOnly part of the attribute playerTickets
     */
    public ObservableList<Ticket> playerTickets() {
        return unmodifiableObservableList(playerTickets);
    }

    /**
     * this method returns the readOnly part of the number of each card of each type of the playerState
     *
     * @param card (Card) : the card we want to know it's number
     * @return (IntegerProperty) : the property of the number of the card in parameter
     */
    public ReadOnlyIntegerProperty cardsTypeNumber(Card card) {

        return cardsTypeNumber.get(card.ordinal());
    }

    /**
     * this method returns tells if the route is claimable
     *
     * @param route (Route) : the route we want to know if it's claimable
     * @return (ReadOnlyBooleanProperty) : the readOnlyPart of the boolean that tells us that the route is claimable
     */
    public ReadOnlyBooleanProperty claimable(Route route) {
        return routeClaimable.get(route);
    }

    /**
     * this method returns the ReadOnlyBooleanProperty that tells us if as player can draw cards
     *
     * @return (ReadOnlyBooleanProperty) : the readOnlyPart of the method canDrawCards() of gameState
     */
    public boolean canDrawCards() {
        return gameState.canDrawCards();
    }

    /**
     * this method returns the ReadOnlyBooleanProperty that tells us if we can draw tickets
     *
     * @return (ReadOnlyBooleanProperty) : the readOnlyPart of the method canDrawTickets() of gameState
     */
    public boolean canDrawTickets() {
        return gameState.canDrawTickets();
    }


    /**
     * this method returns a property of all the cards that the player can play to claim the cards
     *
     * @param route (Route) : the route the player wants to claim
     * @return (List < SortedBag < Card > >) : the possible cards that the player can play to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }


    private void updateClaimableRoute() {
        if (gameState == null) return;
        for (Route route : ChMap.routes()) {
            boolean routeClaimed = !gameState.claimedRoutes().contains(route);
            if (routeClaimed) {
                for (Route gameStateRoute : gameState.claimedRoutes()) {
                    if (!gameStateRoute.stations().containsAll(route.stations())) {
                        routeClaimed = false;
                        break;
                    }
                }
            }
            BooleanProperty booleanProperty = new SimpleBooleanProperty();
            booleanProperty.set(gameState.currentPlayerId() == playerId && playerState.canClaimRoute(route) && routeClaimed);
            routeClaimable.put(route, booleanProperty);
        }

    }

    private List<ObjectProperty<Card>> createFaceUpCardProperty() {
        List<ObjectProperty<Card>> cards = new ArrayList<>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            cards.add(new SimpleObjectProperty<>());
        }
        return cards;
    }

    private List<IntegerProperty> createsNumberOfCard() {
        List<IntegerProperty> listOfNumberOfCards = new ArrayList<>();
        for (int i = 0; i < Card.COUNT; i++) {
            IntegerProperty cardProperty = new SimpleIntegerProperty();
            cardProperty.set(0);
            listOfNumberOfCards.add(cardProperty);
        }
        return listOfNumberOfCards;
    }

    private void updateRoutes() {
        for (Route route : ChMap.routes()) {
            ObjectProperty<PlayerId> routeOwnerId = new SimpleObjectProperty<>();
            routeOwnerId.set(null);
            for (PlayerId playerId : PlayerId.ALL) {
                if (gameState.playerState(playerId).routes().contains(route)) {
                    routeOwnerId.set(playerId);
                    break;
                }
            }
            routeMap.put(route, routeOwnerId);
        }

    }


}

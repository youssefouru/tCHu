package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

/**
 * ObservableGameState : this class creates the observer of the gameState
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class ObservableGameState {
    private final static int MINIMAL_NUMBER = 3;
    private final PlayerId playerId;
    private final IntegerProperty ticketPct = new SimpleIntegerProperty();
    private final IntegerProperty cardPct = new SimpleIntegerProperty();
    private final List<ObjectProperty<Card>> faceUpCards = createFaceUpCardProperty();
    private final Map<Route, ObjectProperty<PlayerId>> routeMap = creatMap();
    private final Map<PlayerId, IntegerProperty> ticketCount = createPlayerIdMap();
    private final Map<PlayerId, IntegerProperty> cardCount = createPlayerIdMap();
    private final Map<PlayerId, IntegerProperty> carCount = createPlayerIdMap();
    private final Map<PlayerId, IntegerProperty> claimPoints = createPlayerIdMap();
    private final ObservableList<Ticket> playerTickets = observableArrayList();
    private final Map<Card, IntegerProperty> cardsTypeNumber = createsNumberOfCard();
    private final Map<Route, BooleanProperty> routeClaimable = createBooleanMap();
    private PlayerState playerState;
    private PublicGameState gameState;


    /**
     * Constructor of ObservableGameState
     *
     * @param playerId (PlayerId) : the id of the player linked to this class
     */
    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
        gameState = null;
        playerState = null;

    }

    private static Map<Route, ObjectProperty<PlayerId>> creatMap() {
        Map<Route, ObjectProperty<PlayerId>> mapRoute = new HashMap<>();
        for (Route route : ChMap.routes()) {
            mapRoute.put(route, new SimpleObjectProperty<>());
        }
        return mapRoute;
    }

    private static Map<PlayerId, IntegerProperty> createPlayerIdMap() {
        Map<PlayerId, IntegerProperty> map = new EnumMap<>(PlayerId.class);
        for (PlayerId playerId : PlayerId.ALL) {
            map.put(playerId, new SimpleIntegerProperty());
        }
        return map;
    }

    private static Map<Route, BooleanProperty> createBooleanMap() {
        Map<Route, BooleanProperty> map = new HashMap<>();
        for (Route route : ChMap.routes()) {
            map.put(route, new SimpleBooleanProperty());
        }
        return map;
    }

    private static List<ObjectProperty<Card>> createFaceUpCardProperty() {
        List<ObjectProperty<Card>> cards = new ArrayList<>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            cards.add(new SimpleObjectProperty<>());
        }
        return cards;
    }

    private static Map<Card, IntegerProperty> createsNumberOfCard() {
        Map<Card, IntegerProperty> listOfNumberOfCards = new EnumMap<>(Card.class);
        for (Card card : Card.ALL) {
            listOfNumberOfCards.put(card, new SimpleIntegerProperty());
        }
        return listOfNumberOfCards;
    }


    /**
     * this method refresh the states of the game
     *
     * @param gameState   (PublicGameState)  : the new gameState of the this observer
     * @param playerState (PlayerState) : the new playerState of the observer
     */
    public void setState(PublicGameState gameState, PlayerState playerState) {
        this.gameState = gameState;
        this.playerState = playerState;
        ticketPct.set((this.gameState.ticketsCount() * 100 / ChMap.tickets().size()));
        cardPct.set((this.gameState.cardState().deckSize() * 100 / Constants.ALL_CARDS.size()));
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            faceUpCards.get(slot).set(this.gameState.cardState().faceUpCard(slot));
        }
        updateRoutes();
        for (PlayerId playerId : PlayerId.ALL) {
            PublicPlayerState idState = this.gameState.playerState(playerId);
            ticketCount.get(playerId).set(idState.ticketCount());
            cardCount.get(playerId).set(idState.cardCount());
            carCount.get(playerId).set(idState.carCount());
            claimPoints.get(playerId).set(idState.claimPoints());

        }
        playerTickets.setAll(playerState.tickets().toList());
        for (Card card : Card.ALL) {
            cardsTypeNumber.get(card).set(playerState
                    .cards()
                    .countOf(card));
        }
        updateClaimableRoute();
    }

    /**
     * this method returns the readOnly part of the cardPct
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute cardPct
     */
    public ReadOnlyIntegerProperty cardPercentage() {
        return cardPct;
    }

    /**
     * this method returns the readOnly part of the ticketPct
     *
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the attribute ticketPct
     */
    public ReadOnlyIntegerProperty ticketPercentage() {
        return ticketPct;
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
        return faceUpCards.get(slot);
    }

    /**
     * this method returns the readOnly part of the the ticketCount of the playerState
     *
     * @param playerId (PlayerId) :  the player we want to know the the tickets count
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the ticket count of the player with the id in parameter in his identity
     */
    public ReadOnlyIntegerProperty ticketCount(PlayerId playerId) {
        return ticketCount.get(playerId);
    }

    /**
     * this method returns the readOnly part of the the cardCount of the playerState
     *
     * @param playerId (PlayerId) : the player we want to know the the card Count
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the card Count of the player with the id in parameter in his identity
     */
    public ReadOnlyIntegerProperty cardCount(PlayerId playerId) {
        return cardCount.get(playerId);
    }

    /**
     * this method returns the readOnly part of the the carCount of the playerState
     *
     * @param playerId (PlayerId) : the player we want to know the the car Count
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the car Count of the player with the id in parameter in his identity
     */
    public ReadOnlyIntegerProperty carCount(PlayerId playerId) {
        return carCount.get(playerId);
    }

    /**
     * this method returns the readOnly part of the the claimPoints of the playerState
     *
     * @param playerId (PlayerId) : the player we want to know the the claimPoints
     * @return (ReadOnlyIntegerProperty) : the readOnly part of the clainPoints of the player with the id in parameter in his identity
     */
    public ReadOnlyIntegerProperty claimPoints(PlayerId playerId) {
        return claimPoints.get(playerId);
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
        return cardsTypeNumber.get(card);
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
        for (Route route : ChMap.routes()) {
            boolean routeClaimed = !gameState.claimedRoutes().contains(route);
            if (routeClaimed) {
                for (Route gameStateRoute : gameState.claimedRoutes()) {
                    if (gameStateRoute.stations().containsAll(route.stations())) {
                        routeClaimed = PlayerId.COUNT >= MINIMAL_NUMBER;
                        break;
                    }
                }
            }
            routeClaimable.get(route).set(routeClaimed && gameState.currentPlayerId() == playerId && playerState.canClaimRoute(route));
        }
    }

    private void updateRoutes() {
        for (Route route : ChMap.routes()) {
            for (PlayerId playerId : PlayerId.ALL) {
                if (gameState.playerState(playerId).routes().contains(route)) {
                    routeMap.get(route)
                            .set(playerId);
                    break;
                }
            }
        }

    }


}

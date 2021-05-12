package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

/**
 * A PlayerState : this class represents the private part of a playerState
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;


    /**
     * Constructor of PlayerState
     *
     * @param tickets (SortedBag<Ticket>) : the list of tickets that the player has
     * @param cards   (SortedBag<Card>) : the cards that the player has
     * @param routes  (List<Route>) : list of routeOwner that the player has
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }


    /**
     * this method creates the initial state of the player
     *
     * @param initialCards (SortedBag<Card> ) : initial cards of the player
     * @return (PlayerState) : the initial state of the player
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    private static Color colorOfTheBag(SortedBag<Card> cards) {
        for (Card card : cards) {
            if (card.color() != null) {
                return card.color();
            }
        }
        return null;
    }

    private static int numberOfKinds(SortedBag<Card> sortedBag) {
        Color save = sortedBag.get(0).color();
        int count = 1;
        for (Card card : sortedBag) {
            if (save != card.color()) {
                ++count;
                save = card.color();
            }
        }
        return count;
    }

    /**
     * this method returns the tickets that the player has
     *
     * @return (Ticket) : the attribute ticket
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * return a new PlayerState with new tickets added to it's ticket
     *
     * @param newTickets (SortedBag<Ticket>) : the additional tickets
     * @return (PlayerState) : new PlayerState with the same attributes except for the tickets which we add the new tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }

    /**
     * return a new PlayerState with a new card added to it's cards
     *
     * @param card (Card) : the card added to the bag of cards
     * @return (PlayerState) : new PlayerState with the same attributes except for the cards which we add the new card
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets,cards.union(SortedBag.of(card)),routes());
    }

    /**
     * this method returns the cards of the player
     *
     * @return (SortedBag < Card >): the attribute cards
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * this method checks if the player can claim the route
     *
     * @param route (Route) : the route that the player want
     * @return (boolean) : if the player can claim the route route in parameter
     */
    public boolean canClaimRoute(Route route) {
        return route.length() <= carCount() && !possibleClaimCards(route).isEmpty();
    }

    /**
     * this method returns all the possible claim card to claim the route in parameter
     *
     * @param route (Route) : the route claimed
     * @return (List < SortedBag < Card > >): list of all the possible cards that we can use to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());
        List<SortedBag<Card>> possibleClaimCard = new ArrayList<>();
        for (int i = 0; i < route.possibleClaimCards().size(); ++i) {
            if (cards.contains(route.possibleClaimCards().get(i))) {
                possibleClaimCard.add(route.possibleClaimCards().get(i));
            }
        }
        return possibleClaimCard;
    }

    /**
     * this method returns the possible additional cards needed to claim an UnderGround route that the player can claim
     *
     * @param additionalCardsCount (int) :  the number of additional cards needed
     * @param initialCards         (SortedBag<Card>) : initial cards played to take the route
     * @return (List < SortedBag < Card > >) : returns the possible additional cards that can be played to take the route
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS && !initialCards.isEmpty() && numberOfKinds(initialCards) <= 2);
        SortedBag<Card> cards = cards().difference(initialCards);
        List<SortedBag<Card>> myList = new ArrayList<>();
        Color colorOfTheBag = colorOfTheBag(initialCards);
        if (colorOfTheBag == null) {
            SortedBag<Card> bag = SortedBag.of(additionalCardsCount, Card.LOCOMOTIVE);
            if (cards.contains(bag)) {
                myList.add(bag);
            }
            return myList;
        }
        Card card = Card.of(colorOfTheBag);
        //this for loop will add iteratively all the possible bags that the player can play and
        for (int i = 0; i <= additionalCardsCount; ++i) {
            SortedBag<Card> bagOfCards;
            if (i != additionalCardsCount) {
                bagOfCards = SortedBag.of(i, Card.LOCOMOTIVE, additionalCardsCount - i, card);

            } else {
                bagOfCards = SortedBag.of(additionalCardsCount, Card.LOCOMOTIVE);
            }
            if (cards.contains(bagOfCards)) {
                myList.add(bagOfCards);
            }
        }
        return myList;
    }

    /**
     * this method returns a new PlayerState with the route in parameter added to the routeOwner and we remove the claimCards from the cards
     *
     * @param route      (Route) : the route claimed with the claimCards
     * @param claimCards (SortedBag<Card>) : claimCard used to claim the route
     * @return (PlayerState) : a new PlayerState with the route in parameter added to the routeOwner and we remove the claimCards from the cards
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routes = new ArrayList<>(routes());
        routes.add(route);
        return new PlayerState(tickets, this.cards.difference(claimCards), routes);
    }

    private int findMaxId() {

        if (routes().size() == 0) {
            return 0;
        }
        int max = 0;
        for (Route route : routes()) {
            for (Station station : route.stations()) {
                if (station.id() >= max) {
                    max = station.id();
                }
            }
        }
        return max;
    }

    /**
     * return the total points that the player has with the all his tickets
     *
     * @return (int) : the total number of points that the player has
     */
    public int ticketPoints() {
        int i = 0;
        StationPartition.Builder builder = new StationPartition.Builder(findMaxId() + 1);
        for (Route route : routes()) {
            builder.connect(route.station1(), route.station2());
        }
        StationPartition partition = builder.build();
        for (Ticket ticket : tickets) {
            i += ticket.points(partition);
        }
        return i;
    }


    /**
     * this method returns the final points that the playerState has with adding the ticketPoints to the claimPoints
     *
     * @return (int) :the final points that the playerState has
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }

}

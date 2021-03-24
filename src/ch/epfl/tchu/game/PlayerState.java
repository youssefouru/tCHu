package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

/**
 * A PlayerState
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
     * @param routes  (List<Route>) : list of routes that the player has
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
     * return a new PlayerState with new cards added to it's cards
     *
     * @param additionalCards (SortedBag<Card>) : the additional cards
     * @return (PlayerState) : new PlayerState with the same attributes except for the cards which we add the new cards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, this.cards.union(additionalCards), routes());
    }

    /**
     * return a new PlayerState with a new card added to it's cards
     *
     * @param card (Card) : the card added to the bag of cards
     * @return (PlayerState) : new PlayerState with the same attributes except for the cards which we add the new card
     */
    public PlayerState withAddedCard(Card card) {
        List<Card> cards = this.cards.toList();
        cards.add(card);
        return new PlayerState(tickets, SortedBag.of(cards), routes());
    }

    /**
     * this method returns the cards of the player
     *
     * @return (SortedBag < Card >): the attribute cards
     */
    public SortedBag<Card> cards() {
        return this.cards;
    }

    /**
     * this method checks if the player can claim the route
     *
     * @param route (Route) : the route that the player want
     * @return (boolean) : if the player can claim the route route in parameter
     */
    public boolean canClaimRoute(Route route) {
        List<SortedBag<Card>> myList = route.possibleClaimCards();
        for (SortedBag<Card> sortedBag : myList) {
            if (cards.contains(sortedBag) && super.carCount() >= route.length()) {
                return true;
            }
        }
        return false;
    }

    /**
     * this method returns all the possible claim card to claim the route in parameter
     *
     * @param route (Route) : the route claimed
     * @return (List < SortedBag < Card > >): list of all the possible cards that we can use to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(super.carCount() >= route.length());
        List<SortedBag<Card>> myList = new ArrayList<>();
        for (int i = 0; i < route.possibleClaimCards().size(); ++i) {
            if (cards.contains(route.possibleClaimCards().get(i))) {
                myList.add(route.possibleClaimCards().get(i));
            }
        }
        return myList;
    }

    /**
     * this method returns the possible additional cards needed to claim an UnderGround route that the player can claim
     *
     * @param additionalCardsCount (int) :  the number of additional cards needed
     * @param initialCards         (SortedBag<Card>) : initial cards played to take the route
     * @param drawnCards           (SortedBag<Card>) : drawn cards
     * @return (List < SortedBag < Card > >) : returns the possible additional cards that can be played to take the route
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS && !initialCards.isEmpty() && numberOfKinds(initialCards) <= 2 && drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        SortedBag<Card> cards = cards().difference(initialCards);
        List<SortedBag<Card>> myList = new ArrayList<>();
        if (colorOfTheBag(initialCards) == null) {
            SortedBag<Card> bag = SortedBag.of(additionalCardsCount, Card.LOCOMOTIVE);
            if (cards.contains(bag)) {
                myList.add(bag);
            }
            return myList;
        }
        Card card = Card.of(colorOfTheBag(initialCards));
        for (int i = 0; i <= additionalCardsCount; ++i) {
            if (i != additionalCardsCount) {
                SortedBag<Card> bag = SortedBag.of(i, Card.LOCOMOTIVE, additionalCardsCount - i, card);
                if (cards.contains(bag)) {
                    myList.add(bag);
                }
            } else {
                SortedBag<Card> bag = SortedBag.of(additionalCardsCount, Card.LOCOMOTIVE);
                if (cards.contains(bag)) {
                    myList.add(bag);
                }
            }
        }
        return myList;
    }

    /**
     * this method returns a new PlayerState with the route in parameter added to the routes and we remove the claimCards from the cards
     *
     * @param route      (Route) : the route claimed with the claimCards
     * @param claimCards (SortedBag<Card>) : claimCard used to claim the route
     * @return (PlayerState) : a new PlayerState with the route in parameter added to the routes and we remove the claimCards from the cards
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routes = new ArrayList<>(routes());
        routes.add(route);
        return new PlayerState(tickets, this.cards.difference(claimCards), routes);
    }

    private int findMaxId() {
        int max = 0;
        if (routes().size() == 0) {
            return 0;
        }
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
     * this method returns the final points that the playerState has with adding the ticketpoints to the claimPoints
     *
     * @return (int) :the final points that the playerState has
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }

}

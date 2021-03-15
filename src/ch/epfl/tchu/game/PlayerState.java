package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class PlayerState extends PublicPlayerState{
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    public static PlayerState initial(SortedBag<Card> initialCards) {
        PlayerState initialPlayerState = new PlayerState(new SortedBag.Builder<Ticket>().build(), initialCards, new ArrayList<Route>());
        return initialPlayerState;
    }

    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    public SortedBag<Card> cards() {
        return cards;
    }

    public PlayerState withAddedCard (Card additionalCard) {
        SortedBag.Builder<Card> sortedBagBuilder = new SortedBag.Builder();
        sortedBagBuilder.add(cards).add(1, additionalCard);
        return new PlayerState(tickets, sortedBagBuilder.build(), routes());
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        SortedBag.Builder sortedBagBuilder = new SortedBag.Builder();
        sortedBagBuilder.add(cards).add(additionalCards);
        return new PlayerState(tickets, sortedBagBuilder.build(), routes());
    }

    /** Compute if a player can Claim a Route according to its cards and cards left
     *
     * @param route
     * @return if he can claim the route or not (boolean)
     */
    public boolean canClaimRoute(Route route) {
        int numberOfUsefulCards;
        if (route.level() == Route.Level.UNDERGROUND) {
            numberOfUsefulCards = numberOfCardsOfThisColor(route.color(), cards) + numberOfCardsOfThisColor(null, cards);
        } else {
            numberOfUsefulCards = numberOfCardsOfThisColor(route.color(), cards) + numberOfCardsOfThisColor(null, cards);
        }

        if (carCount() >= route.length() && ( numberOfUsefulCards >= route.length())) {
            return true;
        }
        return false;
    }

    /**
     * Wich returns the different combination of cards that allow a player to claim a road
     * @param route
     * @return
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        int numberOfCardsOfTheSameColor = numberOfCardsOfThisColor(route.color(), cards);
        int numberOfLocomotive = numberOfCardsOfLocomotive( cards);
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
        Preconditions.checkArgument(canClaimRoute(route));

        if (route.level() == Route.Level.UNDERGROUND) {
            for (int i = 1; i <= Math.min(route.length(), numberOfCardsOfTheSameColor); i++) {
                SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();
                int wagonStillToBeTaken = route.length() - i;

                if (numberOfLocomotive >= wagonStillToBeTaken) { // If there is enough locomotive cards to take the other cases
                    cardsBuilder.add(i, Card.of(route.color()));
                    cardsBuilder.add(wagonStillToBeTaken, Card.of(null));
                    possibleClaimCards.add(cardsBuilder.build());
                }
            }
        } else {
            if (numberOfCardsOfTheSameColor >= route.length()) {
                SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();
                cardsBuilder.add(route.length(), Card.of(route.color()));
                possibleClaimCards.add(cardsBuilder.build());
            }
        }

        return possibleClaimCards;
    }

    List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(!initialCards.isEmpty());
        Color colorOfTheRoad = colorOfTheCards(initialCards); // is null if there are only locomotive
        //number of cards that are initially played to tempt to claim the route
        int numberOfInitialColoredCards = numberOfCardsOfThisColor(colorOfTheRoad, cards);
        int numberOfInitialLocomotibes = numberOfCardsOfLocomotive(cards);
        // number of cards that belong to the player
        int numberOfCardsOfThisColor = 0;
        if (colorOfTheRoad != null) {
            numberOfCardsOfThisColor = numberOfCardsOfThisColor(colorOfTheRoad, cards);
        }
        int numberOfLocomotive = numberOfCardsOfLocomotive(cards);


        SortedBag.Builder<Card> usableCardBuilder = new SortedBag.Builder<>();
        // Here we add in cardBuilder all the cards that are usable, and that are still in possession of the player. That is when removing the corresponding initial cards (the cards that have been played to attemp the claim of the tunnel)
        usableCardBuilder.add(numberOfCardsOfThisColor - numberOfInitialColoredCards, Card.of(colorOfTheRoad)).add(numberOfLocomotive - numberOfInitialLocomotibes, Card.of(null));
        // Since it's a Set, every subset of the usable Cards appears only one time
        Set<SortedBag<Card>> possibleAdditionalCards = usableCardBuilder.build().subsetsOfSize(additionalCardsCount);
        List<SortedBag<Card>> possibleAdditionalCardsList = new ArrayList<>(possibleAdditionalCards);
        possibleAdditionalCardsList.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return possibleAdditionalCardsList;
    }
    /**
     * Return the number of cards of a given color (please note that a Locomotive is also considered to be a color)
     *
     * @param color
     * @param
     * @return the number of cards of this color
     */
    private static int numberOfCardsOfThisColor(Color color, SortedBag<Card> cards) {
        int numberOfCardsOfThisColor = 0;
        for (Card card : cards) {
            if (card.color() == color) {
                numberOfCardsOfThisColor++;
            }
        }
        return numberOfCardsOfThisColor;
    }

    /**
     * Return the color of the wagon cards that are all similar and null if there are no color cards (and throw IllegalArgumentExceptionIfNot)
     * @return
     */
    private static Color colorOfTheCards(SortedBag<Card> initialCards) {
        int moreThanOneColor = 0;
        Color color = null;

        for (Card card : initialCards) {
            if (card.color() != null && color != card.color()) {
                color = card.color();
                moreThanOneColor++; //increment this variable every Time the color is not null, so that we know when there is more than one non-null color
            }
        }
        Preconditions.checkArgument(moreThanOneColor<2);
        return color;

    }
    private static int numberOfCardsOfLocomotive( SortedBag<Card> cards) {
        int numberOfLocomotive = 0;
        for (Card card : cards) {
            if (card.color() == null) {
                numberOfLocomotive++;
            }
        }
        return numberOfLocomotive;
    }

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();
        cardsBuilder.add(cards()).add(claimCards);
        List<Route> routes = new ArrayList<>(routes());
        routes.add(route);
        PlayerState withClaimedRoutes = new PlayerState(tickets, cardsBuilder.build(), routes);

        return withClaimedRoutes;
    }

    /*public int ticketPoints() {

    }*/








}

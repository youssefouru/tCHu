package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

/**
 * Info
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Info {
    private final String player;

    /**
     * Constructor of Info
     *
     * @param player (String) : the name of the player
     */
    public Info(String player) {
        this.player = player;
    }

    /**
     * Return a string with the name of the card and its color (with an s attached to the name iff count equals 1 or -1)
     *
     * @param card  (Card) : the card that we want to have the name
     * @param count (int) : the number of cards
     * @return (String) the String described
     */
    public static String cardName(Card card, int count) {
        String color = "";
        String cardName;
        switch (card) {
            case BLACK:
                color = StringsFr.BLACK_CARD;
                break;
            case VIOLET:
                color = StringsFr.VIOLET_CARD;
                break;
            case BLUE:
                color = StringsFr.BLUE_CARD;
                break;
            case GREEN:
                color = StringsFr.GREEN_CARD;
                break;
            case YELLOW:
                color = StringsFr.YELLOW_CARD;
                break;
            case ORANGE:
                color = StringsFr.ORANGE_CARD;
                break;
            case RED:
                color = StringsFr.RED_CARD;
                break;
            case WHITE:
                color = StringsFr.WHITE_CARD;
                break;
            case LOCOMOTIVE:
                color = StringsFr.LOCOMOTIVE_CARD;
                break;

        }

        color = color + sIfPlural(count);
        cardName = String.format("%s", color);


        return cardName;
    }

    private static String sIfPlural(int number) {
        String str = "";
        if (number > 1 || number == 0) {
            str = "s";
        }
        return str;
    }

    private static String routePrinter(Route route) {
        return String.format("%s%s%s", route.station1(), StringsFr.EN_DASH_SEPARATOR, route.station2());
    }

    private static String cardsCollectionPrinter(SortedBag<Card> cardsCollection) {
        StringBuilder toBeDisplayed = new StringBuilder();
        int counter = 0;
        for (Card c : cardsCollection.toSet()) {
            int n = cardsCollection.countOf(c);
            if (counter == 0) {
                if (n > 1) {
                    toBeDisplayed.append(String.format("%s %s", n, cardName(c, n)));
                } else {
                    toBeDisplayed.append(String.format("%s %s", n, cardName(c, n)));
                }
            } else if (counter == cardsCollection.toSet().size() - 1) {
                if (n > 1) {
                    toBeDisplayed.append(String.format("%s%s %s", StringsFr.AND_SEPARATOR, n, cardName(c, n)));
                } else {
                    toBeDisplayed.append(String.format("%s%s %s", StringsFr.AND_SEPARATOR, n, cardName(c, n)));
                }

            } else if (n > 1) {
                toBeDisplayed.append(String.format(", %s %s", n, cardName(c, n)));
            } else {
                toBeDisplayed.append(String.format(", %s %s", n, cardName(c, n)));
            }
            counter++;
        }

        return toBeDisplayed.toString();

    }

    /**
     * Displays if there is a draw between two players with the number of points
     *
     * @param playerNames (String) : the name of the player
     * @param points      (int) : the number of points
     * @return (String) if there is a draw between two players with the number of points
     */
    public static String draw(List<String> playerNames, int points) {
        String bothPlayer = String.format("%s%s%s", playerNames.get(0), StringsFr.AND_SEPARATOR, playerNames.get(1));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(playerNames.get(0));
        stringBuilder.append(StringsFr.AND_SEPARATOR);
        stringBuilder.append(playerNames.get(1));
        return String.format(StringsFr.DRAW, stringBuilder, points);
    }

    /**
     * Displays the next player to play first
     *
     * @return (String) : the name of the player who will play first
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, player);
    }

    /**
     * Displays the number of tickets the player has chosen to keep
     *
     * @param count (int) : number of tickets
     * @return (String) : that the player has count ticket
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, player, count, sIfPlural(count));
    }


    /**
     * Displays that the the players can play
     *
     * @return (String) : that the player can play
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, player);
    }

    /**
     * Displays that a certain player drew tickets
     *
     * @param count (int) : the number of tickets drawn
     * @return (String) : that the player drew count ticket
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, player, count, sIfPlural(count));
    }


    /**
     * Displays that a player drew a blind card
     *
     * @return (String) : that the player drawn a blind card
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, player);
    }

    /**
     * Displays that a player drew a visible card, with the name of the card
     *
     * @param card (Card) : the card drawn
     * @return (String) : that the player drew the card in parameter
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, player, cardName(card, 1));
    }

    /**
     * Displays the message that a player Claimed a route with the given cards
     *
     * @param route        (Route) : the route that has been claimed
     * @param initialCards (SortedBag<Card>) : the initial cards
     * @return (String) : the player has claimed the route with initialCards
     */
    public String claimedRoute(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.CLAIMED_ROUTE, player, routePrinter(route), cardsCollectionPrinter(initialCards));
    }

    /**
     * displays that a player attempt to claim a tunnel with the given cards
     *
     * @param route        (Route) : the route that has been claimed
     * @param initialCards (SortedBag<Card>) : the initial cards
     * @return (String) : the player has claimed the underground route with initialCards
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, player, routePrinter(route), cardsCollectionPrinter(initialCards));
    }

    /**
     * Displays that a player drew additional cards, with the cards drawn and the additional cost
     *
     * @param drawnCards     (SortedBag<Card>) : the cards drawn
     * @param additionalCost (int) : the additional cost
     * @return (String) :a player drew additional cards, with the cards drawn and the additional cost
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardsCollectionPrinter(drawnCards)));
        if (additionalCost == 0) {
            stringBuilder.append(String.format(StringsFr.NO_ADDITIONAL_COST));
        } else {
            stringBuilder.append(String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, sIfPlural(additionalCost)));
        }
        return stringBuilder.toString();
    }

    /**
     * Displays that a player didn't claimed a route, with the name of the route
     *
     * @param route (Route) : the route that hasn't been claimed
     * @return (String) : the player hasn't claimed the route
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, player, routePrinter(route));
    }

    /**
     * Displays that the last turn of the play is beginning, and indicates the number of cars of one of the player
     *
     * @param carCount (int) : number of Wagons
     * @return (String) : the number of wagons left for the player
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, player, carCount, sIfPlural(carCount));
    }

    /**
     * Displays that a player got the longest trail bonus, with the longest trail displayed
     *
     * @param longestTrail (Trail) : the trail that we want to display
     * @return (String) : a player got the longest trail bonus, with the longest trail displayed
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        String s = String.format("%s%s%s", longestTrail.station1(), StringsFr.EN_DASH_SEPARATOR, longestTrail.station2());
        return String.format(StringsFr.GETS_BONUS, player, s);
    }

    /**
     * Display that a player won, among the number of points of the winner and the number of points of the loser
     *
     * @param points      (int) : the number of points of the player
     * @param loserPoints (int) : the loser points of the other player
     * @return (String) : a player won, among the number of points of the winner and the number of points of the loser
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, player, points, sIfPlural(points), loserPoints, sIfPlural(loserPoints));
    }


}

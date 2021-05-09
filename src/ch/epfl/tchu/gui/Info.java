package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;



/**
 * Info : this class gathers all the info needed in the game
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Info {
    private final String player;
    private final CardBagStringConverter cardBagStringConverter;


    /**
     * Constructor of Info
     *
     * @param player (String) : the name of the player
     */
    public Info(String player) {
        this.player = player;
        cardBagStringConverter = new CardBagStringConverter();
    }

    /**
     * Return a string with the name of the card and its color (with an s attached to the name iff count equals 1 or -1)
     *
     * @param card  (Card) : the card that we want to have the name
     * @param count (int) : the number of cards
     * @return (String) the String described
     */
    public static String cardName(Card card, int count) {
        String color;
        switch (card) {
            case BLACK:
                return StringsFr.BLACK_CARD + StringsFr.plural(count);
            case VIOLET:
                return StringsFr.VIOLET_CARD + StringsFr.plural(count);
            case BLUE:
                return StringsFr.BLUE_CARD + StringsFr.plural(count);
            case GREEN:
                return StringsFr.GREEN_CARD + StringsFr.plural(count);
            case YELLOW:
                return  StringsFr.YELLOW_CARD+ StringsFr.plural(count);
            case ORANGE:
                return StringsFr.ORANGE_CARD + StringsFr.plural(count);
            case RED:
                return StringsFr.RED_CARD+ StringsFr.plural(count);
            case WHITE:
                return StringsFr.WHITE_CARD + StringsFr.plural(count);
            case LOCOMOTIVE:
                return StringsFr.LOCOMOTIVE_CARD  + StringsFr.plural(count);
            default:
                throw new Error();

        }

    }


    private static String routePrinter(Route route) {
        return String.format("%s%s%s", route.station1(), StringsFr.EN_DASH_SEPARATOR, route.station2());
    }


    /**
     * Displays if there is a draw between two players with the number of points
     *
     * @param playerNames (String) : the name of the player
     * @param points      (int) : the number of points
     * @return (String) if there is a draw between two players with the number of points
     */
    public static String draw(List<String> playerNames, int points) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < playerNames.size(); ++i) {
            String separator = i == playerNames.size() - 2 ? StringsFr.AND_SEPARATOR : ",";
            separator = i == playerNames.size() - 1 ? "" : separator;
            builder.append(playerNames.get(i));
            builder.append(separator);
        }

        return String.format(StringsFr.DRAW, builder, points);
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
        return String.format(StringsFr.KEPT_N_TICKETS, player, count, StringsFr.plural(count));
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
        return String.format(StringsFr.DREW_TICKETS, player, count, StringsFr.plural(count));
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
        return String.format(StringsFr.CLAIMED_ROUTE, player, routePrinter(route), cardBagStringConverter.toString(initialCards));
    }

    /**
     * displays that a player attempt to claim a tunnel with the given cards
     *
     * @param route        (Route) : the route that has been claimed
     * @param initialCards (SortedBag<Card>) : the initial cards
     * @return (String) : the player has claimed the underground route with initialCards
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {

        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, player, routePrinter(route), cardBagStringConverter.toString(initialCards));
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
        stringBuilder.append(String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardBagStringConverter.toString(drawnCards)));
        if (additionalCost == 0) {
            return stringBuilder.append(String.format(StringsFr.NO_ADDITIONAL_COST)).toString();
        } else {
            return stringBuilder.append(String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost))).toString();
        }
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
        return String.format(StringsFr.LAST_TURN_BEGINS, player, carCount, StringsFr.plural(carCount));
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
        return String.format(StringsFr.WINS, player, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }


}

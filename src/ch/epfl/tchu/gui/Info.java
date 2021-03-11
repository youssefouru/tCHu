package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.text.Format;
import java.util.Collection;
import java.util.List;

public final class Info {
    private final String player;

    public Info(String player) {
        this.player = player;
    }

    /**
     *Return a string with the name of the card and its color (with an s attached to the name iff count equals 1 or -1
     * @param card
     * @param count
     * @return the String described
     *
     */

    public static String cardName(Card card, int count) {
        String color = null;
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

        color =color + sIfPlural(count);
        cardName = String.format("%s",  color);


        return cardName;
    }

    private static String sIfPlural(int number) {
        String str ="";
        if (number > 1) {
            str ="s";
        }
        return str;
    }

    private static String routePrinter(Route route) {
        return String.format("%s%s%s", route.station1(), StringsFr.EN_DASH_SEPARATOR, route.station2());
    }

    private static String cardsCollectionPrinter(SortedBag<Card> cardsCollection) {
        StringBuilder toBeDisplayed = new StringBuilder();
        int counter = 0;
        for (Card c: cardsCollection.toSet()) {
            int n = cardsCollection.countOf(c);
            if (counter == 0) {
                if (n > 1) {
                    toBeDisplayed.append(String.format("%s %s", n, cardName(c,n)));
                } else {
                    toBeDisplayed.append(String.format("%s %s", n, cardName(c,n)));
                }
            }
            else if ( counter == cardsCollection.toSet().size() - 1) {
                if (n > 1) {
                    toBeDisplayed.append(String.format("%s%s %s",StringsFr.AND_SEPARATOR, n, cardName(c,n)));
                } else {
                    toBeDisplayed.append(String.format("%s%s %s",StringsFr.AND_SEPARATOR, n, cardName(c,n)));
                }

            } else if (n > 1) {
                toBeDisplayed.append(String.format(", %s %s ", n, cardName(c,n)));
            } else {
                toBeDisplayed.append(String.format(", %s %s", n, cardName(c,n)));
            }
            counter++;
        }

        return toBeDisplayed.toString();

    }

    /**
     * Displays if there is a draw between two players with the number of points
     * @param playerNames
     * @param points
     * @return
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
     * Displays the next plyaer to play first
     * @return
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, player);
    }

    /**
     * Displays the number of tickets the player has chosen to keep
     * @param count
     * @return
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, player, count, sIfPlural(count));
    }

    /**
     * Displays that one of the players can play
     * @return
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, player);
    }

    /**
     * Displays that a certain player drew tickets
     * @param count
     * @return
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, player, count, sIfPlural(count));
    }

    /**
     * Displays that a plyaer drew a blind card
     * @return
     */
    public String drewBlindCard() {
        String string = "";
        return String.format(StringsFr.DREW_BLIND_CARD, player);
    }

    /**
     * Displays that a player drew a visible card, with the name of the card
     * @param card
     * @return
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, player,cardName(card,1));
    }

    /**
     * Displays the message that a player Claimed a route with the given cards
     * @param route
     * @param initialCards
     * @return
     */
    public String claimedRoute(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.CLAIMED_ROUTE, player, routePrinter(route), cardsCollectionPrinter(initialCards));
    }

    /**
     * displays that a player attempt to claim a tunnel with the given cards
     * @param route
     * @param initialCards
     * @return
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, player, routePrinter(route), cardsCollectionPrinter(initialCards) );
    }

    /**
     * Displays that a player drew additional cards, with the cards drawn and the additional cost
     * @param drawnCards
     * @param additionalCost
     * @return
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardsCollectionPrinter(drawnCards)));
        if (additionalCost == 0) {
            stringBuilder.append(String.format(StringsFr.NO_ADDITIONAL_COST));
        } else {
            stringBuilder.append(String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost,sIfPlural(additionalCost)));
        }
        return stringBuilder.toString();
    }

    /**
     * Displays that a player didn't claimed a route, with the name of the route
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, player, routePrinter(route));
    }

    /**
     * Displays that the last turn of the play is beginning, and indicates the number of cars of one of the player
     * @param carCount
     * @return
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, player, carCount, sIfPlural(carCount));
    }

    /**
     * Displays thaht a player got the longest trail bonus, with the longest trail displayed
     * @param longestTrail
     * @return
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, player, longestTrail.toString());
    }

    /**
     * Display that a player won, among the number of points of the winner and the number of points of the loser
     * @param points
     * @param loserPoints
     * @return
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, player, points,sIfPlural(points), loserPoints, sIfPlural(loserPoints));
    }



}

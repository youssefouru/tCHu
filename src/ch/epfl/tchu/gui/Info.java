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
                    toBeDisplayed.append(String.format("%s %s %s",StringsFr.AND_SEPARATOR, n, cardName(c,n)));
                } else {
                    toBeDisplayed.append(String.format("%s %s",StringsFr.AND_SEPARATOR, n, cardName(c,n)));
                }

            } else if (n > 1) {
                toBeDisplayed.append(String.format("%s %s ", n, cardName(c,n)));
            } else {
                toBeDisplayed.append(String.format("%s %s", n, cardName(c,n)));
            }
            counter++;
        }

        return toBeDisplayed.toString();

    }

    public static String draw(List<String> playerNames, int points) {
        String bothPlayer = String.format("%s%s%s", playerNames.get(0), StringsFr.AND_SEPARATOR, playerNames.get(1));
        return String.format(StringsFr.DRAW, playerNames, points);
    }

    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, player);
    }

    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, player, count, sIfPlural(count));
    }

    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, player);
    }

    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, player, count, sIfPlural(count));
    }

    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, player);
    }

    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, player,cardName(card,1));
    }

    public String claimedRoute(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.CLAIMED_ROUTE, player, routePrinter(route), cardsCollectionPrinter(initialCards));
    }

    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, player, routePrinter(route), cardsCollectionPrinter(initialCards) );
    }

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

    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, player, routePrinter(route));
    }

    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, player, carCount, sIfPlural(carCount));
    }

    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, player, longestTrail.toString());
    }

    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, player, points,sIfPlural(points), loserPoints, sIfPlural(loserPoints));
    }



}

package ch.epfl.tchu.game;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.gui.StringsFr;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class InfoTest {
    private final String pn1="Hana";
    private final Info info =new Info(pn1);
    @Test
    void cardNameWorksSingular(){
        assertEquals("bleue", Info.cardName(Card.BLUE, 1));
    }
    @Test
    void cardNameWorksPlural(){
        assertEquals("bleues", Info.cardName(Card.BLUE, 22));
    }
    @Test
    void drawWorks(){
        String pn2="Zaineb";
        String expected="\nHana et Zaineb sont ex æqo avec 100 points !\n";
        assertEquals(expected, Info.draw(List.of(pn1, pn2), 100));
    }
    @Test
    void willPlayFirstWorks(){
        String expected = "Hana jouera en premier.\n\n";
        assertEquals(expected, info.willPlayFirst());
    }
    @Test
    void keptTicketsWorksSingular() {
        String expected = "Hana a gardé 1 billet.\n";
        assertEquals(expected, info.keptTickets(1));
    }
    @Test
    void keptTicketsPlural() {
        String expected = "Hana a gardé 100 billets.\n";
        assertEquals(expected, info.keptTickets(100));
    }
    @Test
    void canPlayWorks() {
        String expected = "\nC'est à Hana de jouer.\n";
        assertEquals(expected, info.canPlay());
    }
    @Test
    void drewTicketsWorksSingular() {
        String expected = "Hana a tiré 1 billet...\n";
        assertEquals(expected, info.drewTickets(1));
    }
    @Test
    void drewTicketsWorksPlural() {
        String expected = "Hana a tiré 100 billets...\n";
        assertEquals(expected, info.drewTickets(100));
    }
    @Test
    void drewBlindCardWorks() {
        String expected = "Hana a tiré une carte de la pioche.\n";
        assertEquals(expected, info.drewBlindCard());
    }
    @Test
    void drewVisibleCardWorks() {
        String expected = "Hana a tiré une carte verte visible.\n";
        assertEquals(expected, info.drewVisibleCard(Card.GREEN));
    }
    @Test
    void claimedRouteWorks() {
        Route route = ChMap.routes().get(2);
        SortedBag<Card> cards = SortedBag.of(1, Card.LOCOMOTIVE, 2, Card.ORANGE);
        String expected = "Hana a pris possession de la route Baden"+ StringsFr.EN_DASH_SEPARATOR+"Bâle au moyen de 2 oranges et 1 locomotive.\n";
        assertEquals(expected, info.claimedRoute(route, cards));
    }
    @Test
    void attemptsTunnelClaimWorks(){
        String expected = "Hana tente de s'emparer du tunnel Baden"+StringsFr.EN_DASH_SEPARATOR+"Zürich au moyen de 3 violettes, 5 bleues, 1 orange et 3 locomotives !\n";
        SortedBag<Card> cards = new SortedBag.Builder().
                                add(3, Card.LOCOMOTIVE).
                                add(3, Card.VIOLET).
                                add(1, Card.ORANGE).
                                add(5,Card.BLUE).
                                build();
        Route route = ChMap.routes().get(4);
        assertEquals(expected, info.attemptsTunnelClaim(route, cards));
    }
    @Test
    void drewAdditionalCardsWorks1() {
        String expected = "Les cartes supplémentaires sont 3 blanches. Elles impliquent un coût additionnel de 3 cartes.\n";
        SortedBag<Card> drawnCards = SortedBag.of(3, Card.WHITE);
        assertEquals(expected, info.drewAdditionalCards(drawnCards, 3));
    }
    @Test
    void drewAdditionalCardsWorks2() {
        String expected = "Les cartes supplémentaires sont 3 blanches. "+"Elles n'impliquent aucun coût additionnel.\n";
        SortedBag<Card> drawnCards = SortedBag.of(3, Card.WHITE);
        assertEquals(expected, info.drewAdditionalCards(drawnCards, 0));
    }
    @Test
    void didNotClaimRouteWorks() {
        String expected = "Hana n'a pas pu (ou voulu) s'emparer de la route Autriche"+StringsFr.EN_DASH_SEPARATOR+"Saint-Gall.\n";
        assertEquals(expected, info.didNotClaimRoute(ChMap.routes().get(0)));
    }
    @Test
    void lastTurnBeginsWorks() {
        String expected = "\nHana n'a plus que 4 wagons, le dernier tour commence !\n";
        assertEquals(expected, info.lastTurnBegins(4));
    }
    @Test
    void getsLongestTrailBonusWorks() {
        Trail trail = Trail.longest(List.of(ChMap.routes().get(2), ChMap.routes().get(6)));
        String expected = "\nHana reçoit un bonus de 10 points pour le plus long trajet"+" ("+trail+").\n";
        assertEquals(expected, info.getsLongestTrailBonus(trail));
    }
    @Test
    void wonWorks() {
        String expected = "\nHana remporte la victoire avec 564 points, contre 89 points !\n";
        assertEquals(expected, info.won(564, 89));
    }
}
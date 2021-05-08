package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ch.epfl.tchu.SortedBag.Builder;
import static ch.epfl.tchu.gui.Info.cardName;

public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    private final Map<String, Card> cardMap;

    /**
     * Constructor  of CardBagStringConverter
     */
    public CardBagStringConverter() {
        cardMap = new HashMap<>();
        for (Card card : Card.ALL) {
            cardMap.put(Info.cardName(card, 1), card);
            cardMap.put(Info.cardName(card,0),card);
        }
    }

    /**
     * this method convert a sorted bag of cards in a string representation
     * @param cardsCollection (SortedBag< Card >) : the bag of cards we want to convert into a string representation
     * @return (String) : the string representation of a bag
     */
    @Override
    public String toString(SortedBag<Card> cardsCollection) {
        StringBuilder toBeDisplayed = new StringBuilder();
        int counter = 0;
        for (Card c : cardsCollection.toSet()) {
            int n = cardsCollection.countOf(c);
            if (counter == 0) {
                toBeDisplayed.append(String.format("%s %s", n, cardName(c, n)));
            } else if (counter == cardsCollection.toSet().size() - 1) {
                toBeDisplayed.append(String.format("%s%s %s", StringsFr.AND_SEPARATOR, n, cardName(c, n)));
            } else {
                toBeDisplayed.append(String.format(", %s %s", n, cardName(c, n)));
            }
            counter++;
        }

        return toBeDisplayed.toString();
    }

    @Override
    public SortedBag<Card> fromString(String string) {
        String[] stringTab = string.split(Pattern.quote(" "), -1);
        List<String> filteredList = Arrays.stream(stringTab).filter((s) -> !(s.equals("et") || s.equals(","))).collect(Collectors.toList());
        Builder<Card> cardBuilder = new Builder<>();
        for(int i = 0 ; i< filteredList.size(); i = i+2){
            String cardName =filteredList.get(i+1);

            cardBuilder.add(Integer.parseInt(filteredList.get(i)),
                             cardMap.get(cardName.endsWith(",") || cardName.endsWith("s")? cardName.substring(0,cardName.length() -1) : cardName));
        }
        return cardBuilder.build();
    }

}

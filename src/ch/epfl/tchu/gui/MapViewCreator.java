package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * MapViewCreator : this class creates the view of the map
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class MapViewCreator {
    private final static int CIRCLE_RADIUS = 3;
    private final static int RECTANGLE_WIDTH = 36;
    private final static int RECTANGLE_HEIGHT = 12;
    private final static int CIRCLE_X_POS = 12;
    private final static int CIRCLE_Y_POS = 6;

    private MapViewCreator(){
    }
    /**
     * this method create the view of the map
     *
     * @param gameState    (ObservableGameState) : the observer of the gameState
     * @param claimRouteHP (ObjectProperty< ActionHandlers.ClaimRouteHandler >) : the  handler of the route
     * @param cardChooser  (CardChooser) : the chooser of the cards
     * @return (Node) : the view of the map
     */
    public static Node createMapView(ObservableGameState gameState, ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHP, CardChooser cardChooser) {
        Pane mapView = new Pane();
        mapView.getStylesheets().addAll("map.css", "colors.css");
        ImageView imageView = new ImageView("map.png");
        mapView.getChildren().add(imageView);


        for (Route route : ChMap.routes()) {

            Group routeGroup = new Group();
            routeGroup.setId(route.id());


            routeGroup.disableProperty().bind(claimRouteHP.isNull().or(gameState.claimable(route).not()));
            routeGroup.setOnMouseClicked((e) -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);
                ActionHandlers.ClaimRouteHandler claimRouteH = claimRouteHP.get();
                if (possibleClaimCards.size() == 1) {
                    claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                } else {
                    ActionHandlers.ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }

            });

            routeGroup.getStyleClass().addAll("route",
                    route.level().name(),
                    route.color() == null ? "NEUTRAL" : route.color().name());

            gameState.routeOwner(route).addListener((o, oV, nV) -> routeGroup.getStyleClass().add(nV.name()));


            for (int i = 1; i <= route.length(); i++) {
                Rectangle wayRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                wayRectangle.getStyleClass().addAll("track", "filled");
                Rectangle carRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                carRectangle.getStyleClass().add("filled");
                Group carGroup = new Group(carRectangle, new Circle(CIRCLE_X_POS, CIRCLE_Y_POS, CIRCLE_RADIUS), new Circle(2 * CIRCLE_X_POS, CIRCLE_Y_POS, CIRCLE_RADIUS));
                carGroup.getStyleClass().add("car");
                Group caseGroup = new Group(wayRectangle, carGroup);
                caseGroup.setId(route.id() + "_" + i);
                routeGroup.getChildren().add(caseGroup);
            }

            mapView.getChildren().add(routeGroup);
        }


        return mapView;

    }


    @FunctionalInterface
    interface CardChooser {
        /**
         * this method is called when the player has to choose cards
         *
         * @param options (List< SortedBag< Card > >) : the cards that the player has to chose
         * @param handler (ChooseCardsHandler) : the handler
         */
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }
}

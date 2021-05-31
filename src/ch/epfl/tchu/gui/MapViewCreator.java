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
final class MapViewCreator {
    private final static int CIRCLE_RADIUS = 3;
    private final static int RECTANGLE_WIDTH = 36;
    private final static int RECTANGLE_HEIGHT = 12;
    private final static int SPACE_BETWEEN_CIRCLE = 6;
    private final static String MAP_NAME = "map.png";
    private final static String MAP_CLASS = "map.css";
    private final static String COLOR_CLASS = "colors.css";
    private final static String TRACK_STRING = "track";
    private final static String FILLED = "filled";
    private final static String CAR = "car";
    private final static String ROUTE = "route";
    private final static String NEUTRAL_COLOR = "NEUTRAL";

    private MapViewCreator() {
    }

    /**
     * this method create the view of the map
     *
     * @param gameState    (ObservableGameState) : the observer of the gameState
     * @param claimRouteHP (ObjectProperty< ActionHandlers.ClaimRouteHandler >) : the  handler of the route
     * @param cardChooser  (CardChooser) : the chooser of the cards
     * @return (Node) : The View of the map
     */
    public static Node createMapView(ObservableGameState gameState, ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHP, CardChooser cardChooser) {
        Pane mapView = new Pane();
        mapView.getStylesheets().addAll(MAP_CLASS, COLOR_CLASS);
        mapView.getChildren().add(new ImageView(MAP_NAME));


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

            routeGroup.getStyleClass().addAll(ROUTE,
                    route.level().name(),
                    route.color() == null ? NEUTRAL_COLOR : route.color().name());

            gameState.routeOwner(route).addListener((o, oV, nV) -> routeGroup.getStyleClass().add(nV.name()));

            for (int i = 1; i <= route.length(); i++) {
                Rectangle wayRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);

                wayRectangle.getStyleClass().addAll(TRACK_STRING, FILLED);

                Rectangle carRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                route.isHighlighted().addListener((o,oV,nV)->{
                    if(nV) {
                        carRectangle.getStyleClass().add("longest");
                    }
                });
                carRectangle.getStyleClass().add(FILLED);
                int circle1XPos =RECTANGLE_WIDTH/2 + SPACE_BETWEEN_CIRCLE;
                int circle2XPos =RECTANGLE_WIDTH/2 - SPACE_BETWEEN_CIRCLE;
                int circleYPos = RECTANGLE_HEIGHT/2;
                Group carGroup = new Group(carRectangle, new Circle(circle1XPos,circleYPos , CIRCLE_RADIUS), new Circle(circle2XPos,circleYPos, CIRCLE_RADIUS));
                carGroup.getStyleClass().add(CAR);

                Group caseGroup = new Group(wayRectangle, carGroup);

                caseGroup.setId(route.id() + "_" + i);

                routeGroup.getChildren().add(caseGroup);
            }

            mapView.getChildren().add(routeGroup);
        }


        return mapView;

    }

    /**
     * This interface is used by the player to choose a card.
     */
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

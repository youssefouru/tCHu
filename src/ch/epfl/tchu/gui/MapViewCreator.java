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

public final class MapViewCreator {
    private final static int circleRadius = 3;
    private final static int rectangleWidth = 36;
    private final static int rectangleHeight = 12;

    /**
     * this method
     *
     * @param gameState    (ObservableGameState) :
     * @param claimRouteHP (ObjectProperty< ActionHandlers.ClaimRouteHandler >) :
     * @param cardChooser  (CardChooser) :
     * @return (Node) :
     */
    public static Node createMapView(ObservableGameState gameState, ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHP, CardChooser cardChooser) {
        Pane mapView = new Pane();
        mapView.getStylesheets().addAll("map.css", "colors.css");
        ImageView imageView = new ImageView("map.png");
        mapView.getChildren().add(imageView);


        for (Route route : ChMap.routes()) {
            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            gameState.routeOwner(route).addListener((o, oV, nV) -> routeGroup.getStyleClass().add(nV.name()));
            routeGroup.disableProperty().bind(claimRouteHP.isNull().or(gameState.claimable(route).not()));
            routeGroup.setOnMouseClicked((e) -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);
                ActionHandlers.ClaimRouteHandler claimRouteH = claimRouteHP.get();
                ActionHandlers.ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                cardChooser.chooseCards(possibleClaimCards, chooseCardsH);

            });

            routeGroup.getStyleClass().addAll("route",
                    route.level().name(),
                    route.color() == null ? "NEUTRAL" : route.color().name());


            for (int i = 1; i <= route.length(); i++) {
                Group caseGroup = new Group();
                caseGroup.setId(route.id() + "_" + i);
                Rectangle wayRectangle = new Rectangle(rectangleWidth, rectangleHeight);
                wayRectangle.getStyleClass().addAll("track", "filled");
                Group carGroup = new Group();
                carGroup.getStyleClass().add("car");
                Rectangle carRectangle = new Rectangle(rectangleWidth, rectangleHeight);
                carRectangle.getStyleClass().add("filled");
                carGroup.getChildren().addAll(carRectangle,new Circle(12, 6, circleRadius) ,new Circle(24, 6, circleRadius));
                caseGroup.getChildren().addAll(wayRectangle, carGroup);
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

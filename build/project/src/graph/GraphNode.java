package graph;
import java.util.ArrayList;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GraphNode extends StackPane {

    private Circle circle;
    private Label text;

    private ArrayList<GraphNode> connectedNodesList = new ArrayList<>();
    private ArrayList<Line> edgesList = new ArrayList<>();
    private ArrayList<Label> edgesLabelList = new ArrayList<>();

    private double radius = 30;

    public GraphNode(String name, double xPos, double yPos, Color color) {

        circle = new Circle(radius, color);
        text = new Label(name);
        text.setTextFill(Color.WHITE);

        setLayoutX(xPos);
        setLayoutY(yPos);

        getChildren().addAll(circle, text);
        layout();
    }

    public void addNeighbor(GraphNode node) {
        connectedNodesList.add(node);
    }

    public void addEdge(Line edgeLine, Label edgeLabel) {
        edgesList.add(edgeLine);
        edgesLabelList.add(edgeLabel);
        edgeLabel.layoutXProperty().bind(new DoubleBinding() {
            {
                bind(translateXProperty());
                bind(connectedNodesList.get(connectedNodesList.size() - 1).translateXProperty());
            }

            @Override
            protected double computeValue() {

                // We find the center of the line to translate the text
                double width = edgeLine.getEndX() - edgeLine.getStartX();

                return edgeLine.getStartX() + width / 2.0;
            }
        });

        edgeLabel.layoutYProperty().bind(new DoubleBinding() {
            {
                bind(translateYProperty());
                bind(connectedNodesList.get(connectedNodesList.size() - 1).translateYProperty());
            }

            @Override
            protected double computeValue() {

                double width = edgeLine.getEndY() - edgeLine.getStartY();
                return edgeLine.getStartY() + width / 2.0;
            }
        });

    }
    
    public Label getLabel () { return text; }
    
    public Circle getCircle () { return circle; }

    public ArrayList<GraphNode> getConnectedNodes() {
        return connectedNodesList;
    }

    public ArrayList<Line> getEdges() {
        return edgesList;
    }

    public double getX() {
        return getLayoutX() + getTranslateX();
    }

    public double getY() {
        return getLayoutY() + getTranslateY();
    }

    public double getCenterX() {
        return getX() + radius;
    }

    public double getCenterY() {
        return getY() + radius;
    }

}
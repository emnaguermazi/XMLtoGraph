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
    private Label label;
    private double radius = 30;
    private ArrayList<GraphNode> neighbors = new ArrayList<>();
    private ArrayList<Line> incidentEdges = new ArrayList<>();

    public GraphNode(String name, double xPos, double yPos, Color color) {
        circle = new Circle(radius, color);
        label = new Label(name);
        label.setTextFill(Color.WHITE);
        setLayoutX(xPos);
        setLayoutY(yPos);
        getChildren().addAll(circle, label);
        layout();
    }

    public void addNeighbor(GraphNode node) { neighbors.add(node); }

    public void addEdge(Line edgeLine, Label edgeLabel) {
        incidentEdges.add(edgeLine);
        edgeLabel.layoutXProperty().bind(new DoubleBinding() {
            {
                bind(translateXProperty());
                bind(neighbors.get(neighbors.size() - 1).translateXProperty());
            }

            @Override
            protected double computeValue() {
                double width = edgeLine.getEndX() - edgeLine.getStartX();
                return edgeLine.getStartX() + width / 2.0;
            }
        });

        edgeLabel.layoutYProperty().bind(new DoubleBinding() {
            {
                bind(translateYProperty());
                bind(neighbors.get(neighbors.size() - 1).translateYProperty());
            }

            @Override
            protected double computeValue() {

                double width = edgeLine.getEndY() - edgeLine.getStartY();
                return edgeLine.getStartY() + width / 2.0;
            }
        });

    }
    
    public Label getLabel () { return label; }
    
    public Circle getCircle () { return circle; }

    public ArrayList<GraphNode> getNeighbors() { return neighbors; }

    public ArrayList<Line> getIncidentEdges() { return incidentEdges; }

    public double getX() { return getLayoutX() + getTranslateX(); }

    public double getY() { return getLayoutY() + getTranslateY(); }

    public double getCenterX() { return getX() + radius; }

    public double getCenterY() { return getY() + radius; }

}
package graph;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

    private static double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY;
    private static Group root = new Group();
    private static File fileBrowse = null;
    private static TextArea textArea;
    
    public static void createGraph(Element fileRoot) {
		String ns = fileRoot.getNamespace().getURI();	
		Element graph = fileRoot.getChild("graph", Namespace.getNamespace(ns));
		List<Element> nodes = graph.getChildren("node", Namespace.getNamespace(ns));
		List<Element> edges = graph.getChildren("edge", Namespace.getNamespace(ns));
		for (Element e : edges) {
			Element edgeLabel = e.getChild("data", Namespace.getNamespace(ns));
			String sourceLabel = e.getAttribute("source").getValue();
			String targetLabel = e.getAttribute("target").getValue();
			GraphNode source = create(nodes, ns, sourceLabel);
			GraphNode target = create(nodes, ns, targetLabel);
			if (edgeLabel == null) {
				connectNodes(source, target,"");
			} else {
				connectNodes(source, target, edgeLabel.getValue());
			}
		}
	}
    
    private static GraphNode create(List<Element> nodes, String ns, String label) {
    	GraphNode graphNode = null;
    	double randomPos = new Random().nextInt((300 - 100) + 1) + 100;
    	if (!created(label)) {
			for (Element n : nodes) {
				String nodeLabel = n.getAttribute("id").getValue();
				if (label.equals(nodeLabel)) {
					Element nodeColor = n.getChild("data", Namespace.getNamespace(ns));
					if (nodeColor == null) {
						graphNode = createNode(label,randomPos, randomPos,Color.web("black"));
					} else {
						graphNode = createNode(label,randomPos, randomPos,Color.web(nodeColor.getValue()));
					}
				}
			}
		} else {
			graphNode = getNode(label);
		}
    	return graphNode;
    }
    
    public static boolean created(String label) {
    	boolean found = false;
    	for (Node e : root.getChildren()) {
    		if (e.getClass() == GraphNode.class) {
    			GraphNode n = (GraphNode) e;
    			if (n.getLabel().getText().equals(label)) {
    				found = true;
    				break;
    			}
    		}
    	}
    	return found;
    }
    
    private static GraphNode getNode(String label) {
    	GraphNode found = null;
    	for (Node e : root.getChildren()) {
    		if (e.getClass() == GraphNode.class) {
    			GraphNode n = (GraphNode) e;
    			if (n.getLabel().getText().equals(label)) {
    				found = n;
    			}
    		}
    	}
    	return found;
    }
    
    private static void connectNodes(GraphNode node1, GraphNode node2, String edgeText) {

        Line edgeLine = new Line(node1.getCenterX(), node1.getCenterY(), node2.getCenterX(), node2.getCenterY());
        Label edgeLabel = new Label(edgeText);

        node1.addNeighbor(node2);
        node2.addNeighbor(node1);

        node1.addEdge(edgeLine, edgeLabel);
        node2.addEdge(edgeLine, edgeLabel);

        root.getChildren().addAll(edgeLine, edgeLabel);

    }

    private static GraphNode createNode(String nodeName, double xPos, double yPos, Color color) {
        GraphNode node = new GraphNode(nodeName, xPos, yPos, color);
        node.setOnMousePressed(circleOnMousePressedEventHandler);
        node.setOnMouseDragged(circleOnMouseDraggedEventHandler);
        
        root.getChildren().add(node);

        return node;
    }

    static EventHandler<MouseEvent> circleOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();

            GraphNode node = (GraphNode) t.getSource();

            orgTranslateX = node.getTranslateX();
            orgTranslateY = node.getTranslateY();
        }
    };

    static EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            GraphNode node = (GraphNode) t.getSource();

            node.setTranslateX(newTranslateX);
            node.setTranslateY(newTranslateY);

            updateLocations(node);
        }
    };

    private static void updateLocations(GraphNode node) {

        ArrayList<GraphNode> neighbors = node.getNeighbors();

        ArrayList<Line> incidentEdges = node.getIncidentEdges();

        for (int i = 0; i < neighbors.size(); i++) {
            GraphNode neighbor = neighbors.get(i);
            Line line = incidentEdges.get(i);
            line.setStartX(node.getCenterX());
            line.setStartY(node.getCenterY());
            line.setEndX(neighbor.getCenterX());
            line.setEndY(neighbor.getCenterY());
        }
    }
    
    public static void error(String msg) {
        Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setHeaderText(msg);
		errorAlert.show();
    }
    
    private static File getFile() {
    	File file = null;
    	if (fileBrowse != null && fileBrowse.getAbsolutePath().equals(textArea.getText())) {
			file = fileBrowse;
    	} else if (!textArea.getText().isEmpty()) {
    		file = new File(textArea.getText());
    	}
    	return file;
    }
    
    public void start(Stage browseStage) throws Exception {
    	 
        final FileChooser fileChooser = new FileChooser();
 
        textArea = new TextArea();
        textArea.setMinHeight(20);

        Button chooseButton = new Button("Browse File");
        chooseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.clear();
                fileBrowse = fileChooser.showOpenDialog(browseStage);
                if (fileBrowse != null) {
                    textArea.appendText(fileBrowse.getAbsolutePath());
                }
                
            }
        });

        Button startButton = new Button("Generate Graph");
        startButton.setOnAction(new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent event) {
				File file = getFile();
				if (file != null) {
					if (!file.exists()) {
		    			error("This file does not exist.");
		    		} else {
		    			try {
							SAXBuilder builder = new SAXBuilder();
							Document xml = builder.build(file);
							Element fileRoot = xml.getRootElement();
			    			String ns = fileRoot.getNamespace().getURI();	
			    			Element graph = fileRoot.getChild("graph", Namespace.getNamespace(ns));
			    			if (graph == null || !fileRoot.getName().equals("graphml")) {
			   				 	error("This is not a GraphML file.");
			    			} else {
			    				createGraph(fileRoot);
			                    Button saveButton = new Button("Save as Image");
			                    saveButton.setLayoutX(250);
			                    saveButton.setOnAction(new EventHandler<ActionEvent>() {
			                    	@Override
			                        public void handle(ActionEvent event) {
			                    		root.getChildren().remove(saveButton);
			                    		FileChooser fileChooser = new FileChooser();
			                            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
			                            File file = fileChooser.showSaveDialog(null);
			                            if(file != null){
			                                try {
			                                	WritableImage image = root.snapshot(new SnapshotParameters(), null);
			                                	ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			                                } catch (IOException ex) {
			                                	ex.printStackTrace();
			                                }
			                            }
			                    	}
			                    });
			                    browseStage.close();
								Stage startStage = new Stage();
					            startStage.setTitle("Graph Editor");
					    		Scene scene = new Scene(root, 600, 400);
			                    root.getChildren().add(saveButton);
			                    startStage.setScene(scene);
			                    startStage.show();
			    			}
						} catch (JDOMException e) {
							error("This is not an XML file.");
						} catch (IOException e) {
							e.printStackTrace();
						}
		    		}
				} else {
					error("Browse a file or type a file absolute path first.");
				}
				
            }
        });
        
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);
 
        root.getChildren().addAll(textArea, chooseButton, startButton);
 
        Scene scene = new Scene(root, 600, 120);
 
        browseStage.setTitle("File Chooser");
        browseStage.setScene(scene);
        browseStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
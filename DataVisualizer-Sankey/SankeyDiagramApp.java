/*
 * This is the main application class for the Sankey Diagram visualizer.
 * It uses JavaFX to render the diagram based on data read from a file.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import java.util.Map;

public class SankeyDiagramApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<String, Double> categoryData;
        Map<String, Double> sourceData;

        try {
            // Use the refactored data reader class
            SankeyDataReader reader = new SankeyDataReader("example2.txt");
            categoryData = reader.parseCategoryData();
            sourceData = reader.calculateSourceData();

        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Failed to process the data file.");
            errorAlert.setContentText("Please check the file format and content.\nDetails: " + e.getMessage());
            errorAlert.getButtonTypes().setAll(ButtonType.OK);
            errorAlert.showAndWait();
            return; // Exit if data loading fails
        }

        SankeyPane sankeyPane = new SankeyPane();
        sankeyPane.setUserData(new SankeyData(categoryData, sourceData));

        Scene scene = new Scene(sankeyPane, 800, 600);

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.setTitle("Sankey Diagram Visualizer"); // A professional title
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}


/**
 * A simple data holder class to pass data to the SankeyPane.
 */
class SankeyData {
    public final Map<String, Double> categoryData;
    public final Map<String, Double> sourceData;

    SankeyData(Map<String, Double> categoryData, Map<String, Double> sourceData) {
        this.categoryData = categoryData;
        this.sourceData = sourceData;
    }
}


/**
 * The custom Pane that handles all the drawing logic for the Sankey diagram.
 */
class SankeyPane extends Pane {

    /**
     * This method is automatically called by JavaFX when the pane's size changes.
     * It's the ideal place to trigger a redraw.
     */
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        drawDiagram();
    }
    

    /**
     * The main drawing method. It clears the canvas and redraws the entire diagram
     * based on the current pane size and data.
     */
    private void drawDiagram() {
        getChildren().clear();

        SankeyData data = (SankeyData) getUserData();
        if (data == null || data.sourceData.isEmpty() || data.categoryData.isEmpty()) {
            return; // Don't draw if there's no data
        }

        double paneHeight = getHeight();
        double paneWidth = getWidth();

        // --- Layout Constants ---
        final double DRAWABLE_HEIGHT_RATIO = 0.80; // Use 80% of pane height for the diagram
        final double NODE_WIDTH_RATIO = 0.015;
        final double SOURCE_NODE_X_RATIO = 0.20;
        final double TARGET_NODES_X_RATIO = 0.75;
        final double LABEL_FONT_SIZE = 16;
        final double VALUE_SCALE_FACTOR = 0.1; // Scales data values to pixel heights

        // --- Data Extraction ---
        String sourceLabel = data.sourceData.keySet().iterator().next();
        double sourceValue = data.sourceData.get(sourceLabel);
        int categoryCount = data.categoryData.size();

        // --- Dynamic Calculations ---
        double drawableHeight = paneHeight * DRAWABLE_HEIGHT_RATIO;
        double totalScaledHeight = sourceValue * VALUE_SCALE_FACTOR;
        double gapBetweenNodes = (drawableHeight - totalScaledHeight) / Math.max(1, categoryCount - 1);
        
        double nodeWidth = paneWidth * NODE_WIDTH_RATIO;
        double sourceNodeX = paneWidth * SOURCE_NODE_X_RATIO;
        double targetNodesX = paneWidth * TARGET_NODES_X_RATIO;
        
        double sourceNodeY = (paneHeight / 2.0) - (totalScaledHeight / 2.0);
        double sourceNodeHeight = totalScaledHeight;
        
        // Initial Y position for the first target node
        double targetNodesInitialY = (paneHeight / 2.0) - (drawableHeight / 2.0);

        // --- 1. Draw Source Node (Left Rectangle) ---
        Rectangle sourceNodeRect = new Rectangle(sourceNodeX, sourceNodeY, nodeWidth, sourceNodeHeight);
        Color sourceColor = Color.rgb(100, 150, 250);
        sourceNodeRect.setFill(sourceColor);
        sourceNodeRect.setStroke(sourceColor);
        getChildren().add(sourceNodeRect);

        // --- 2. Draw Target Nodes and Labels (Right Rectangles) ---
        int nodeIndex = 0;
        double currentTargetY = targetNodesInitialY;
        for (Map.Entry<String, Double> entry : data.categoryData.entrySet()) {
            String categoryName = entry.getKey();
            double categoryValue = entry.getValue();
            double nodeHeight = categoryValue * VALUE_SCALE_FACTOR;

            Rectangle targetNodeRect = new Rectangle(targetNodesX, currentTargetY, nodeWidth, nodeHeight);
            
            // Generate a distinct color for each category
            double hue = (double) nodeIndex / categoryCount * 360;
            Color targetColor = Color.hsb(hue, 0.6, 0.9);
            targetNodeRect.setFill(targetColor);
            targetNodeRect.setStroke(targetColor);
            getChildren().add(targetNodeRect);

            // Add label for the category
            String labelText = categoryName + ": " + categoryValue;
            Text categoryLabel = new Text(targetNodesX + nodeWidth * 2, currentTargetY + nodeHeight / 2 + 5, labelText);
            categoryLabel.setFont(Font.font("Calibri", FontPosture.REGULAR, LABEL_FONT_SIZE));
            getChildren().add(categoryLabel);

            currentTargetY += nodeHeight + gapBetweenNodes;
            nodeIndex++;
        }

        // --- 3. Draw Flows (Connecting Paths) ---
        double sourceFlowStartY = sourceNodeY;
        double targetFlowStartY = targetNodesInitialY;
        nodeIndex = 0;
        for (double categoryValue : data.categoryData.values()) {
            double flowHeight = categoryValue * VALUE_SCALE_FACTOR;

            Path flowPath = new Path();
            MoveTo moveTo = new MoveTo(sourceNodeX + nodeWidth, sourceFlowStartY);
            
            double controlX1 = sourceNodeX + paneWidth * 0.2;
            double controlX2 = targetNodesX - paneWidth * 0.2;

            CubicCurveTo curveUp = new CubicCurveTo(controlX1, sourceFlowStartY, controlX2, targetFlowStartY, targetNodesX, targetFlowStartY);
            LineTo lineDown = new LineTo(targetNodesX, targetFlowStartY + flowHeight);
            CubicCurveTo curveDown = new CubicCurveTo(controlX2, targetFlowStartY + flowHeight, controlX1, sourceFlowStartY + flowHeight, sourceNodeX + nodeWidth, sourceFlowStartY + flowHeight);
            
            flowPath.getElements().addAll(moveTo, curveUp, lineDown, curveDown);
            
            // Create a gradient fill for the flow
            double hue = (double) nodeIndex / categoryCount * 360;
            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, sourceColor.deriveColor(1, 1, 1, 0.7)),
                new Stop(1.0, Color.hsb(hue, 0.6, 0.9, 0.7))
            );
            flowPath.setFill(gradient);
            flowPath.setStrokeWidth(0); // No border for the flow
            getChildren().add(flowPath);

            // Update Y positions for the next flow
            sourceFlowStartY += flowHeight;
            targetFlowStartY += flowHeight + gapBetweenNodes;
            nodeIndex++;
        }
        
        // --- 4. Draw Source Label ---
        // Drawn last to appear on top of flows if they overlap
        String sourceLabelText = sourceLabel + ": " + sourceValue;
        Text sourceLabelNode = new Text(sourceNodeX - paneWidth * 0.15, sourceNodeY + sourceNodeHeight / 2 + 5, sourceLabelText);
        sourceLabelNode.setFont(Font.font("Calibri", FontPosture.REGULAR, LABEL_FONT_SIZE));
        getChildren().add(sourceLabelNode);
    }
}
/*
The file CW3_2252534_SankeyDiagrams.java implements the function of visualizing data by drawing Sankey Diagrams.
The specific functions of the specific code can be found in the comments.
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

import java.util.HashMap;
import java.util.LinkedHashMap;

public class CW3_2252534_SankeyDiagrams extends Application {

    public static void main(String[] args) {
        //Launch JavaFX application
        //Note: If this sentence is deleted, the application will not start properly
        launch(args);
    }

    @Override  //Override the start method in the Application class
    public void start(Stage primaryStage) throws Exception {
        //Created a scene to display error messages
        LinkedHashMap<String, Double> difData = null;
        HashMap<String, Double> totalValue = null;
        try {
            //Using the method in Read_File_Data to read file data
            Read_File_Date myFile = new Read_File_Date("example2.txt");
            //Note: HashMap does not guarantee the order of elements, it stores and accesses elements based on the hash value of the key
            //Note: LinkedHashMap stores and accesses elements in the order they are inserted, maintaining the order in which they are inserted
            //So we chose LinkedHashMap, which can match the data order in the file
            difData = new LinkedHashMap<>(myFile.GetDifferentData());
            totalValue = new HashMap<>(myFile.CalculateTotal());
        } catch (Exception e) {  //This method may throw any type of exception
            //Create an error type pop-up window to display error messages indicating file processing failures
            Alert myAlert = new Alert(Alert.AlertType.ERROR);
            myAlert.setTitle("Error");
            myAlert.setHeaderText("Processing file failed");
            myAlert.setContentText("Please try again!\n" + e.getMessage());
            myAlert.getButtonTypes().setAll(ButtonType.OK);
            //Display pop-ups and wait for users to respond to them
            myAlert.showAndWait();
            stop();
        }
        //Create a scene to display Sankey Diagrams
        SankeyPane myPane = new SankeyPane();
        //Set user data for nodes
        myPane.setUserData(new SankeyPaneData(difData, totalValue));
        //Define the initial size of the scene
        Scene myScene = new Scene(myPane, 700, 500);
        //Limited the minimum size of the stage
        //Note: If the stage is too small, it will affect the display effect of Sankey Diagrams
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        //Set the stage title
        primaryStage.setTitle("CW3_2252534_SankeyDiagrams");
        //Place the scene in the stage
        primaryStage.setScene(myScene);
        //Display the stage
        primaryStage.show();
    }
}

class SankeyPaneData {

    //Using Map to store key value pairs of data
    public HashMap<String, Double> difData;
    public HashMap<String, Double> totalValue;

    //Create a constructor
    SankeyPaneData(HashMap<String, Double> difData, HashMap<String, Double> totalValue) {
        this.difData = difData;
        this.totalValue = totalValue;
    }
}

class SankeyPane extends Pane {

    @Override
    public void setHeight(double length) {
        //set length
        //Calling the SankeyPaint() method to redraw the interface
        super.setHeight(length);
        SankeyPaint();
    }

    @Override
    public void setWidth(double width) {
        //Set width
        //Calling the SankeyPaint() method to redraw the interface
        super.setWidth(width);
        SankeyPaint();
    }

    private void SankeyPaint() {

        double length = getHeight();
        double width = getWidth();
        //Empty all child nodes in the current container
        getChildren().clear();
        //Obtain data from the Sankey Diagrams
        SankeyPaneData sankeyData = (SankeyPaneData) getUserData();
        //Obtain the number of nodes in the data
        int numNode = sankeyData.difData.keySet().size();
        //Initialize the relevant lengths of the title, rectangular box, start node, and end node
        double totalLength = length * 0.75;
        //title
        String title = "";
        double titleLength = 0;
        //rectangular box
        double recLength = 0;
        double recWidth = width * 0.0125;
        //start node
        double startLength = 0;
        double startWidth = (width * 0.25) - recWidth;
        //end node
        double endLength = 0;
        double endWidth = width * 0.625;

        //Draw the left part of the Sankey Diagram
        for(String str : sankeyData.totalValue.keySet()) {  //Enhanced for loops
            //Set the title of the left rectangle
            title = str;
            titleLength = sankeyData.totalValue.get(str);
            //Set the relevant numerical values for the left rectangle
            recLength = ((totalLength - titleLength * 0.1) / numNode);
            startLength = (length * 0.5) - (titleLength * 0.05);
            endLength = length * 0.5 - (titleLength * 0.1 + recLength * (numNode - 1)) * 0.5;
            //Create a rectangular object on the left
            Rectangle recLeft = new Rectangle(startWidth, startLength, recWidth * 2, titleLength * 0.1);
            //Set the color of the left rectangle
            //Note: In the Color.rgb() method, the last four digits represent the values of red (R), green (G), blue (B), and opacity (Opacity), respectively
            Color myColor1 = Color.rgb(100, 150, 250, 1);
            //Set the fill color of the rectangle
            recLeft.setFill(myColor1);
            //Set the border color of the rectangle
            recLeft.setStroke(myColor1);
            getChildren().add(recLeft);
        }

        //Draw the right part of the Sankey Diagram
        //There are many rectangles on the right representing different data
        //Different colors, lengths, and text are needed to distinguish
        int node = 1;  //Used to record the current number of data
        double tempLength = endLength;
        for(String str : sankeyData.difData.keySet()) {  //Enhanced for loops
            //Create a rectangular object on the right
            Rectangle recRight = new Rectangle(endWidth, tempLength, recWidth * 2, sankeyData.difData.get(str) * 0.1);
            //Set the text next to the right rectangular
            double myText1X = endWidth + recWidth + (endWidth - startWidth) * 0.05;
            double myText1Y = tempLength + sankeyData.difData.get(str) * 0.05 + 5;
            String myText1Con = str + ": " + sankeyData.difData.get(str);
            Text myText1 = new Text(myText1X, myText1Y, myText1Con);
            myText1.setFont(Font.font("Calibri", FontPosture.ITALIC, 18));
            getChildren().add(myText1);
            //Set the relevant numerical values for the right rectangle
            double lengthRight = sankeyData.difData.get(str) * 0.1 + recLength;
            tempLength = tempLength + lengthRight;  //Adjust length
            //Set the color of the left rectangle
            //The last three numbers represent the hue, saturation, and brightness of the color, respectively
            double hue = (double) node / numNode * 360;
            Color myColor2 = Color.hsb(hue, 0.5, 0.8);
            recRight.setFill(myColor2);  //fill color
            recRight.setStroke(myColor2);  //border color
            getChildren().add(recRight);
            node++;  //Indicates processing the next data
        }

        //Draw the curve connecting the left and right parts in the Sankey Diagram
        //Note: The following variables cannot be placed in a for loop
        double startPathLength = startLength;
        double endPathLength = endLength;
        double throughCurveWidthUp = startWidth + width * 0.125 + recWidth * 2;
        double throughCurveWidthLow = endWidth - width * 0.125;
        node = 1;  //Used to record the current number of data
        for(String str : sankeyData.difData.keySet()) {//Enhanced for loops
            double subRecLength = sankeyData.difData.get(str) * 0.1;
            ////Create a Path object
            Path myPath = new Path();
            //Draw starting point
            //The MoveTo class represents the starting point of a path, which is used to specify the starting position of the path
            MoveTo moveTo = new MoveTo(startWidth + recWidth * 2, startPathLength);
            //Draw the starting position of the line segment
            //The LineTo class represents a straight segment in a path, which is used to specify the endpoint position of the segment in the path
            LineTo lineToStart = new LineTo(startWidth + recWidth * 2, startLength);
            //Draw the endpoint position of the line segment
            //The LineTo class represents a straight segment in a path, which is used to specify the endpoint position of the segment in the path
            LineTo lineToEnd = new LineTo(endWidth, endPathLength + subRecLength);
            //Draw the upper curve in the curve path
            //The CubicCurveTo class represents cubic-Bezier curve segments in a path, which are used to specify the control points and endpoint positions of the curve segments in the path
            CubicCurveTo curveUp = new CubicCurveTo(throughCurveWidthUp, startPathLength, throughCurveWidthLow, endPathLength, endWidth, endPathLength);
            //Draw the lower curve in the curve path
            //The CubicCurveTo class represents cubic-Bezier curve segments in a path, which are used to specify the control points and endpoint positions of the curve segments in the path
            CubicCurveTo curveLow = new CubicCurveTo(throughCurveWidthLow, endPathLength + subRecLength, throughCurveWidthUp, startPathLength + subRecLength, startWidth + recWidth * 2, startPathLength + subRecLength);
            //Update length
            endPathLength = endPathLength + subRecLength + recLength;
            startPathLength = startPathLength + subRecLength;
            //Add the drawn part above to the path
            //Pay attention to the order
            myPath.getElements().add(moveTo);
            myPath.getElements().add(curveUp);
            myPath.getElements().add(lineToEnd);
            myPath.getElements().add(curveLow);
            myPath.getElements().add(lineToStart);
            //Set the color of the path
            //Gradient effect
            //The LinearGradient class can achieve gradient effects by specifying the coordinates of the starting and ending points, as well as the starting and ending colors of the gradient
            double hue = (double) node / numNode * 360;
            LinearGradient linearGradient = new LinearGradient(0, 0, 1, 1,
                    true, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.rgb(100, 150, 250, 1)),
                    new Stop(1.0, Color.hsb(hue, 0.5, 0.8)));
            myPath.setFill(linearGradient);
            myPath.setStroke(linearGradient);
            //Control the transparency of nodes to achieve gradient effects
            myPath.setOpacity(0.8);
            getChildren().add(myPath);
            node++;  //Indicates processing the next data
        }

        //Set the text next to the left rectangular
        //Note: This requires final setting so that the text can appear above the Sankey Diagram for easy viewing
        double myText2X = width * 0.25 + (endWidth - startWidth) * 0.05;
        double myText2Y = length * 0.5 - 10;
        String myText2Con = title + ": " + titleLength;
        Text myText2 = new Text(myText2X, myText2Y, myText2Con);
        myText2.setFont(Font.font("Calibri", FontPosture.ITALIC, 18));
        getChildren().add(myText2);
    }
}
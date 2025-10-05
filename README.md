# Java Sankey Diagram Visualizer

![Java](https://img.shields.io/badge/Language-Java-orange.svg)
![JavaFX](https://img.shields.io/badge/Framework-JavaFX-blue.svg)

A simple yet powerful data visualization tool built from scratch in Java and JavaFX. This application reads data from plain text files and generates interactive, aesthetically pleasing Sankey diagrams to represent data flow.

The entire project is self-contained and does not rely on any third-party charting or graphics libraries.

### Demo

Here is a sample visualization generated from `example2.txt`:

![Sankey Diagram Demo](./docs/sankey-demo.png)

---

## Features

This visualizer comes with a range of features designed for clarity, usability, and aesthetics.

*   **Dynamic Rendering from Text Files**: The application reads simple `.txt` files to generate diagrams. This makes it incredibly easy to visualize different datasets without changing any code.
*   **Responsive and Resizable Display**: The diagram intelligently redraws itself to fit the window size. The layout and proportions are automatically recalculated when the window is resized, ensuring the visualization remains clear and legible. A minimum window size is enforced to prevent the diagram from becoming unreadable.
*   **Robust Error Handling**: If the data file is missing, improperly formatted (e.g., non-numeric or negative values), or is a directory instead of a file, the application will display a user-friendly error pop-up instead of crashing.
*   **Aesthetic Gradient Flows**: The flows (the paths connecting the source and target nodes) are rendered with smooth color gradients, transitioning from the source node's color to the corresponding target node's color. This enhances the visual appeal and makes the diagram easier to follow.

---

## Project Structure

The project is organized into two main Java classes, along with supporting data files.

```
DataVisualizer-Sankey/
│
├── SankeyDataReader.java     # Handles all file reading and data parsing logic.
├── SankeyDiagramApp.java     # The main JavaFX application, handles UI and rendering.
│
├── example1.txt              # Sample dataset 1 (Business metrics)
├── example2.txt              # Sample dataset 2 (Budget plan)
├── example3.txt              # Sample dataset 3 (Exam scores)
├── example4.txt              # Sample dataset 4 (Yang Hui Triangle)
│
└── README.md                 # This file.
```

---

## How to Run

To compile and run this project, you need to have the Java Development Kit (JDK) and the JavaFX SDK installed and configured on your system.

**1. Compile the Code**

Open a terminal or command prompt in the project's root directory and run the following command. Make sure to replace `"path/to/javafx-sdk/lib"` with the actual path to your JavaFX library.

```bash
# On Windows
javac --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml *.java

# On macOS/Linux
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml *.java
```

**2. Run the Application**

After successful compilation, run the main application class with the following command:

```bash
# On Windows
java --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml SankeyDiagramApp

# On macOS/Linux
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml SankeyDiagramApp
```

The application will launch and display the visualization for `example2.txt` by default. To change the input file, modify the file path string in `SankeyDiagramApp.java`.

---

## Data File Format

The application uses a simple and specific format for its `.txt` data files.

*   **Line 1**: The title of the diagram (Not directly used in the current visual, but parsed).
*   **Line 2**: The label for the source node (the left-side block).
*   **Line 3 onwards**: Each subsequent line represents a target category and its value, separated by a space. The category name can contain spaces. The value must be the last element on the line.

**Example (`example1.txt`):**

```
Year 1
Resources
Invest 4300
Sales 2400
Profit 2200
```

*   `Resources` will be the source node on the left, with a total value of `8900`.
*   `Invest`, `Sales`, and `Profit` will be the target nodes on the right.

---

## Code Overview

The project's logic is cleanly separated into two main classes.

### `SankeyDataReader.java`

This class is solely responsible for handling file I/O and data validation.
*   It opens and reads the specified `.txt` file.
*   It validates the file's existence and format, throwing specific exceptions for errors like `FileNotFoundException`, `NumberFormatException` (for invalid or non-positive numbers), and `RuntimeException` (if a directory is provided).
*   It parses the data into two main data structures: one for the source node and one for the target categories, preserving the order of the categories as they appear in the file using a `LinkedHashMap`.

### `SankeyDiagramApp.java`

This is the main entry point of the application and handles all aspects of the user interface and visualization.
*   It initializes the JavaFX window (the `Stage`).
*   It creates an instance of `SankeyDataReader` to load the data. If any exceptions are caught during this process, it displays an error alert.
*   It contains the inner `SankeyPane` class, which is a custom `Pane` component responsible for all rendering logic.
    *   The `drawDiagram()` method calculates all coordinates, dimensions, and colors based on the loaded data and the current window size.
    *   It draws the source and target nodes as rectangles.
    *   It draws the flows between nodes using `CubicCurveTo` for smooth, curved paths.
    *   This method is called automatically whenever the window is resized, making the diagram responsive.

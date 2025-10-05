/*
 * This class is responsible for reading and parsing data from a text file
 * formatted for a Sankey diagram. It extracts the source node data and
 * the target category data.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class SankeyDataReader {

    private final File file;


    /**
     * Constructor to initialize the File object and perform initial checks.
     * @param filePath The path to the data file.
     * @throws FileNotFoundException If the file does not exist or is a directory.
     */
    public SankeyDataReader(String filePath) throws FileNotFoundException {
        this.file = new File(filePath);
        if (!this.file.exists()) {
            throw new FileNotFoundException("The input file " + filePath + " does not exist!");
        }
        if (!this.file.isFile()) {
            throw new RuntimeException("The input " + filePath + " is not a file!");
        }
    }


    /**
     * Parses the data for the target categories (the right side of the diagram).
     * It maintains the order of categories as they appear in the file.
     * @return A Map where keys are category names and values are their amounts.
     * @throws FileNotFoundException If the file cannot be read.
     */
    public Map<String, Double> parseCategoryData() throws FileNotFoundException {
        // Using LinkedHashMap to preserve the insertion order from the file.
        Map<String, Double> categoryData = new LinkedHashMap<>();
        try (Scanner scanner = new Scanner(file)) { // Use try-with-resources for automatic closing
            // Skip the first two lines (diagram title and source label)
            if (scanner.hasNextLine()) scanner.nextLine();
            if (scanner.hasNextLine()) scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue; // Skip empty lines

                int lastSpaceIndex = line.lastIndexOf(' ');
                if (lastSpaceIndex == -1) {
                    throw new NumberFormatException("Invalid data format in line: \"" + line + "\"");
                }

                String name = line.substring(0, lastSpaceIndex);
                double amount;
                try {
                    amount = Double.parseDouble(line.substring(lastSpaceIndex + 1));
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Invalid number format in line: \"" + line + "\"");
                }


                if (amount <= 0) {
                    throw new NumberFormatException("Data values must be positive. Error in line: \"" + line + "\"");
                }
                categoryData.put(name, amount);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("The input file " + file.getName() + " could not be read!");
        }
        return categoryData;
    }
    

    /**
     * Calculates the total value and gets the label for the source node (the left side).
     * @return A Map containing a single entry: the source label and its total value.
     * @throws FileNotFoundException If the file cannot be read.
     */
    public Map<String, Double> calculateSourceData() throws FileNotFoundException {
        Map<String, Double> sourceData = new HashMap<>();
        Map<String, Double> categoryData = parseCategoryData();
        
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) scanner.nextLine(); // Skip diagram title
            String sourceLabel = "Source"; // Default label
            if (scanner.hasNextLine()) {
                 sourceLabel = scanner.nextLine(); // This is the source label, e.g., "Budget"
            }

            double totalValue = 0;
            for (double value : categoryData.values()) {
                totalValue += value;
            }
            sourceData.put(sourceLabel, totalValue);
        } catch (FileNotFoundException e) {
            // This exception should technically not be thrown here if the constructor succeeded,
            // but it's good practice to handle it.
            throw new RuntimeException(e);
        }
        return sourceData;
    }
}
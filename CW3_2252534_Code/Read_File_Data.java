/*
The file Read_File_Date.java implements the function of reading file data and display.
There are four methods in the class, and their functions are as follows:
(1)Read_File_Data() construction method: This method is used to initialize the file object and check if the file exists and is a directory.
(2)GetDifferentData() method: This method is used to scan data in a file, store different types of data in a Map object, and then return that object.
(3)CalculateTotal() method: This method uses the GetDifferentData() method to obtain data, calculates the total of the data, stores the result in a Map object, and then returns that object.
(4)main() method: Output relevant data using the previous three methods.
With the help of the four methods, we successfully read the file data, laying the foundation for generating the corresponding Sankey Diagrams.
The specific functions of the specific code can be found in the comments.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
public class Read_File_Date {

    //Output relevant data
    public static void main(String[] args) throws FileNotFoundException {
        Read_File_Date myFile = new Read_File_Date("example2.txt");
        System.out.println(myFile.GetDifferentData());
        System.out.println(myFile.CalculateTotal());
    }

    //Declare a private File object to represent a file
    private final File myFile;

    //Use a constructor to initialize the myFile attribute and throw related exceptions
    public Read_File_Date(String myFile) throws FileNotFoundException {
        this.myFile = new File(myFile);
        //First exception: File does not exist
        if(!this.myFile.exists()) {
            throw new FileNotFoundException("The input file " + myFile + " does not exist!");
        }
        //Second exception: Input is not a file
        //Note: In Java, RuntimeException and its subclasses belong to Unchecked Exception and do not need to be explicitly declared or captured in method declarations
        if(!this.myFile.isFile()) {
            throw new RuntimeException("The input " + myFile + " is not a file!");
        }
    }

    //Using the CalculateTotal method to calculate the total amount of data(at the left end of the Sankey plot)
    public Map<String, Double> CalculateTotal() throws FileNotFoundException {
        //Create a new HashMap object to store data
        //Note: HashMap does not guarantee the order of elements, it stores and accesses elements based on the hash value of the key
        Map<String, Double> total = new HashMap<>();
        //Call the GetDifferentData method to obtain data of different categories
        Map<String, Double> difData = GetDifferentData();
        try {
            //Get title
            Scanner myScanner = new Scanner(myFile);
            myScanner.nextLine();
            String title = myScanner.nextLine();
            //Calculate the total value of the title
            double totalValue = 0;
            for(double num : difData.values()) {  //Enhanced for loops
                totalValue = totalValue + num;
            }
            total.put(title, totalValue);
            //Close myScanner object
            myScanner.close();
        } catch (FileNotFoundException e) {  //Handling related exceptions
            throw new RuntimeException(e);
        }
        //Return the total object, which includes the title and totalValue
        return total;
    }

    //Using the GetDifferentData method to obtain data of different categories
    public Map<String, Double> GetDifferentData() throws FileNotFoundException {
        //Create a new LinkedHashMap object to store data
        //Note: LinkedHashMap stores and accesses elements in the order they are inserted, maintaining the order in which they are inserted
        //So we chose LinkedHashMap, which can match the data order in the file
        Map<String, Double> data = new LinkedHashMap<>();
        try {
            Scanner myScanner = new Scanner(myFile);
            //Skip the first two lines when reading file content
            myScanner.nextLine();
            myScanner.nextLine();
            //Then read the data line by line
            while (myScanner.hasNextLine()) {
                String line = myScanner.nextLine();
                //When storing data, the method performs some validation
                //If a row of data does not contain spaces, the data object will be cleared and a NumberFormatException exception will be thrown
                if(!line.contains(" ")) {
                    data.clear();
                    throw new NumberFormatException();
                }
                //Splitting a row of data into multiple strings by spaces
                String[] values = line.split(" ");
                //Store different names and amounts in the HashMap
                double amount;
                String name = "";
                for(int i = 0; i < values.length - 1; i++) {
                    name = name.concat(" ").concat(values[i]);
                }
                name = name.trim();
                amount = Double.parseDouble(values[values.length - 1]);
                //If the amount is less than or equal to 0, the data object will be cleared and a NumberFormatException exception will be thrown
                if(amount <= 0) {
                    String errorPrompt = "There is an issue with the format of the data!";
                    data.clear();
                    throw new NumberFormatException(errorPrompt);
                }
                data.put(name, amount);
            }
            //Close myScanner object
            myScanner.close();
        } catch (FileNotFoundException e) {  //Handling related exceptions
            throw new FileNotFoundException("The input file " + myFile + " does not exist!");
        } catch (NumberFormatException e) {  //Handling related exceptions
            throw new NumberFormatException("There is an issue with the format of the data!");
        }
        //Return the data object, which includes the names and amounts of different categories
        return data;
    }
}
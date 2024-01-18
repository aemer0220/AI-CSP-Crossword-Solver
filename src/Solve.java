/**Alexandra Emerson**/

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Solve {
	/*Declare variables involved in the command line.
	 * Only those with defaults are instantiated immediately*/
	private static String dFileName;
	private static String pFileName;
	private static int verbos = 0; //default is 0
	private static String varSelect = "static"; //static by default
	private static String valOrder = "static"; //static by default
	private static boolean limForCheck = false; //false by default
	
	//declare rest of variables involved in solving the CSP
	private List<String> dictData;
	private int rows;
	private int cols;
	private Object puzzMatrix[][];
	private String solvedMatrix[][];
	private CSP csp;
	private LinkedHashMap<Variable, String > result = new LinkedHashMap<Variable, String>(); //Idk
	LinkedHashMap<Variable, String> finalAssignment;
	private long dur1, dur2;
	private int numCalls;
	
	/**
	 * Main method reads in the command line options
	 * and calls the major functions of the program: openFiles,
	 * createCSP, and backTrackSearch
	 * @param args
	 */
	public static void main(String[] args) {
		Solve solve = new Solve();
		try {
			List<String> options = Arrays.asList(args); //converts args to a list so it's easier to work with
			int dOptIndex = options.indexOf("-d");
			dFileName = options.get(dOptIndex+1);
			int pOptIndex = options.indexOf("-p");
			pFileName = options.get(pOptIndex+1);
	
			if(options.contains("-v")) {
				int vOptIndex = options.indexOf("-v");
				verbos = Integer.parseInt(options.get(vOptIndex + 1));
			}
			if(options.contains("-vs")) {
				int vsOptIndex = options.indexOf("-vs");
				varSelect = options.get(vsOptIndex+1).toLowerCase();
			}
			else if(options.contains("--variable-selection")) {
				int vsOptIndex = options.indexOf("--variable-selection");
				varSelect = options.get(vsOptIndex+1).toLowerCase();
			}
			if(options.contains("-vo")) {
				int voOptIndex = options.indexOf("-vo");
				valOrder = options.get(voOptIndex+1).toLowerCase();
			}
			else if(options.contains("--value-order")) {
				int voOptIndex = options.indexOf("--value-order");
				valOrder = options.get(voOptIndex+1).toLowerCase();
			}
			if(options.contains("-lfc") || options.contains("--limited-forward-check")) {
				limForCheck = true;
			}
		
		solve.openFiles();
		solve.createCSP();
		solve.solveCSP();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** openFiles() calls sub-functions for reading each file
	 * 
	 */
	private void openFiles() {
		if (verbos >= 1) {
			System.out.println("* Reading dictionary from [" + dFileName + "]" );
			System.out.println("* Reading puzzle from [" + pFileName + "]" );
		}
		dictData = readDFile(dFileName);
		readPFile(pFileName);
		
		if (verbos >=2) {
			System.out.println("** Puzzle");
			for(int i = 0; i<puzzMatrix.length; i++) {
				for (int j = 0; j<cols; j++) {
					System.out.print(puzzMatrix[i][j] + "  ");
				}		
				System.out.println();
			}
			System.out.println();
		}			
	}

	/** The dictionary file is read line by line,
	 * each word being added to an ArrayList of Strings
	 * 
	 * @param name of the dictionary file
	 * @return an ArrayList of Strings
	 */
	private ArrayList<String> readDFile(String fn) {
		String line;
		ArrayList<String> data = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fn));
			while((line = reader.readLine())!= null) {
				data.add(line);	
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	/** the Puzzle Data file is read. The numbers for rows and columns
	 * are put into global variables, and the puzzle matrix is put into a 2d array.
	 * @param the name of the puzzle data file
	 */
	private void readPFile(String fn) {
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fn));
			line = reader.readLine(); //read first line to get ROW and COL
			Scanner scan = new Scanner(line);
			rows = Integer.parseInt(scan.next());
			cols = Integer.parseInt(scan.next());
			scan.close();
			
			puzzMatrix = new Object [rows][cols]; //will add the puzzle data into a 2d array one token at a time
			int rowCount = 0;
			while((line = reader.readLine())!= null && rowCount <= rows) {
				Scanner scan2 = new Scanner(line);
				for(int colCount = 0; colCount<cols; colCount++) {
					if(scan2.hasNextInt()) {
						puzzMatrix[rowCount][colCount] = scan2.nextInt();
					}
					else{
						puzzMatrix[rowCount][colCount] = scan2.next().charAt(0); 
					}
				}
				rowCount++;	
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** createCSP is the main function for building the csp object.
	 * It calls methods from within CSP
	 */
	private void createCSP() {
		csp = new CSP();
		csp.findVariables(puzzMatrix, cols);
		csp.findDomain(dictData);
		csp.findConstraints();
		
		if (verbos >= 1) {
			System.out.println("* CSP has " + csp.variables.size() + " variables");
			System.out.println("* CSP has " + csp.constraints.size() + " constraints");
		}
	}
	
	/** solveCSP() calls the backtracking search method which
	 * returns a result, and also calls the printOutput() method.
	 * 
	 */
	private void solveCSP() {
		dur1 = System.currentTimeMillis();
		if (verbos >= 1) {
			System.out.println("* Attempting to solve crossword puzzle...");
			System.out.println();
		}
		finalAssignment = backtrackingSearch();
		dur2 = System.currentTimeMillis();
		printOutput(); //the level 0 output. With higher verbosity levels, those print statements have already been generated
	}
	
	private void printOutput() {
		if (finalAssignment !=null) {
			System.out.println("SUCCESS! Solving took " + (dur2-dur1) + "ms (" + numCalls + " recursive calls)");
			System.out.println();
			solvedMatrix = new String[rows][cols]; //now we'll build the solved puzzle grid
				for (Map.Entry<Variable, String> entry : finalAssignment.entrySet()) {
					Variable variable = entry.getKey();
					String word = entry.getValue();
					//System.out.println("Final Assignment: "); //TEST - GOOD
					//System.out.println(variable.varId + " <-- " + word); //TEST - GOOD
					for (int i = 0; i<variable.indexes.size(); i++) {
						int[] index = variable.indexes.get(i);
						String letter = String.valueOf(word.charAt(i));
						int row = index[0];
						int col = index[1];
						solvedMatrix[row][col] = letter;
					}
				}		
				//fill the remaining null values with " "
				for (int row = 0; row < solvedMatrix.length; row++) {
					for (int col = 0; col < cols; col++) {
						if (solvedMatrix[row][col] == null) {
							solvedMatrix[row][col] = " ";
						}
					}
				}		
				for (int row = 0; row < solvedMatrix.length; row++) {
					for (int col = 0; col < cols; col++) {
						System.out.print(solvedMatrix[row][col]);
					}
					System.out.println();
				}
			}
		
		else {
			System.out.println("FAILED; Solving took " + (dur2-dur1) + "ms (" + numCalls + " recursive calls)");
			System.out.println();
		}	
	}
	
	/**
	 * backtrackingSearch includes the initial call to the recursive backtrack search.
	 * @return the complete assignment or a failure (null value)
	 */
	private LinkedHashMap<Variable, String> backtrackingSearch() {
		return backtrack(csp, csp.assignment); //assignment is initially empty
	}
	
	String spaces = "";
	
	private LinkedHashMap<Variable, String> backtrack(CSP csp, LinkedHashMap<Variable,String> assignment){
		numCalls++;
		if (verbos >=2) {
			for (int i = 0; i < numCalls; i++) {
				spaces = spaces + " ";
			}
			System.out.println(spaces + "Backtrack Call:");
		}
		if (assignmentIsComplete(assignment)) { 
			if (verbos >= 2) {
				System.out.println(spaces + "Assignment is complete!");
				System.out.println();
			}
			return assignment;
		}
		Variable var = selectUnassignedVariable(csp, assignment);
		if (verbos >=2) {
			System.out.println(spaces + "   Trying values for X" + var.varId);
		}
		ArrayList<String> legalValues = orderDomainValues(csp, var, assignment);
		for (String value : legalValues) {
			if (isConsistent(var, value, assignment)) {	
				if (verbos >= 2) {
					System.out.println(spaces + "   Assignment { X" + var.varId + " = " + value + " } is consistent");
				}
				assignment.put(var, value);
				result = backtrack(csp, assignment);
				if (result != null) return result;
				assignment.replace(var, null);	
			}
			
			else {
				if (verbos >= 2) {
					System.out.println("	Assignment { X" + var.varId + " = " + value + " } is inconsistent");
				}
			}	
		}
		
		if (verbos >= 2) {
			System.out.println(spaces + "    Failed call; backtracking...");
		}
		return null;
	}
	
	/** assignmentIsComplete() iterates through the assignment
	 * hashmap, returning false as soon as a Variable with a null
	 * value is found.
	 * 
	 * @param assignment
	 * @return true if the assignment is complete, false if not
	 */
	public boolean assignmentIsComplete(LinkedHashMap<Variable, String> assignment) {
		for (Map.Entry<Variable, String> entry : assignment.entrySet()) {
			if (entry.getValue() == null) {
				return false;
			}
		}
		return true;
	}
	
	/** is Consistent takes in a variable and its value. It goes through the 
	 * assignments list, and, picking out only the entries that have an assignment.
	 * It then iterates through the constraints, finding only those constraints which
	 * have relationships involving both the in-variable and the current assignment variable.
	 * It compares those variables' letters at the constraints' shared index, only returning false
	 * if those letters do not match. The method will return true if the assignment is empty
	 * or if no inconsistencies are found.
	 * 
	 * @param value
	 * @param assignment
	 * @return a Boolean
	 */
	public boolean isConsistent(Variable variable, String value, LinkedHashMap<Variable, String> assignment) {	
		String inVar = variable.varId;
		String inVal = value;
		
		if (assignment.isEmpty()) {
			return true;
		}
		
		for (Map.Entry<Variable, String> entry : assignment.entrySet()) {
			if (entry.getValue() != null) { //if there is an assignment
				String existingVar = entry.getKey().varId; //get that assignment's variable
				String existingVal = entry.getValue(); //get that assignment's value
				
				//check if the relationship is in the constraints
				for (Constraint c : csp.constraints) {
					String v1 = c.v1.varId;
					String v2 = c.v2.varId;
					
					if(existingVar.equals(v1)) { //if the variable in the assignment matches the first variable in this constraint
						if(inVar.equals(v2)) { //if the incoming variable is also in the constraint
							int[] sharedIndex = c.sharedIndex;
							
							//Locate the index that this shared index is located within both variables' indexes array
							int index1 = findIndex (c.v1, sharedIndex); //where that shared letter (index) is in existingVar
							int index2 = findIndex (c.v2, sharedIndex); //where that shared letter (index) is in inVar
							
							//now check if the values at those indexes, of the two variables, are equal, continue with program if they are
							if (existingVal.charAt(index1) != inVal.charAt(index2)) {
								return false; //return false if inconsistency found
							}
						}
					}
					/*I don't think the program ever gets to this else if - included it just in case*/
					else if(existingVar.equals(v2)) { //if the variable in the assignment matches the second variable in this constraint
						if(inVar.equals(v1)) { //if the incoming variable is also in the constraint
							int[] sharedIndex = c.sharedIndex;

							//Locate the index that this shared index is located within both variables' indexes array
							int index1 = findIndex (c.v1, sharedIndex); //where that shared letter (index) is in inVar
							int index2 = findIndex (c.v2, sharedIndex); //where that shared letter (index) is in existingVar
							
							//now check if the values at those indexes, of the two variables, are equal
							if (inVal.charAt(index1) != existingVal.charAt(index2)) { //if
								return false;
							}
						}
					}
				}
			}
		}
			
		return true; //every assignment is null or the value does not violate any constraints with existing assignments
	}
	
	/** findIndex takes in a variable and an index2, and returns what index
	 * that index2 (i.e. that particular letter), occurs in that variable's "word"
	 * 
	 * @param v
	 * @param sharedIndex
	 * @return the index of the variable where that letter appears
	 */
	public int findIndex(Variable v, int[] index2) {
		for (int i = 0; i<v.indexes.size(); i++) {
			int[] index = v.indexes.get(i);
			if (Arrays.equals(index, index2)) {
				return i;
			}
		}	
		return -1;
	}
	
	/**selectUnassignedVariable will return a variable from a list
	 * of unassigned variables, calculated within the method.
	 * 
	 * The order in which variables are returned depends on 
	 * the variable selection flag (varSelect)
	 * 
	 * @param csp
	 * @param assignment
	 * @return Variable object
	 */
	public Variable selectUnassignedVariable(CSP csp, LinkedHashMap<Variable, String> assignment) {
		//first filter out the unassigned variables
		ArrayList<Variable> unassignedVars = new ArrayList<Variable>();
		for (Map.Entry<Variable,String> entry: assignment.entrySet()) {
			if (entry.getValue() == null) {
				unassignedVars.add(entry.getKey());
			}
		}
		
		if(varSelect.equals("static")) { //fixed ordering, select next variable with empty value in assignments
			for (Map.Entry<Variable, String> entry : assignment.entrySet()) {
				Variable v = entry.getKey();
				if (entry.getValue() == null) return v;
			}	
			return null;	//shouldn't ever reach here
		}
		
		else if (varSelect.equals("mrv")) { //selects the variable with the fewest remaining legal values
			//iterate through the unassigned variables to find the one with the fewest legal values - ties broken "arbitrarily"
			Variable v1 = unassignedVars.get(0);
			int fewestLV = v1.legalValues.size();
			
			for (int i = 1; i<unassignedVars.size(); i++) {
				Variable v2 = unassignedVars.get(i);
				int LV2 = v2.legalValues.size();
				if (fewestLV > LV2) {
					fewestLV = LV2;
					v1 = v2;
				}
			}
			return v1;
		}
		
		else if (varSelect.equals("deg")) { //most constraining variable - selects the variable with the highest degree - ties broken "arbitrarily"
			Variable v1 = unassignedVars.get(0);
			int largestDegree = v1.degree;
			for (int i = 1; i<unassignedVars.size(); i++) {
				Variable v2 = unassignedVars.get(i);
				int d2 = v2.degree;
				if (largestDegree < d2) {
					largestDegree = d2;
					v1 = v2;
				}
			}
			return v1;
		}
		
		else if (varSelect.equals("mrv+deg")) {
			Variable v1 = unassignedVars.get(0);
			int fewestLV = v1.legalValues.size();
			
			for (int i = 1; i<unassignedVars.size(); i++) {
				Variable v2 = unassignedVars.get(i);
				int LV2 = v2.legalValues.size();
				
				if (fewestLV == LV2) {
					int d1 = v1.degree;
					int d2 = v2.degree;
					
					if (d1 < d2) {
						fewestLV = LV2;
						v1 = v2;
					}
				}			
				else if (fewestLV > LV2) {
					fewestLV = LV2;
					v1 = v2;
				}
			}
			return v1;
		}	
		return null;	
	}
	
	/** orderDomainValues takes a previously selected variable,
	 * and iterates through its possible values. The values
	 * are ordered depending on the valOrder variable.
	 * 
	 * @param csp
	 * @param var
	 * @param assignment
	 * @return
	 */
	private ArrayList<String> orderDomainValues(CSP csp, Variable var, LinkedHashMap<Variable, String> assignment) {
		if (valOrder.equals("static")) { //try each value in the order in which they were inserted into the legal values
			return var.legalValues;
		}	
		else { //valOrder.equals("lcv");
			
		}
		
		return null;
	}

}

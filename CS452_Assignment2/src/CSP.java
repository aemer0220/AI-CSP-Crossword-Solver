/**Alexandra Emerson**/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CSP {
	public ArrayList<Variable> variables = new ArrayList<Variable>();
	public LinkedHashMap<Variable, ArrayList<String>> domains = new LinkedHashMap<Variable, ArrayList<String>>();
	public ArrayList<Constraint> constraints = new ArrayList<Constraint>();
	public LinkedHashMap<Variable, String> assignment = new LinkedHashMap<Variable, String>();
	
	public CSP() {}
	
	public void findVariables(Object[][] puzzMatrix, int maxCols) {
		for(int row = 0; row<puzzMatrix.length; row++) {
				for (int col = 0; col<maxCols; col++) {
					String c = puzzMatrix[row][col].toString();
					if (c.matches(".*\\d.*")) { //if the object contains digits
						/*The below Boolean values let us know how many variables to create for the one number*/
						Boolean wordAcross = checkAcross(puzzMatrix, row, col);
						Boolean wordDown = checkDown(puzzMatrix, row, col);
					
						if (wordAcross) {
							Variable v = new Variable(Integer.parseInt(c), row, col);
							v.direction = "across";
							v.varId = v.varNumber + "" + "a";
							v.wordLength = findVarLengthAcross(v, puzzMatrix, maxCols); //finds the length of the variable
							findSpanAcross(v, puzzMatrix); //calculates each index of each letter
							variables.add(v);
							assignment.put(v, null);
						}
						if(wordDown) {
							Variable v1 = new Variable(Integer.parseInt(c), row, col);
							v1.direction = "down";
							v1.varId = v1.varNumber + "" + "d";
							v1.wordLength = findVarLengthDown(v1, puzzMatrix); //finds the length of the variable
							findSpanDown(v1, puzzMatrix); //calculates each index of each letter
							variables.add(v1);
							assignment.put(v1, null);
						}
					}
				}	
			}	
	}
	
	/** Checks if the variable moves horizontally
	 * If the variable in the first column OR the
	 * cell to the left is '#', then it will return true.
	 * 
	 * @param puzzMatrix
	 * @param row
	 * @param col
	 * @return a Boolean value
	 */
	public Boolean checkAcross(Object[][] puzzMatrix, int row, int col) {
		if (col == 0 || puzzMatrix[row][col-1].toString().equals("#")) { 
			return true;
		}
		
		return false;
	}
	
	/** Checks if the variable moves downward
	 * If the variable is in the first row OR the 
	 * cell above is '#', then it will return true.
	 * 
	 * @param puzzMatrix
	 * @param row
	 * @param col
	 * @return a Boolean value
	 */
	public Boolean checkDown(Object[][] puzzMatrix, int row, int col) {
		if (row == 0 || puzzMatrix[row-1][col].toString().equals("#")) {
			return true;
		}	
		return false;
	}
	
	/** findVarLengthAcross computes the length of the across variable,
	 * using its position within the muzzle matrix
	 * 
	 * @param v
	 * @param puzzMatrix
	 * @param maxCols
	 * @return int (wordLength)
	 */
	private int findVarLengthAcross(Variable v, Object[][] puzzMatrix, int maxCols) {
		int letCount = 0;
		int row = v.startIndex[0];
		int col = v.startIndex[1];
		//continue adding to letCount until we reach the end of the row or hit a "#"*/
		while(col < maxCols && (!puzzMatrix[row][col].toString().equals("#"))) {
			letCount++;
			col++;
		}
		return letCount;
	}
	
	private int findVarLengthDown(Variable v, Object[][] puzzMatrix) {
		int letCount = 0;
		int row = v.startIndex[0];
		int col = v.startIndex[1];
		
		while(row < puzzMatrix.length && (!puzzMatrix[row][col].toString().equals("#"))) {
			letCount++;
			row++;
		}
		return letCount;
	}
	
	/** findDomain iterates through each variable in the CSP,
	 * and compares the length of each word in the dictionary data
	 * to each variable's word length. When they match, that word is
	 * added to variable's domain, an ArrayList of Strings.
	 * 
	 * The variable's starting legal values are also populated
	 * into an ArrayList of Strings that will eventually be reduced by
	 * the backtracking search method.
	 * 
	 * @param dictData
	 */
	public void findDomain(List<String> dictData) {
		for (Variable v : variables) {
			for (String word : dictData) {
				if(v.wordLength == word.length()) {
					v.domain.add(word);
					v.legalValues.add(word);
				}
			}
			
			domains.put(v, v.domain);
		}	
	}
	
	
	/** findSpanAcross() turns each letter of each variable into an index
	 * of the puzzle matrix, and stores it in an ArrayList of arrays.
	 * Indexes were my way of finding the constraints.
	 * 
	 * @param v
	 * @param puzzMatrix
	 */
	public void findSpanAcross(Variable v, Object[][] puzzMatrix) {
		int wordLength = v.wordLength;
		v.span = new Object[wordLength];
		v.indexes = new ArrayList<int[]>();
		int row = v.startIndex[0];
		int col = v.startIndex[1];
		
		for(int i = 0; i < wordLength; i++) {
			v.span[i] = puzzMatrix[row][col];
			int [] ins = {row, col};
			v.indexes.add(ins);
			col++;
		}
	}
	
	/** findSpanDown() turns each letter of each variable into an index
	 * of the puzzle matrix, and stores it in an ArrayList of arrays.
	 * Indexes were my way of finding the constraints.
	 * 
	 * @param v
	 * @param puzzMatrix
	 */
	public void findSpanDown(Variable v, Object[][] puzzMatrix) {
		int wordLength = v.wordLength;
		v.span = new Object[wordLength];
		v.indexes = new ArrayList<int[]>();
		int row = v.startIndex[0];
		int col = v.startIndex[1];
		
		for(int i = 0; i < wordLength; i++) {
			v.span[i] = puzzMatrix[row][col];
			int [] ins = {row, col};
			v.indexes.add(ins);
			row++;
		}
	}
	
	
	/** findConstraints iterates through each variable's indexes,
	 * then it compares the indexes to the succeeding variables'
	 * indexes, creating a constraint if it finds a match.
	 * 
	 * Though seemingly inefficient, this method is accurate
	 * and avoids duplicates.
	 */
	public void findConstraints() {
		for (int i = 0; i<variables.size(); i++) {
			Variable v1 = variables.get(i);
			for (int [] v1Index : v1.indexes) {
				int j = i+1;
				while(j<variables.size()) { //check the subsequent variables
					Variable v2 = variables.get(j);
					for (int [] v2Index : v2.indexes) {
						if (Arrays.equals(v1Index, v2Index)) {
						 	v1.degree++;
						 	v2.degree++;
							Constraint c = new Constraint(v1, v2, v1Index);
							constraints.add(c);
						}
					}		
					j++;
				}
			}					
		}
	}
}

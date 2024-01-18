/**Alexandra Emerson**/
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Variable {
	public String varId; //composed of the varNumber and the first letter of the direction
	public int varNumber;
	public int wordLength;
	public String direction;
	public int degree;
	int [] startIndex;
	public ArrayList<String> domain;
	public ArrayList<String> legalValues;
	public Object[] span; //used for testing purposes
	public ArrayList<int[]> indexes;
	
	public Variable(int c, int row, int col) {
		varNumber = c;
		startIndex = new int[] {row, col};
		domain = new ArrayList<String>();
		legalValues = new ArrayList<String>();
	}
	
}

/**Alexandra Emerson**/

public class Constraint {
	Variable v1;
	Variable v2;
	int[] sharedIndex;
	
	public Constraint(Variable v1, Variable v2, int[] index) {
		this.v1 = v1;
		this.v2 = v2;
		sharedIndex = index;
	}
}

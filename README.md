# Crossword-Solver
## Alexandra Emerson
This program reads in two text files: One is a dictionary containing a list of allowable words (variables) for the puzzle, and the other is a text file descirbing the structure of the puzzle matrix.
The program then formulates the puzzle into a Constraint Satisfaction Problem, and finds a solution (if one exists) to the crossword, or returns a failure if there is not a solution.

### How to run from the Command Prompt (terminal)

1. Download the project folder
2. Open your Command Prompt (terminal)
3. cd to project's src file containing the .java files
4. type (without quotes) 'javac Search.java
5. Next, type (without quotes) 'java Search' followed by your command line arguments. See "Allowed Command Line Arguments" for the required and optional arguments

### Allowed Command Line Arguments
#### The -d and -f options (with arguments) are required; the remaining ones are optional and will be set with defaults if not provided
* -d <FILENAME>: Reads dictionary data from the text file named <FILENAME> (specified as a String)
* -p <FILENAME>: Reads puzzle data from the text file named <FILENAME> (specified as a
String)
* -v <INTEGER>: Specifies a verbosity level, indicating how much output the program should
produce (Default is 0 if -v is not provided)
* -vs <STRING> or --variable-selection <STRING>: Specifies how variables should be or-
dered for variable selection in backtracking; <STRING> should be one of:
  * static: Use fixed ordering
  * mrv: Select variables using the minimum remaining values (most constrained variable) heuristic. Ties are broken arbitrarily
  * deg: Select variables using the degree (most constraining) heuristic. Ties are broken arbitrarily.
  * mrv+degree: Select variables using the minimum remaining values heuristic. Ties are broken using the degree heuristic.
* -vo <STRING> or --value-order <STRING>: Specifies the order in which a variable’s values
should be iterated; <STRING> should be one of:
  * static: Use fixed ordering )default if -vo or --value-order is not provided)
  * lcv: Order values using the least constraining value heuristic.
* -lfc or --limited-forward-check: Indicates that limited forward checking should be ap-
plied when checking constraints for consistency;

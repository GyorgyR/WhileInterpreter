/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Data structure for representing the program as statements.
    This is basically going to be a collection of statements.
*/

public class AST {

    public final Statements theProgram;

    //constructor
    public AST(Token[] tokens) {
        theProgram = new Statements(tokens);
        //System.out.println(theProgram);
    } //constructor

    @Override
    public String toString() {
        return theProgram.toString();
    }
} //AST
/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Data structure for representing the print statement.
*/

public class PrintStatement implements Statement {
    public final AExpression exprToPrint;

    public PrintStatement(AExpression tokens) {
        exprToPrint = tokens;
    } //constructor

    @Override
    public String toString() {
        return "PrintStatement["+exprToPrint.toString()+"]";
    } //toString
} //PrintStatement
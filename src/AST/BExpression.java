/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Data structure for representing a boolean expression.
*/


public class BExpression implements Expression {

    public final Token[] expr;

    public BExpression(Token[] tokens) {
        //TODO validity check
        expr = tokens;
    } //constructor

    @Override
    public String toString() {
        String exprString = "";

        for(Token item : expr)
            exprString += item.tokenValue + " ";

        exprString = exprString.substring(0,exprString.length()-1);

        return "BExpression (\""+exprString+"\")";
    } //toString
} //BExpression
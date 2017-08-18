    /*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Data structure for representing an arithmetic expression.
*/

public class AExpression implements Expression {

    private Token[] expr;

    //constructor
    public AExpression(Token[] tokens) {
        expr = tokens;
    }

    @Override
    public String toString() {
        String stringtoReturn = "";

        for(Token item : expr) {
            stringtoReturn += item.tokenValue + " ";
        }

        stringtoReturn = stringtoReturn.substring(0,stringtoReturn.length()-1);

        return "AExpression (\""+stringtoReturn+"\")";
    } //toString
} //AExpression
    /*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Data structure for representing a while loop.
*/

public class WhileLoop implements Statement {

    public final BExpression condition;
    public final Statements loopBody;

    //constructor
    public WhileLoop(BExpression _condition, Statements _body) {
        condition = _condition;
        loopBody = _body;
    } //constructor

    @Override
    public String toString() {
        return "WhileLoop[Condition("+condition.toString()
            +"),Body("+loopBody.toString()+")]";
    } //toString
} //WhileLoop
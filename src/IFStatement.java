/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Data structure for representing the if statement.
*/

import java.util.*;

public class IFStatement implements Statement {

    public final BExpression condition;
    public final Statements body;
    public final Statements elseBody;

    //constructor
    public IFStatement(Token[] condTokens, Token[] bodyTokens, Token[] elseTokens) {
        condition = new BExpression(condTokens);
        body = new Statements(bodyTokens);
        elseBody = new Statements(elseTokens);
    } //constructor

    @Override
    public String toString() {
        return "IFStatement[Condition("+condition.toString()+"),body("+body.toString()
            +"), else("+elseBody.toString()+")]";
    }
} //IFStatement
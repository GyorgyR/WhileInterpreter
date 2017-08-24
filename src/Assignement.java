/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Data structure for representing an assignement.
*/

public class Assignement implements Statement{
    public final Token name;
    public final AExpression value;

    //constructor
    public Assignement(Token _name, AExpression _value) {
        //TODO validity check
        name = _name;
        value = _value;
    } //constructor

    @Override
    public String toString(){
        return "Assignement["+name+","+value+"]";
    } //toString
}//Assignement
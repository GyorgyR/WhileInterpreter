/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.17.
--------------------------------------------------------------------------------
    Descrpition: Data structure for tokens.
*/

public class Token {

    private String tokenValue;
    private TokenType tokenType;

    //constructor
    public Token (String _tokenValue, TokenType _tokenType) {
        //assignements
        tokenValue = _tokenValue;
        tokenType = _tokenType;
    }

    @Override
    public String toString() {
        return "(\""+tokenValue+"\",\""+tokenType.name()+"\")";
    }

} //Tokens
